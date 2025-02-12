package com.cattleDB.LocationSimulationService.models;


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


    // Method to calculate distance to a beacon
    public double getDistanceToBeacon(BeaconEntity beacon) {
        return calculateDistance(this.latitude, this.longitude, beacon.getLatitude(), beacon.getLongitude());
    }

    // Haversine formula to calculate distance in km
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth's radius in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
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
