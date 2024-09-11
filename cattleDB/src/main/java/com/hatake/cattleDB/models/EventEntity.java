package com.hatake.cattleDB.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.OffsetDateTime;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class EventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

//    @Type(type = "jsonb")
//    @Column(name = "attributes", columnDefinition = "jsonb")
//    private Object attributes;

    private String type;
    private Long deviceId;
    private Long trackableId;
    private OffsetDateTime serverTime;
    private OffsetDateTime deviceTime;
    private Long positionId;
    private Long geofenceId;
    private String message;
    private Long userId;
    private Long beaconId;
    private String beaconName;
    private Integer beaconMajor;
    private Integer beaconMinor;
    private String beaconUuid;
}
