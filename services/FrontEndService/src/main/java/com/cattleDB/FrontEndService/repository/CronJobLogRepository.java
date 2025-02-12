package com.cattleDB.FrontEndService.repository;


import com.cattleDB.FrontEndService.models.CronJobLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CronJobLogRepository extends JpaRepository<CronJobLog, Long> {
}
