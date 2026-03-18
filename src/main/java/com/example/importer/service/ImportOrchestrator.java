package com.example.importer.service;

import com.example.importer.domain.ImportJobConfig;
import com.example.importer.dto.ImportExecutionResult;

public interface ImportOrchestrator {
    ImportExecutionResult execute(ImportJobConfig config, String triggerType);
}
