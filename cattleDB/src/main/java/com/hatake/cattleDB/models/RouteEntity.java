package com.hatake.cattleDB.models;


import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.Map;

@Entity
@Data
@ToString
@RequiredArgsConstructor
public class RouteEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "device_attributes", joinColumns = @JoinColumn(name = "device_id"))
    @MapKeyColumn(name = "attribute_name")
    @Column(name = "attribute_value")
    private Map<String, String> attributes;
    private String protocol;
    @Column(name = "server_time")
    private OffsetDateTime serverTime;
    @Column(name = "device_time")
    private OffsetDateTime deviceTime;
    @Column(name = "fix_time")
    private OffsetDateTime fixTime;
    private boolean outdated;

    private boolean valid;

    private Double latitude;

    private Double longitude;

    @Column(name = "event_id")
    private Long eventId;

    @Column(name = "device_id")
    private Long deviceId;

    @Column(name = "scan_rssi")
    private Integer scanRssi;

    private Integer rssi;

    @Column(name = "beacon_id")
    private Long beaconId;

    @Column(name = "beacon_name")
    private String beaconName;

    @Column(name = "beacon_major")
    private Integer beaconMajor;

    @Column(name = "beacon_minor")
    private Integer beaconMinor;

    @Column(name = "beacon_uuid")
    private String beaconUuid;

    @Column(name = "source_device_id")
    private Long sourceDeviceId;

    @Column(name = "device_name")
    private String deviceName;

}
