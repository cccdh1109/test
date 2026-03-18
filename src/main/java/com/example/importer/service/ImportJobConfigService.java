package com.example.importer.service;

import com.example.importer.domain.ImportJobConfig;
import java.util.List;

public interface ImportJobConfigService {
    List<ImportJobConfig> findEnabledJobs();

    List<ImportJobConfig> findAllJobs();

    ImportJobConfig findByJobCode(String jobCode);
}
