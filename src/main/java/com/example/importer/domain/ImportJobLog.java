package com.example.importer.domain;

import lombok.Data;

@Data
public class ImportJobLog {
    private Long id;
    private Long jobId;
    private String jobCode;
    private String triggerType;
    private String status;
    private String message;
    private Integer totalLines;
    private Integer successLines;
    private Integer failedLines;
    private Long durationMs;
    private String startedAt;
    private String finishedAt;
}
