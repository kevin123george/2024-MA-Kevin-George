package com.hatake.cattleDB.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Data
@Entity
@Table(name = "device")
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // Using the custom JSONB type from the Hibernate Types library
//    @ElementCollection(fetch = FetchType.EAGER)
//    @CollectionTable(name = "device_attributes", joinColumns = @JoinColumn(name = "device_id"))
//    @MapKeyColumn(name = "attribute_name")
//    @Column(name = "attribute_value", columnDefinition = "TEXT")
//    private Map<String, String> attributes;

    private String uniqueId;
    private String status;
    private OffsetDateTime lastUpdate;

    @ElementCollection
    private List<Long> geofences;

    @ElementCollection
    private List<Long> activeGeofences;

    @ElementCollection
    private List<Long> notifications;

    private Long positionId;
    private String phone;
    private String model;
    private String contact;
    private String category;
    private boolean disabled;

    @Column(name = "all_geofences_enabled")
    private boolean allGeofencesEnabled;

    private OffsetDateTime lastSuccessfulAsnJob;
    private String lastOneTimeJob;
    private OffsetDateTime lastIncompleteAsnJob;
    private OffsetDateTime firstDataTime;
    private OffsetDateTime lastAttempt;
    private String lastAsnFail;
    private OffsetDateTime lastAsnCookie;

    @ElementCollection
    private List<Long> asnConfigs;

    private String firmwareVersion;
    private Integer batteryLevel;
    private String leafGroupName;
}
