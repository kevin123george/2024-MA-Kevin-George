package com.hatake.cattleDB.controller;

import com.hatake.cattleDB.models.CronJobLog;
import com.hatake.cattleDB.repository.CronJobLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@CrossOrigin("http://localhost:5173/")

@RestController
public class CronJobAnalyticsController {

    @Autowired
    private CronJobLogRepository cronJobLogRepository;

    @GetMapping("/api/analytics/cron-jobs")
    public ResponseEntity<?> getCronJobAnalytics() {
        long totalCronJobs = cronJobLogRepository.count();
        Double averageExecutionTime = cronJobLogRepository.findAverageExecutionTime();
        Double successRate = cronJobLogRepository.findSuccessRate();
        List<CronJobLog> failedCronJobs = cronJobLogRepository.findTop5ByStatusOrderByStartTimeDesc("FAILED");
        CronJobLog lastCronJob = cronJobLogRepository.findTop1ByOrderByStartTimeDesc();

        Map<String, Object> result = new HashMap<>();
        result.put("totalCronJobsExecuted", totalCronJobs);
        result.put("averageExecutionTimeMs", averageExecutionTime != null ? averageExecutionTime : 0);
        result.put("successRate", successRate != null ? successRate : 0);
        result.put("failedCronJobs", failedCronJobs);
        result.put("lastCronJob", lastCronJob);

        return ResponseEntity.ok(result);
    }
}
