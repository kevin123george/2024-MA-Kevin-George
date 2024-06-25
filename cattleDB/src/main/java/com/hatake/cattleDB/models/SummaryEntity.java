package com.hatake.cattleDB.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "summary")
public class SummaryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "average_speed")
    private Double averageSpeed;

    @Column(name = "beacon_count")
    private Integer beaconCount;

    @Column(name = "device_id")
    private Long deviceId;

    @Column(name = "device_name")
    private String deviceName;

    @Column(name = "distance")
    private Double distance;

    @Column(name = "end_odometer")
    private Double endOdometer;

    @Column(name = "engine_hours")
    private Double engineHours;

    @Column(name = "max_speed")
    private Double maxSpeed;

    @Column(name = "position_count")
    private Integer positionCount;

    @Column(name = "spent_fuel")
    private Double spentFuel;

    @Column(name = "start_odometer")
    private Double startOdometer;
}
