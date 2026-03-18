package com.example.importer.service;

import com.example.importer.domain.ImportJobConfig;
import com.example.importer.domain.ImportJobLog;
import com.example.importer.dto.ImportExecutionResult;

public interface ImportLogService {
    ImportJobLog start(ImportJobConfig config, String triggerType);

    void success(ImportJobLog log, ImportExecutionResult result);

    void fail(ImportJobLog log, Exception exception, long durationMs);
}
