package com.example.importer.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImportExecutionResult {
    private int totalLines;
    private int successLines;
    private int failedLines;
    private long durationMs;
    private String message;
}
