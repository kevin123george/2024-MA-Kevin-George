package com.cattleDB.LocationSimulationService.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.OffsetDateTime;

@Entity
@Data
public class BeaconEntity {
    @Id
    private Long id;
    private String name;
    private Long deviceId;
    private String uuid;
    private int major;
    private int minor;
    private Double latitude;
    private Double longitude;
    private OffsetDateTime lastSeen;
    private Integer batteryLevel;
    private boolean relative;
    private Long roleGroupId;
}
