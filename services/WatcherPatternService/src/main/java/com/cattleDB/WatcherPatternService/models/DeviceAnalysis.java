package com.cattleDB.WatcherPatternService.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;

@Entity
@Data
public class DeviceAnalysis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long deviceId;
    private Double averageRssi;
    private Double averageScanRssi;
    @Column(columnDefinition = "TEXT")  // Use TEXT to store longer strings
    private String topLocations;
    private Long devicesWithBeacon;
    private Long devicesWithoutBeacon;
    private OffsetDateTime timestamp;

}
