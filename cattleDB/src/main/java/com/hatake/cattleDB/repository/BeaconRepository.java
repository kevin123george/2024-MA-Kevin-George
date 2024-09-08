package com.hatake.cattleDB.repository;

import com.hatake.cattleDB.models.Beacon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface BeaconRepository extends JpaRepository<Beacon, Long> {
    // Count online beacons (seen in the last hour)
    @Query("SELECT COUNT(b) FROM Beacon b WHERE b.lastSeen > :time")
    long countOnlineBeacons(OffsetDateTime time);

    // Average battery level of all beacons
    @Query("SELECT AVG(b.batteryLevel) FROM Beacon b WHERE b.batteryLevel IS NOT NULL")
    Double findAverageBatteryLevel();

    // Find the latest beacons based on lastSeen
    List<Beacon> findTop5ByOrderByLastSeenDesc();

    // Count beacons by battery level (high, medium, low)
    @Query("SELECT COUNT(b) FROM Beacon b WHERE b.batteryLevel >= 80")
    long countHighBatteryBeacons();

    @Query("SELECT COUNT(b) FROM Beacon b WHERE b.batteryLevel >= 50 AND b.batteryLevel < 80")
    long countMediumBatteryBeacons();

    @Query("SELECT COUNT(b) FROM Beacon b WHERE b.batteryLevel < 50")
    long countLowBatteryBeacons();

    // Query for heatmap data (group by location)
    @Query("SELECT b.latitude, b.longitude, COUNT(b) FROM Beacon b GROUP BY b.latitude, b.longitude")
    List<Object[]> findBeaconLocationsForHeatmap();

}