package com.cattleDB.LocationSimulationService.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Data
@NoArgsConstructor
public class DeviceMovementPattern {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long deviceId;

    private double centerLatitude;
    private double centerLongitude;

    private int clusterSize; // Number of positions in the cluster

    private OffsetDateTime timestamp; // Time when the pattern was detected

    @Lob
    @Column(columnDefinition = "text")
    private String positionsJson; // JSON array of positions in the cluster
}
