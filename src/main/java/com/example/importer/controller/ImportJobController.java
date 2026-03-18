package com.example.importer.controller;

import com.example.importer.domain.ImportJobConfig;
import com.example.importer.dto.ImportExecutionResult;
import com.example.importer.service.DynamicImportScheduleService;
import com.example.importer.service.ImportJobConfigService;
import com.example.importer.service.ImportOrchestrator;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/import/jobs")
@RequiredArgsConstructor
public class ImportJobController {

    private final ImportJobConfigService importJobConfigService;
    private final ImportOrchestrator importOrchestrator;
    private final DynamicImportScheduleService dynamicImportScheduleService;

    @GetMapping
    public List<ImportJobConfig> list() {
        return importJobConfigService.findAllJobs();
    }

    @PostMapping("/{jobCode}/run")
    public ImportExecutionResult run(@PathVariable String jobCode) {
        ImportJobConfig config = Objects.requireNonNull(importJobConfigService.findByJobCode(jobCode), "任务不存在: " + jobCode);
        return importOrchestrator.execute(config, "MANUAL");
    }

    @PostMapping("/refresh")
    public String refreshSchedule() {
        dynamicImportScheduleService.refreshSchedules();
        return "OK";
    }
}
