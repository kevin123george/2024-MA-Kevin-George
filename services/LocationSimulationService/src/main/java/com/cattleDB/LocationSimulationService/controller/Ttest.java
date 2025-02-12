//package com.cattleDB.LocationSimulationService.controller;
//
//import com.cattleDB.LocationSimulationService.Service.TrilaterationService;
//import com.cattleDB.LocationSimulationService.models.BeaconEntity;
//import com.cattleDB.LocationSimulationService.models.PositionSanitized;
//import com.fasterxml.jackson.databind.DeserializationFeature;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.io.File;
//import java.io.IOException;
//import java.time.OffsetDateTime;
//import java.time.temporal.ChronoUnit;
//import java.util.*;
//import java.util.stream.Collectors;
//
//@RestController
//public class Ttest {
//
//    private static final Logger logger = LoggerFactory.getLogger(TrilaterationController.class);
//    private static final double RSSI_DIFFERENCE_THRESHOLD = 10.0;
//    private static final double SCAN_RSSI_DIFFERENCE_THRESHOLD = 10.0;
//    private static final long TIME_DIFFERENCE_SECONDS = 60;
//
//    @GetMapping("/compute/all/test/csv")
//    public ResponseEntity<?> test(@RequestParam(defaultValue = "5") int minutes) {
//        logger.info("Starting trilateration process for all devices within the last {} minutes", minutes);
//
//        String jsonFilePath = "C:\\Users\\hikev\\Desktop\\thesis\\2024-MA-Kevin-George\\services\\LocationSimulationService\\data.json";
//        List<PositionSanitized> positionsSubset = readPositionsFromJson(jsonFilePath);
//
//        if (positionsSubset == null || positionsSubset.isEmpty()) {
//            return ResponseEntity.ok("No positions found or error reading JSON file.");
//        }
//
//        positionsSubset = preprocessRSSI(positionsSubset);
//        Map<Long, OffsetDateTime> latestTimesMap = getLatestDeviceTimestamps(positionsSubset);
//
//        if (latestTimesMap.isEmpty()) {
//            return ResponseEntity.ok("No recent detections found.");
//        }
//
//        List<Map<String, Object>> devicesResults = new ArrayList<>();
//        Map<Long, Map<String, Object>> uniqueBeaconsMap = new HashMap<>();
//
//        for (Map.Entry<Long, OffsetDateTime> entry : latestTimesMap.entrySet()) {
//            Long deviceId = entry.getKey();
//            OffsetDateTime windowStart = entry.getValue().minusMinutes(minutes);
//
//            List<PositionSanitized> recentDetections = positionsSubset.stream()
//                    .filter(pos -> pos.getDeviceId().equals(deviceId)
//                            && (pos.getDeviceTime().isAfter(windowStart) || pos.getDeviceTime().isEqual(windowStart)))
//                    .collect(Collectors.toList());
//
//            if (recentDetections.isEmpty()) {
//                continue;
//            }
//
//            PositionSanitized optimalPosition = findOptimalCowLocation(recentDetections);
//            if (optimalPosition == null) {
//                continue;
//            }
//
//            Map<String, Object> deviceResult = new HashMap<>();
//            deviceResult.put("deviceId", deviceId);
//            deviceResult.put("optimalPosition", optimalPosition);
//
//            try {
//                double[] trilaterationResult = performTrilateration(recentDetections);
//                deviceResult.put("latitude", trilaterationResult[0]);
//                deviceResult.put("longitude", trilaterationResult[1]);
//            } catch (Exception e) {
//                logger.error("Trilateration failed for device {}: {}", deviceId, e.getMessage());
//                double[] fallbackPosition = fallbackWeightedAverage(recentDetections);
//                deviceResult.put("latitude", fallbackPosition[0]);
//                deviceResult.put("longitude", fallbackPosition[1]);
//                deviceResult.put("message", "Fallback method used for positioning.");
//            }
//
//            devicesResults.add(deviceResult);
//        }
//
//        if (devicesResults.isEmpty()) {
//            return ResponseEntity.ok("No valid positions could be estimated.");
//        }
//
//        Map<String, Object> finalResponse = new HashMap<>();
//        finalResponse.put("devices", devicesResults);
//        finalResponse.put("allBeaconsUsed", new ArrayList<>(uniqueBeaconsMap.values()));
//
//        return ResponseEntity.ok(finalResponse);
//    }
//
//    private List<PositionSanitized> readPositionsFromJson(String jsonFilePath) {
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.registerModule(new JavaTimeModule());
//        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//
//        try {
//            return objectMapper.readValue(
//                    new File(jsonFilePath),
//                    objectMapper.getTypeFactory().constructCollectionType(List.class, PositionSanitized.class)
//            );
//        } catch (IOException e) {
//            logger.error("Error reading JSON file: {}", e.getMessage());
//            return null;
//        }
//    }
//
//    private List<PositionSanitized> preprocessRSSI(List<PositionSanitized> positions) {
//        double meanRSSI = positions.stream()
//                .mapToInt(PositionSanitized::getRssi)
//                .average()
//                .orElse(0);
//        double stdDevRSSI = Math.sqrt(positions.stream()
//                .mapToDouble(p -> Math.pow(p.getRssi() - meanRSSI, 2))
//                .average()
//                .orElse(0));
//
//        return positions.stream()
//                .filter(pos -> Math.abs(pos.getRssi() - meanRSSI) <= 2 * stdDevRSSI)
//                .collect(Collectors.toList());
//    }
//
//    private Map<Long, OffsetDateTime> getLatestDeviceTimestamps(List<PositionSanitized> positions) {
//        Map<Long, OffsetDateTime> latestTimesMap = new HashMap<>();
//
//        for (PositionSanitized pos : positions) {
//            latestTimesMap.merge(pos.getDeviceId(), pos.getDeviceTime(),
//                    (existing, newTime) -> existing.isAfter(newTime) ? existing : newTime);
//        }
//
//        return latestTimesMap;
//    }
//
//    private PositionSanitized findOptimalCowLocation(List<PositionSanitized> positions) {
//        positions.sort(Comparator.comparing(PositionSanitized::getDeviceTime));
//
//        for (int i = 0; i < positions.size(); i++) {
//            for (int j = i + 1; j < positions.size(); j++) {
//                PositionSanitized pos1 = positions.get(i);
//                PositionSanitized pos2 = positions.get(j);
//
//                double rssiDiff = Math.abs(pos1.getRssi() - pos2.getRssi());
//                double scanRssiDiff = Math.abs(
//                        Double.parseDouble(pos1.getAttributes().get("scanRssi")) -
//                                Double.parseDouble(pos2.getAttributes().get("scanRssi")));
//
//                long timeDiff = Math.abs(ChronoUnit.SECONDS.between(pos1.getDeviceTime(), pos2.getDeviceTime()));
//
//                if (rssiDiff <= RSSI_DIFFERENCE_THRESHOLD &&
//                        scanRssiDiff <= SCAN_RSSI_DIFFERENCE_THRESHOLD &&
//                        timeDiff <= TIME_DIFFERENCE_SECONDS) {
//                    return pos1; // Simplified: Return the first valid pair for now
//                }
//            }
//        }
//
//        return null;
//    }
//
//    private ResponseEntity<?> performTrilateration(List<PositionSanitized> filteredPositions) {
//        List<Map<String, Object>> devicesResults = new ArrayList<>();
//
//        logger.info("Computing reference latitude and longitude using beacon data...");
//        List<BeaconEntity> allBeacons = beaconRepository.findAll();
//        double sumLat = 0.0, sumLon = 0.0;
//        int count = 0;
//
//        for (BeaconEntity b : allBeacons) {
//            if (b.getLatitude() != null && b.getLongitude() != null) {
//                sumLat += b.getLatitude();
//                sumLon += b.getLongitude();
//                count++;
//            }
//        }
//
//        if (count == 0) {
//            logger.error("No valid beacon lat/lons found in the database.");
//            return ResponseEntity.badRequest().body("No valid beacon lat/lons found.");
//        }
//
//        double refLat = sumLat / count;
//        double refLon = sumLon / count;
//        logger.info("Reference position set to Lat: {}, Lon: {}", refLat, refLon);
//
//        for (PositionSanitized pos : filteredPositions) {
//            if (pos.getBeaconId() == null) {
//                continue;
//            }
//
//            BeaconEntity beacon = beaconRepository.findByName(pos.getBeaconName()).orElse(null);
//            if (beacon == null || beacon.getLatitude() == null || beacon.getLongitude() == null) {
//                continue;
//            }
//
//            double[] xy = TrilaterationService.latlonToXY(beacon.getLatitude(), beacon.getLongitude(), refLat, refLon);
//            int rssiValue = (pos.getRssi() == null) ? -100 : 128 - pos.getRssi();
//            double d = TrilaterationService.rssiToDistance(rssiValue);
//
//            Map<String, Object> deviceResult = new HashMap<>();
//            deviceResult.put("deviceId", pos.getDeviceId());
//            deviceResult.put("latitude", xy[0]);
//            deviceResult.put("longitude", xy[1]);
//            deviceResult.put("message", "Estimated location computed via trilateration.");
//
//            devicesResults.add(deviceResult);
//        }
//
//        if (devicesResults.isEmpty()) {
//            logger.warn("No valid positions could be estimated.");
//            return ResponseEntity.ok("No valid positions could be estimated.");
//        }
//
//        Map<String, Object> finalResponse = new HashMap<>();
//        finalResponse.put("devices", devicesResults);
//
//        return ResponseEntity.ok(finalResponse);
//    }
//
//
//    private double[] fallbackWeightedAverage(List<PositionSanitized> detections) {
//        double totalWeight = 0;
//        double weightedLat = 0;
//        double weightedLon = 0;
//
//        for (PositionSanitized pos : detections) {
//            double weight = 1.0 / (Math.abs(pos.getRssi()) + 1);
//            weightedLat += pos.getLatitude() * weight;
//            weightedLon += pos.getLongitude() * weight;
//            totalWeight += weight;
//        }
//
//        return new double[]{weightedLat / totalWeight, weightedLon / totalWeight};
//    }
//}
