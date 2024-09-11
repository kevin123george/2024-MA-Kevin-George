package com.hatake.cattleDB.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.Map;

@Entity
@Data
@Table(name = "beacon")
public class Beacon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "beacon_attributes", joinColumns = @JoinColumn(name = "beacon_id"))
    @MapKeyColumn(name = "attribute_name")
    @Column(name = "attribute_value", columnDefinition = "TEXT")
    private Map<String, String> attributes;

    private Long deviceId;

    private String uuid;

    private int major;

    private int minor;

    private Double latitude;

    private Double longitude;

    @Column(name = "last_seen")
    private OffsetDateTime lastSeen;

    private Integer batteryLevel;

    private boolean relative;

    private Long roleGroupId;

}
