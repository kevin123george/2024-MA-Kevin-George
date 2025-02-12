//package com.cattleDB.LocationSimulationService.controller;
//
//import com.cattleDB.LocationSimulationService.Service.TrilaterationService;
//import com.cattleDB.LocationSimulationService.models.BeaconEntity;
//import com.cattleDB.LocationSimulationService.models.PositionSanitized;
//import com.cattleDB.LocationSimulationService.repository.BeaconRepository;
//import com.fasterxml.jackson.databind.DeserializationFeature;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.io.File;
//import java.io.IOException;
//import java.time.OffsetDateTime;
//import java.util.*;
//import java.util.stream.Collectors;
//
//@RestController
//@RequestMapping("/api/proximity1")
//public class proximityfind4 {
//
//    private static final Logger logger = LoggerFactory.getLogger(ProximityFind.class);
//
//    @Autowired
//    private BeaconRepository beaconRepository;
//
//    @GetMapping("/compute/all/test/csv")
//    public ResponseEntity<?> test(@RequestParam(defaultValue = "5") int minutes) {
//        logger.info("Starting trilateration process for all devices within the last {} minutes", minutes);
//
//        List<PositionSanitized> positionsSubset = readPositionsFromJson();
//        if (positionsSubset.isEmpty()) {
//            return ResponseEntity.ok("No recent detections found.");
//        }
//
//        List<Double> rssiDiffs = new ArrayList<>();
//        List<Double> scanRssiDiffs = new ArrayList<>();
//        List<Long> timeDiffs = new ArrayList<>();
//
//        for (int i = 0; i < positionsSubset.size(); i++) {
//            for (int j = i + 1; j < positionsSubset.size(); j++) {
//                PositionSanitized pos1 = positionsSubset.get(i);
//                PositionSanitized pos2 = positionsSubset.get(j);
//
//                double rssiDiff = Math.abs(pos1.getRssi() - pos2.getRssi());
//                double scanRssiDiff = Math.abs(
//                        Double.parseDouble(pos1.getAttributes().get("scanRssi")) -
//                                Double.parseDouble(pos2.getAttributes().get("scanRssi"))
//                );
//                long timeDiff = Math.abs(
//                        ChronoUnit.SECONDS.between(pos1.getDeviceTime(), pos2.getDeviceTime())
//                );
//
//                rssiDiffs.add(rssiDiff);
//                scanRssiDiffs.add(scanRssiDiff);
//                timeDiffs.add(timeDiff);
//            }
//        }
//
//        double rssiThreshold = computeDynamicThreshold(rssiDiffs, 0.8);
//        double scanRssiThreshold = computeDynamicThreshold(scanRssiDiffs, 0.8);
//        long timeThreshold = computeDynamicThreshold(timeDiffs, 0.9);
//
//        logger.info("Computed thresholds: RSSI_DIFF={}, SCAN_RSSI_DIFF={}, TIME_DIFF={}",
//                rssiThreshold, scanRssiThreshold, timeThreshold);
//
//        List<PositionSanitized> filteredPositions = positionsSubset.stream()
//                .filter(pos -> applyDynamicFiltering(pos, rssiThreshold, scanRssiThreshold, timeThreshold))
//                .map(this::applyKalmanFilter)
//                .collect(Collectors.toList());
//
//        for (PositionSanitized pos : filteredPositions) {
//            BeaconEntity closestBeacon = findClosestBeacon(pos);
//            if (closestBeacon != null) {
//                pos.setBeaconName(closestBeacon.getName());
//            }
//        }
//
//        return ResponseEntity.ok(filteredPositions);
//    }
//
//    private BeaconEntity findClosestBeacon(PositionSanitized pos) {
//        List<BeaconEntity> beacons = beaconRepository.findAll();
//        return beacons.stream()
//                .min(Comparator.comparingDouble(beacon -> calculateDistance(
//                        pos.getLatitude(), pos.getLongitude(),
//                        beacon.getLatitude(), beacon.getLongitude()
//                )))
//                .orElse(null);
//    }
//
//    private List<PositionSanitized> readPositionsFromJson() {
//        String jsonFilePath = "C:\\Users\\hikev\\Desktop\\thesis\\2024-MA-Kevin-George\\services\\LocationSimulationService\\data.json";
//        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule())
//                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        try {
//            return objectMapper.readValue(new File(jsonFilePath),
//                    objectMapper.getTypeFactory().constructCollectionType(List.class, PositionSanitized.class));
//        } catch (IOException e) {
//            logger.error("Error reading JSON file: {}", e.getMessage());
//            return Collections.emptyList();
//        }
//    }
//
//    private boolean applyDynamicFiltering(PositionSanitized pos, double rssiThreshold, double scanRssiThreshold, long timeThreshold) {
//        double scanRssi = Double.parseDouble(pos.getAttributes().get("scanRssi"));
//        return (Math.abs(pos.getRssi()) <= rssiThreshold) && (scanRssi <= scanRssiThreshold);
//    }
//
//    private PositionSanitized applyKalmanFilter(PositionSanitized pos) {
//        KalmanFilter latitudeFilter = new KalmanFilter(0.01, 0.5, 1);
//        KalmanFilter longitudeFilter = new KalmanFilter(0.01, 0.5, 1);
//        KalmanFilter rssiFilter = new KalmanFilter(0.005, 12, 1);
//
//        pos.setLatitude(latitudeFilter.update(pos.getLatitude()));
//        pos.setLongitude(longitudeFilter.update(pos.getLongitude()));
//        pos.setRssi((int) rssiFilter.update(pos.getRssi()));
//        return pos;
//    }
//
//    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
//        final int R = 6371; // Earth's radius in km
//        double latDistance = Math.toRadians(lat2 - lat1);
//        double lonDistance = Math.toRadians(lon2 - lon1);
//        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
//                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
//                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
//        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
//    }
//}
