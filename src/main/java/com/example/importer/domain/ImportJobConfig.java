package com.example.importer.domain;

import lombok.Data;

@Data
public class ImportJobConfig {
    private Long id;
    private String jobCode;
    private String jobName;
    private String cronExpression;
    private Integer enabled;
    private String ossObjectKey;
    private String fileSuffix;
    private String delimiter;
    private String indexName;
    private String fieldMappings;
    private String handlerType;
    private Integer batchSize;
    private String description;
    private String createdAt;
    private String updatedAt;
}
