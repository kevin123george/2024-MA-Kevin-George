package com.cattleDB.WatcherPatternService.repository;

import com.cattleDB.WatcherPatternService.models.CronJobConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CronJobConfigRepository extends JpaRepository<CronJobConfigEntity, String> {
    CronJobConfigEntity findByJobName(String jobName);
}