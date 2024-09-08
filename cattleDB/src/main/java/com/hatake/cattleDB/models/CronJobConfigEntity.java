package com.hatake.cattleDB.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import lombok.Data;


@Data
@Entity
public class CronJobConfigEntity {

    @Id
    private String jobName;  // Unique identifier for each cron job

    private String cronExpression; // Stores cron expression
    private Long fixedRate; // Stores fixed rate for jobs that are fixed-rate based
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean enabled = true;  // New field to indicate if the job is enabled

}
