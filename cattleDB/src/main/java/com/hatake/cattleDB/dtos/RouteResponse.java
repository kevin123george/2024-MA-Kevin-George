package com.hatake.cattleDB.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.Map;

@Data
public class RouteResponse {

    private Long id;
    private String name;
    private Map<String, String> attributes;
    private String protocol;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private OffsetDateTime serverTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private OffsetDateTime deviceTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private OffsetDateTime fixTime;
    private boolean outdated;
    private boolean valid;
    private Double latitude;
    private Double longitude;
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
}
