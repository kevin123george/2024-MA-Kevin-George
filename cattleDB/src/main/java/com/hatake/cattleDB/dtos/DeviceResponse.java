package com.hatake.cattleDB.dtos;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Data
public class DeviceResponse {
    private Long id;
    private String name;
    private Map<String, String> attributes; // This is stored as a JSONB field in the database
    private String uniqueId;
    private String status;
    private OffsetDateTime lastUpdate;
    private List<Long> geofences;
    private List<Long> activeGeofences;
    private List<Long> notifications;
    private Long positionId;
    private String phone;
    private String model;
    private String contact;
    private String category;
    private boolean disabled;
    private boolean allGeofencesEnabled;
    private OffsetDateTime lastSuccessfulAsnJob;
    private String lastOneTimeJob;
    private OffsetDateTime lastIncompleteAsnJob;
    private OffsetDateTime firstDataTime;
    private OffsetDateTime lastAttempt;
    private String lastAsnFail;
    private OffsetDateTime lastAsnCookie;
    private List<Long> asnConfigs;
    private String firmwareVersion;
    private Integer batteryLevel;
    private String leafGroupName;
}
