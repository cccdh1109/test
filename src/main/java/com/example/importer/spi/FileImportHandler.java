package com.example.importer.spi;

import com.example.importer.domain.ImportJobConfig;
import java.util.Map;

public interface FileImportHandler {

    String getHandlerType();

    default void preProcess(ImportJobConfig config) {
    }

    Map<String, Object> convert(ImportJobConfig config, int lineNumber, String[] headers, String[] values);

    default void postProcess(ImportJobConfig config, int totalLines, int successLines, int failedLines) {
    }
}
