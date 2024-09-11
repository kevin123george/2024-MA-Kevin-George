package com.hatake.cattleDB.dtos;

import lombok.Data;

@Data
public class SummaryResponse {
    private Double averageSpeed;
    private Integer beaconCount;
    private Long deviceId;
    private String deviceName;
    private Double distance;
    private Double endOdometer;
    private Double engineHours;
    private Double maxSpeed;
    private Integer positionCount;
    private Double spentFuel;
    private Double startOdometer;
}