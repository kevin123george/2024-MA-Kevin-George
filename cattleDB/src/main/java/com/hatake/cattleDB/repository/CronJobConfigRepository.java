package com.hatake.cattleDB.repository;

import com.hatake.cattleDB.models.CronJobConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CronJobConfigRepository extends JpaRepository<CronJobConfigEntity, String> {
    CronJobConfigEntity findByJobName(String jobName);
}