package com.hatake.cattleDB.repository;


import com.hatake.cattleDB.models.CronJobLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CronJobLogRepository extends JpaRepository<CronJobLog, Long> {
    Optional<CronJobLog> findTopByJobNameOrderByIdDesc(String jobName);

}
