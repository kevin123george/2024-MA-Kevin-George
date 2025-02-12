package com.cattleDB.LocationSimulationService.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "load_history")
public class LoadHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "rows_loaded", nullable = false)
    private int rowsLoaded;

    @Column(name = "time_taken_ms", nullable = false)
    private long timeTakenMs;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "load_timestamp", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime loadTimestamp;
}
