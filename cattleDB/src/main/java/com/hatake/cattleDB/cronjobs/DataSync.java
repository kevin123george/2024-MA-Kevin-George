package com.hatake.cattleDB.cronjobs;

import com.hatake.cattleDB.models.CronJobLog;
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

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
//
//    @Scheduled(cron = "0 */10 * * * *")
//    public void syncEvents() {
//        logJobExecution("syncEvents", () -> {
//            logger.info("Starting event synchronization...");
//            eventService.fetchEvents();
//            logger.info("Event synchronization completed.");
//        });
//    }
//
//    @Scheduled(cron = "0 0 */3 * * *")
//    public void syncRoutes() {
//        logJobExecution("syncRoutes", () -> {
//            logger.info("Starting route synchronization...");
//            routeService.fetchRoutes();
//            logger.info("Route synchronization completed.");
//        });
//    }
//
//
//    @Scheduled(cron = "0 */10 * * * *")
//    public void syncSummary() {
//        logJobExecution("syncSummary", () -> {
//            logger.info("Starting summary synchronization...");
//            summaryService.fetchSummary();
//            logger.info("Summary synchronization completed.");
//        });
//    }
//
//
//    @Scheduled(cron = "0 */1 * * * *")
//    public void syncBecons() {
//        logJobExecution("syncSummary", () -> {
//            logger.info("Starting summary synchronization...");
//            beaconService.fetchAndSaveBeacons();
//            logger.info("Summary synchronization completed.");
//        });
//    }

//    @Scheduled(fixedRate = 600000) // Run every 5 minutes (300,000 ms)
        @Scheduled(fixedRate = 2000)
        public void syncPositions() {
        logJobExecution("syncPosition", () -> {
            logger.info("Starting position synchronization...");
            positionService.saveFetchedPositions();
            logger.info("position synchronization completed.");
        });
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
