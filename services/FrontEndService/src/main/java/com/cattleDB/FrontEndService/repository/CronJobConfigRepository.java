package com.cattleDB.FrontEndService.repository;

import com.cattleDB.FrontEndService.models.CronJobConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CronJobConfigRepository extends JpaRepository<CronJobConfigEntity, String> {
    CronJobConfigEntity findByJobName(String jobName);
}