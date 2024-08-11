package com.hatake.cattleDB.repository;


import com.hatake.cattleDB.models.CronJobLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CronJobLogRepository extends JpaRepository<CronJobLog, Long> {
}
