package com.hatake.cattleDB.models;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Table(name = "cron_job_logs")
@Data
public class CronJobLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String jobName;

    private String startTime; // Changed to String

    private String endTime; // Changed to String

    private Long durationMs;

    private String status;

    private String errorMessage;

}
