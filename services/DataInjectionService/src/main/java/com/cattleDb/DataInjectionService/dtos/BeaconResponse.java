package com.cattleDb.DataInjectionService.dtos;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.Map;

@Data
public class BeaconResponse {
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
