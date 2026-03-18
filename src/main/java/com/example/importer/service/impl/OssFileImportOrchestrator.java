package com.example.importer.service.impl;

import com.aliyun.oss.OSS;
import com.example.importer.config.EsProperties;
import com.example.importer.config.OssProperties;
import com.example.importer.domain.ImportJobConfig;
import com.example.importer.domain.ImportJobLog;
import com.example.importer.dto.ImportExecutionResult;
import com.example.importer.service.ImportLogService;
import com.example.importer.service.ImportOrchestrator;
import com.example.importer.spi.FileImportHandler;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class OssFileImportOrchestrator implements ImportOrchestrator {

    private final OSS ossClient;
    private final RestHighLevelClient client;
    private final RequestOptions requestOptions;
    private final ImportLogService importLogService;
    private final EsProperties esProperties;
    private final OssProperties ossProperties;
    private final List<FileImportHandler> handlers;

    @Override
    public ImportExecutionResult execute(ImportJobConfig config, String triggerType) {
        long start = System.currentTimeMillis();
        ImportJobLog logRecord = importLogService.start(config, triggerType);
        try {
            validate(config);
            FileImportHandler handler = getHandler(config.getHandlerType());
            handler.preProcess(config);
            List<Map<String, Object>> buffer = new ArrayList<>();
            int total = 0;
            int success = 0;
            int failed = 0;
            String[] headers = parseHeaders(config.getFieldMappings());
            int batchSize = config.getBatchSize() == null || config.getBatchSize() <= 0 ? esProperties.getBulkActions() : config.getBatchSize();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                ossClient.getObject(ossProperties.getBucketName(), config.getOssObjectKey()).getObjectContent(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    total++;
                    if (StringUtils.isBlank(line)) {
                        log.warn("[IMPORT][{}] 第{}行为空，已跳过", config.getJobCode(), total);
                        continue;
                    }
                    try {
                        String[] values = line.split(java.util.regex.Pattern.quote(config.getDelimiter()), -1);
                        buffer.add(handler.convert(config, total, headers, values));
                        success++;
                        if (buffer.size() >= batchSize) {
                            flush(config, buffer);
                        }
                    } catch (Exception ex) {
                        failed++;
                        success--;
                        log.error("[IMPORT][{}] 第{}行处理失败，内容={}", config.getJobCode(), total, line, ex);
                    }
                }
            }
            if (!CollectionUtils.isEmpty(buffer)) {
                flush(config, buffer);
            }
            handler.postProcess(config, total, success, failed);
            ImportExecutionResult result = ImportExecutionResult.builder()
                .totalLines(total)
                .successLines(success)
                .failedLines(failed)
                .durationMs(System.currentTimeMillis() - start)
                .message("导入完成")
                .build();
            importLogService.success(logRecord, result);
            return result;
        } catch (Exception ex) {
            long duration = System.currentTimeMillis() - start;
            importLogService.fail(logRecord, ex, duration);
            throw new IllegalStateException("导入失败, jobCode=" + config.getJobCode(), ex);
        }
    }

    private void flush(ImportJobConfig config, List<Map<String, Object>> buffer) throws Exception {
        BulkRequest request = new BulkRequest();
        for (Map<String, Object> item : buffer) {
            request.add(new IndexRequest(config.getIndexName()).source(item, XContentType.JSON));
        }
        BulkResponse response = client.bulk(request, requestOptions);
        if (response.hasFailures()) {
            throw new IllegalStateException("ES批量写入存在失败: " + response.buildFailureMessage());
        }
        log.info("[IMPORT][{}] 已写入ES批次，索引={}, 批次数量={}", config.getJobCode(), config.getIndexName(), buffer.size());
        buffer.clear();
    }

    private void validate(ImportJobConfig config) {
        if (StringUtils.isBlank(config.getOssObjectKey()) || StringUtils.isBlank(config.getIndexName())
            || StringUtils.isBlank(config.getDelimiter()) || StringUtils.isBlank(config.getFieldMappings())) {
            throw new IllegalArgumentException("任务配置缺少必要字段: ossObjectKey/indexName/delimiter/fieldMappings");
        }
        if (!(StringUtils.endsWithIgnoreCase(config.getOssObjectKey(), ".dat")
            || StringUtils.endsWithIgnoreCase(config.getOssObjectKey(), ".txt"))) {
            throw new IllegalArgumentException("仅支持.dat或.txt文件: " + config.getOssObjectKey());
        }
    }

    private String[] parseHeaders(String fieldMappings) {
        return java.util.Arrays.stream(fieldMappings.split(","))
            .map(String::trim)
            .filter(StringUtils::isNotBlank)
            .toArray(String[]::new);
    }

    private FileImportHandler getHandler(String handlerType) {
        Map<String, FileImportHandler> handlerMap = handlers.stream()
            .collect(Collectors.toMap(FileImportHandler::getHandlerType, Function.identity(), (a, b) -> a));
        return handlerMap.getOrDefault(StringUtils.defaultIfBlank(handlerType, "default"), handlerMap.get("default"));
    }
}
