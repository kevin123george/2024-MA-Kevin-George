package com.hatake.cattleDB.controller;

import com.hatake.cattleDB.models.Beacon;
import com.hatake.cattleDB.repository.BeaconRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@CrossOrigin("http://localhost:5173/")

@RestController
public class BeaconAnalyticsController {

    @Autowired
    private BeaconRepository beaconRepository;

    @GetMapping("/api/analytics/beacons")
    public ResponseEntity<?> getBeaconAnalytics() {
        long totalBeacons = beaconRepository.count();
        long onlineBeacons = beaconRepository.countOnlineBeacons(OffsetDateTime.now().minusHours(1));
        Double averageBatteryLevel = beaconRepository.findAverageBatteryLevel();
        List<Beacon> latestBeacons = beaconRepository.findTop5ByOrderByLastSeenDesc();

        Map<String, Object> result = new HashMap<>();
        result.put("totalBeacons", totalBeacons);
        result.put("onlineBeacons", onlineBeacons);
        result.put("averageBatteryLevel", averageBatteryLevel != null ? averageBatteryLevel : 0);
        result.put("latestBeacons", latestBeacons);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/api/analytics/beacon-battery-levels")
    public ResponseEntity<?> getBeaconBatteryLevels() {
        long totalBeacons = beaconRepository.count();
        long highBattery = beaconRepository.countHighBatteryBeacons();
        long mediumBattery = beaconRepository.countMediumBatteryBeacons();
        long lowBattery = beaconRepository.countLowBatteryBeacons();

        Map<String, Object> result = new HashMap<>();
        result.put("totalBeacons", totalBeacons);
        result.put("batteryLevels", Map.of(
                "highBattery", highBattery,
                "mediumBattery", mediumBattery,
                "lowBattery", lowBattery
        ));

        return ResponseEntity.ok(result);
    }

    @GetMapping("/api/analytics/beacon-locations")
    public ResponseEntity<?> getBeaconLocationsForHeatmap() {
        List<Object[]> locations = beaconRepository.findBeaconLocationsForHeatmap();
        return ResponseEntity.ok(locations);
    }
}
