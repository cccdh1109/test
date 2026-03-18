package com.example.importer.service.impl;

import com.example.importer.domain.ImportJobConfig;
import com.example.importer.service.DynamicImportScheduleService;
import com.example.importer.service.ImportJobConfigService;
import com.example.importer.service.ImportOrchestrator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DynamicImportScheduleServiceImpl implements DynamicImportScheduleService {

    private final ImportJobConfigService importJobConfigService;
    private final ImportOrchestrator importOrchestrator;
    private final Executor importTaskExecutor;
    private final Map<String, java.util.concurrent.ScheduledFuture<?>> futures = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        refreshSchedules();
    }

    @Override
    public synchronized void refreshSchedules() {
        futures.values().forEach(future -> future.cancel(false));
        futures.clear();
        List<ImportJobConfig> jobs = importJobConfigService.findEnabledJobs();
        TaskScheduler scheduler = (TaskScheduler) importTaskExecutor;
        for (ImportJobConfig job : jobs) {
            futures.put(job.getJobCode(), scheduler.schedule(
                () -> importOrchestrator.execute(job, "CRON"),
                new CronTrigger(job.getCronExpression())));
            log.info("[SCHEDULE] 注册任务成功，jobCode={}, cron={}, handler={}", job.getJobCode(), job.getCronExpression(), job.getHandlerType());
        }
        log.info("[SCHEDULE] 动态任务刷新完成，总任务数={}", jobs.size());
    }
}
