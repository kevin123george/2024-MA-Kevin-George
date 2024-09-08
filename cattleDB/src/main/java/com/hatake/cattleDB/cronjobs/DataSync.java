package com.hatake.cattleDB.cronjobs;

import com.hatake.cattleDB.models.CronJobConfigEntity;
import com.hatake.cattleDB.models.CronJobLog;
import com.hatake.cattleDB.repository.CronJobConfigRepository;
import com.hatake.cattleDB.repository.CronJobLogRepository;
import com.hatake.cattleDB.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

@Component
public class DataSync {

    private static final Logger logger = LoggerFactory.getLogger(DataSync.class);

    @Autowired
    private RouteService routeService;

    @Autowired
    private SummaryService summaryService;

    @Autowired
    private EventService eventService;

    @Autowired
    private BeaconService beaconService;

    @Autowired
    private CronJobLogRepository cronJobLogRepository;

    @Autowired
    private PositionService positionService;

    @Autowired
    private CronJobConfigRepository cronJobConfigRepository;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;

    String authorization = "Basic a2V2aW4uZ2VvcmdlQHN0dWQudW5pLWJhbWJlcmcuZGU6QmxhY2tiaXJkMTIzIQ==";

    @Scheduled(cron = "#{@cronJobConfigRepository.findByJobName('syncEvents')?.cronExpression ?: '0 */10 * * * *'}")
    public void syncEvents() {
        if (isJobEnabled("syncEvents")) {
            logJobExecution("syncEvents", () -> {
                logger.info("Starting event synchronization...");
                eventService.fetchEvents(authorization);
                logger.info("Event synchronization completed.");
            });
        } else {
            logger.info("syncEvents job is disabled.");
        }
    }

    @Scheduled(cron = "#{@cronJobConfigRepository.findByJobName('syncRoutes')?.cronExpression ?: '0 0 */3 * * *'}")
    public void syncRoutes() {
        if (isJobEnabled("syncRoutes")) {
            logJobExecution("syncRoutes", () -> {
                logger.info("Starting route synchronization...");
                routeService.fetchRoutes(authorization);
                logger.info("Route synchronization completed.");
            });
        } else {
            logger.info("syncRoutes job is disabled.");
        }
    }

    @Scheduled(cron = "#{@cronJobConfigRepository.findByJobName('syncSummary')?.cronExpression ?: '0 */10 * * * *'}")
    public void syncSummary() {
        if (isJobEnabled("syncSummary")) {
            logJobExecution("syncSummary", () -> {
                logger.info("Starting summary synchronization...");
                summaryService.fetchSummary(authorization);
                logger.info("Summary synchronization completed.");
            });
        } else {
            logger.info("syncSummary job is disabled.");
        }
    }

    @Scheduled(cron = "#{@cronJobConfigRepository.findByJobName('syncBeacons')?.cronExpression ?: '0 */1 * * * *'}")
    public void syncBeacons() {
        if (isJobEnabled("syncBeacons")) {
            logJobExecution("syncBeacons", () -> {
                logger.info("Starting beacon synchronization...");
                beaconService.fetchAndSaveBeacons(authorization);
                logger.info("Beacon synchronization completed.");
            });
        } else {
            logger.info("syncBeacons job is disabled.");
        }
    }

    @Scheduled(fixedRateString = "#{@cronJobConfigRepository.findByJobName('syncPositions')?.fixedRate ?: '2000'}")
    public void syncPositions() {
        if (isJobEnabled("syncPositions")) {
            logJobExecution("syncPositions", () -> {
                logger.info("Starting position synchronization...");
                positionService.saveFetchedPositions(authorization);
                logger.info("Position synchronization completed.");
            });
        } else {
            logger.info("syncPositions job is disabled.");
        }
    }

    private boolean isJobEnabled(String jobName) {
        CronJobConfigEntity config = cronJobConfigRepository.findByJobName(jobName);
        return config.isEnabled();
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
