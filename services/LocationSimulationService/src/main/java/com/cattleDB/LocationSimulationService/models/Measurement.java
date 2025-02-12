package com.cattleDB.LocationSimulationService.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;

@Entity
@Data
@Table(name = "measurement")
public class Measurement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Boolean valid;

    @Column(nullable = false)
    private String device;

    @Column(nullable = false)
    private OffsetDateTime time;

    private String geofences;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    private Double altitude;

    private String beacon;

    private Integer major;

    private Integer minor;

    private String uuid;

    private Double speed;

    @Lob
    private String attributes;

    @Column(name = "created_at", updatable = false, nullable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }

    // Getters and Setters
}
