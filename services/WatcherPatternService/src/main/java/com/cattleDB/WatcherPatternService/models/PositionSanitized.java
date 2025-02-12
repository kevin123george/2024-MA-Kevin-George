package com.cattleDB.WatcherPatternService.models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Map;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PositionSanitized {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "position_sanitized_attributes", joinColumns = @JoinColumn(name = "position_sanitized_id"))
    @MapKeyColumn(name = "attribute_name")
    @Column(name = "attribute_value", columnDefinition = "TEXT")
    private Map<String, String> attributes;

    private String protocol;
    private OffsetDateTime serverTime;
    private OffsetDateTime deviceTime;
    private OffsetDateTime fixTime;
    private boolean outdated;
    private boolean valid;
    private double latitude;
    private double longitude;
    private Long eventId;
    private Long deviceId;
    private Integer scanRssi;
    private Integer rssi;
    private Long beaconId;
    private String beaconName;
    private Integer beaconMajor;
    private Integer beaconMinor;
    private String beaconUuid;
    private Long sourceDeviceId;
    private String deviceName;
    private double speed;

    public PositionSanitized(Long id, String name, String protocol, OffsetDateTime serverTime,
                             OffsetDateTime deviceTime, OffsetDateTime fixTime, boolean outdated, boolean valid,
                             double latitude, double longitude, Long deviceId, double speed) {
        this.id = id;
        this.name = name;
        this.protocol = protocol;
        this.serverTime = serverTime;
        this.deviceTime = deviceTime;
        this.fixTime = fixTime;
        this.outdated = outdated;
        this.valid = valid;
        this.latitude = latitude;
        this.longitude = longitude;
        this.deviceId = deviceId;
        this.speed = speed;
    }



//    @Column(name = "embedding", columnDefinition = "vector(1536)")  // OpenAI embedding dimension
//    private double[] embedding;

    public String toTextRepresentation() {
        return String.format(
                "Position ID: %d, Device: %s, Location: (%f, %f), Speed: %f km/h, Time: %s",
                id, deviceName, latitude, longitude, speed, deviceTime
        );
    }
}
