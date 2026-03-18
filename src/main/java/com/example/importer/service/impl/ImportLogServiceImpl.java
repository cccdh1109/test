package com.example.importer.service.impl;

import com.example.importer.domain.ImportJobConfig;
import com.example.importer.domain.ImportJobLog;
import com.example.importer.dto.ImportExecutionResult;
import com.example.importer.mapper.ImportJobLogMapper;
import com.example.importer.service.ImportLogService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImportLogServiceImpl implements ImportLogService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final ImportJobLogMapper importJobLogMapper;

    @Override
    public ImportJobLog start(ImportJobConfig config, String triggerType) {
        ImportJobLog logRecord = new ImportJobLog();
        logRecord.setJobId(config.getId());
        logRecord.setJobCode(config.getJobCode());
        logRecord.setTriggerType(triggerType);
        logRecord.setStatus("RUNNING");
        logRecord.setMessage("任务开始执行");
        logRecord.setStartedAt(now());
        importJobLogMapper.insert(logRecord);
        log.info("[IMPORT][{}] 开始执行，触发方式={}, 索引={}, 文件={}", config.getJobCode(), triggerType,
            config.getIndexName(), config.getOssObjectKey());
        return logRecord;
    }

    @Override
    public void success(ImportJobLog logRecord, ImportExecutionResult result) {
        logRecord.setStatus("SUCCESS");
        logRecord.setMessage(result.getMessage());
        logRecord.setTotalLines(result.getTotalLines());
        logRecord.setSuccessLines(result.getSuccessLines());
        logRecord.setFailedLines(result.getFailedLines());
        logRecord.setDurationMs(result.getDurationMs());
        logRecord.setFinishedAt(now());
        importJobLogMapper.updateStatus(logRecord);
        log.info("[IMPORT][{}] 执行成功，总行数={}, 成功={}, 失败={}, 耗时={}ms, 信息={}", logRecord.getJobCode(),
            result.getTotalLines(), result.getSuccessLines(), result.getFailedLines(), result.getDurationMs(),
            result.getMessage());
    }

    @Override
    public void fail(ImportJobLog logRecord, Exception exception, long durationMs) {
        logRecord.setStatus("FAILED");
        logRecord.setMessage(ExceptionUtils.getRootCauseMessage(exception));
        logRecord.setDurationMs(durationMs);
        logRecord.setFinishedAt(now());
        importJobLogMapper.updateStatus(logRecord);
        log.error("[IMPORT][{}] 执行失败，耗时={}ms", logRecord.getJobCode(), durationMs, exception);
    }

    private String now() {
        return LocalDateTime.now().format(FORMATTER);
    }
}
