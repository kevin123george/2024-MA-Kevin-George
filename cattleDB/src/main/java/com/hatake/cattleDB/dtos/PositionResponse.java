package com.hatake.cattleDB.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PositionResponse {
    private Long id;
    private String name;
    private Map<String, String> attributes;  // This will map the dynamic attributes object
    private String protocol;
    private OffsetDateTime serverTime;
    private OffsetDateTime deviceTime;
    private OffsetDateTime fixTime;
    private boolean outdated;
    private boolean valid;
    private Double latitude;
    private Double longitude;
    private Integer eventId;
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
}
