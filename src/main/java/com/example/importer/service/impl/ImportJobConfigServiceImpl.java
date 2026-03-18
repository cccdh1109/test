package com.example.importer.service.impl;

import com.example.importer.domain.ImportJobConfig;
import com.example.importer.mapper.ImportJobConfigMapper;
import com.example.importer.service.ImportJobConfigService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImportJobConfigServiceImpl implements ImportJobConfigService {

    private final ImportJobConfigMapper importJobConfigMapper;

    @Override
    public List<ImportJobConfig> findEnabledJobs() {
        return importJobConfigMapper.findAllEnabled();
    }

    @Override
    public List<ImportJobConfig> findAllJobs() {
        return importJobConfigMapper.findAll();
    }

    @Override
    public ImportJobConfig findByJobCode(String jobCode) {
        return importJobConfigMapper.findByJobCode(jobCode);
    }
}
