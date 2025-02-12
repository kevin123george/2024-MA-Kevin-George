package com.cattleDB.WatcherPatternService.cronjobs.cronjobs;

import com.cattleDB.WatcherPatternService.models.CronJobConfigEntity;
import com.cattleDB.WatcherPatternService.models.CronJobLog;
import com.cattleDB.WatcherPatternService.repository.CronJobConfigRepository;
import com.cattleDB.WatcherPatternService.repository.CronJobLogRepository;
import com.cattleDB.WatcherPatternService.service.PositionAnalysisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

@Component
public class DataSync {

    private static final Logger logger = LoggerFactory.getLogger(DataSync.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;

    private final PositionAnalysisService positionAnalysisService;
    private final CronJobConfigRepository cronJobConfigRepository;
    private final CronJobLogRepository cronJobLogRepository;

    public DataSync(PositionAnalysisService positionAnalysisService, CronJobConfigRepository cronJobConfigRepository, CronJobLogRepository cronJobLogRepository) {
        this.positionAnalysisService = positionAnalysisService;
        this.cronJobConfigRepository = cronJobConfigRepository;
        this.cronJobLogRepository = cronJobLogRepository;
    }


    private boolean isJobEnabled(String jobName) {
        CronJobConfigEntity config = cronJobConfigRepository.findByJobName(jobName);
        return config != null && config.isEnabled(); // Check if the job is enabled
    }

    private void logJobExecution(String jobName, Runnable job) {
        Instant startTime = Instant.now();
        CronJobLog log = new CronJobLog();
        log.setJobName(jobName);
        log.setStartTime(formatter.format(startTime)); // Format start time
        log.setStatus("STARTED");
        cronJobLogRepository.save(log);

        logger.info("Cron job '{}' started at {}", jobName, log.getStartTime());

        try {
            job.run();
            Instant endTime = Instant.now();
            log.setEndTime(formatter.format(endTime)); // Format end time
            log.setDurationMs(endTime.toEpochMilli() - startTime.toEpochMilli());
            log.setStatus("COMPLETED");
            logger.info("Cron job '{}' completed at {}. Duration: {} ms", jobName, log.getEndTime(), log.getDurationMs());
        } catch (Exception e) {
            log.setStatus("FAILED");
            log.setErrorMessage(e.getMessage());
            logger.error("Cron job '{}' failed. Error: {}", jobName, e.getMessage(), e);
        }

        cronJobLogRepository.save(log);
    }

    @Scheduled(cron = "0 */1 * * * *")
    public void analyzePositions() {
        if (true) {
            logJobExecution("positionAnalysis", () -> {
                logger.info("Starting position analysis...");
                positionAnalysisService.analyzePositions();
                logger.info("Position analysis completed.");
            });
        } else {
            logger.info("positionAnalysis job is disabled.");
        }
    }
}
