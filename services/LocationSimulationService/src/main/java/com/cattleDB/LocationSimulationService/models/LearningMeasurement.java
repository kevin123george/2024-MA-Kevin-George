package com.cattleDB.LocationSimulationService.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;

@Entity
@Data
@Table(name = "learning_measurement")
public class LearningMeasurement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "wifi_ap", nullable = false)
    private String wifiAp;

    @Column(name = "cnx_time", nullable = false)
    private OffsetDateTime cnxTime;

    @Column(name = "client_id", nullable = false)
    private String clientId;

    @Column(name = "created_at", updatable = false, nullable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }

    // Getters and Setters
}
