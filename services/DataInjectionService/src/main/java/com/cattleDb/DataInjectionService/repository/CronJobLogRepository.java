package com.cattleDb.DataInjectionService.repository;


import com.cattleDb.DataInjectionService.models.CronJobLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CronJobLogRepository extends JpaRepository<CronJobLog, Long> {
    Optional<CronJobLog> findTopByJobNameOrderByIdDesc(String jobName);

    // Average execution time
    @Query("SELECT AVG(c.durationMs) FROM CronJobLog c WHERE c.durationMs IS NOT NULL")
    Double findAverageExecutionTime();

    // Success rate
    @Query("SELECT (COUNT(c) FILTER (WHERE c.status = 'COMPLETED') * 100.0 / COUNT(c)) FROM CronJobLog c")
    Double findSuccessRate();

    // Find the last failed cron jobs
    List<CronJobLog> findTop5ByStatusOrderByStartTimeDesc(String status);

    // Find the last cron job
    CronJobLog findTop1ByOrderByStartTimeDesc();

}
