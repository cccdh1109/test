package com.example.importer.support;

import com.example.importer.domain.ImportJobConfig;
import com.example.importer.spi.FileImportHandler;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class DefaultFileImportHandler implements FileImportHandler {

    public static final String TYPE = "default";

    @Override
    public String getHandlerType() {
        return TYPE;
    }

    @Override
    public Map<String, Object> convert(ImportJobConfig config, int lineNumber, String[] headers, String[] values) {
        Map<String, Object> document = new LinkedHashMap<>();
        for (int i = 0; i < headers.length; i++) {
            document.put(headers[i], i < values.length ? values[i] : null);
        }
        document.put("_lineNumber", lineNumber);
        document.put("_jobCode", config.getJobCode());
        return document;
    }
}
