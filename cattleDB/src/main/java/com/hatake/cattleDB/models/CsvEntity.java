package com.hatake.cattleDB.models;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class CsvEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private boolean valid;
    private String time;
    private String geofences;
    private double latitude;
    private double longitude;
    private String altitude;
    private String locationBeacon;
    private int major;
    private int minor;
    private String uuid;
    private String speed;
    private String attributes;
    private String filename;

    public CsvEntity(boolean valid, String time, String geofences, double latitude, double longitude,
                     String altitude, String locationBeacon, int major, int minor, String uuid,
                     String speed, String attributes, String filename) {
        this.valid = valid;
        this.time = time;
        this.geofences = geofences;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.locationBeacon = locationBeacon;
        this.major = major;
        this.minor = minor;
        this.uuid = uuid;
        this.speed = speed;
        this.attributes = attributes;
        this.filename = filename;
    }

}
