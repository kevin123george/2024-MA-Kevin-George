package com.cattleDb.DataInjectionService.repository;

import com.cattleDb.DataInjectionService.models.CronJobConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CronJobConfigRepository extends JpaRepository<CronJobConfigEntity, String> {
    CronJobConfigEntity findByJobName(String jobName);
}