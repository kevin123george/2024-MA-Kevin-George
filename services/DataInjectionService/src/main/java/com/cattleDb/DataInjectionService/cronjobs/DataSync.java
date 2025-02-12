package com.cattleDb.DataInjectionService.cronjobs;

import com.cattleDb.DataInjectionService.models.CronJobConfigEntity;
import com.cattleDb.DataInjectionService.models.CronJobLog;
import com.cattleDb.DataInjectionService.repository.CredentialsRepository;
import com.cattleDb.DataInjectionService.repository.CronJobConfigRepository;
import com.cattleDb.DataInjectionService.repository.CronJobLogRepository;
import com.cattleDb.DataInjectionService.service.PositionService;
import com.cattleDb.DataInjectionService.models.Credentials;

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
    private String authorization;
    private final CronJobLogRepository cronJobLogRepository;
    private final PositionService positionService;
    private final CronJobConfigRepository cronJobConfigRepository;
    private final CredentialsRepository credentialsRepository;

    public DataSync(CronJobLogRepository cronJobLogRepository, PositionService positionService, CronJobConfigRepository cronJobConfigRepository, CredentialsRepository credentialsRepository) {
        this.cronJobLogRepository = cronJobLogRepository;
        this.positionService = positionService;
        this.cronJobConfigRepository = cronJobConfigRepository;
        this.credentialsRepository = credentialsRepository;
    }


    private String loadAuthorization() {
        return credentialsRepository.findAll().stream()
                .findFirst()
                .map(Credentials::getSafectoryAPICred)
                .orElse("");
    }

    private String getAuthorization() {
        if (authorization == null) {
            authorization = loadAuthorization();
        }

        return authorization;
    }


    @Scheduled(fixedRateString = "#{@cronJobConfigRepository.findByJobName('syncPositions')?.fixedRate ?: '100000'}")
    public void syncPositions() {
        if (true) {
//        if (isJobEnabled("syncPositions")) {
            logJobExecution("syncPositions", () -> {
                logger.info("Starting position synchronization...");
                positionService.saveFetchedPositions(getAuthorization());
                logger.info("Position synchronization completed.");
            });
        } else {
            logger.info("syncPositions job is disabled.");
        }
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

}
