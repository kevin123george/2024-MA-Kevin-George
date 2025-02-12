package com.cattleDB.LocationSimulationService.controller;


import com.cattleDB.LocationSimulationService.Service.TrilaterationService;
import com.cattleDB.LocationSimulationService.models.BeaconEntity;
import com.cattleDB.LocationSimulationService.models.PositionSanitized;
import com.cattleDB.LocationSimulationService.repository.BeaconRepository;
import com.cattleDB.LocationSimulationService.repository.PositionSanitizedRepository;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Example REST controller for trilateration
 */
@RestController
@RequestMapping("/api/trilateration")
public class TrilaterationController {


    private static final Logger logger = LoggerFactory.getLogger(TrilaterationController.class);

    @Autowired
    private PositionSanitizedRepository positionRepository;

    @Autowired
    private BeaconRepository beaconRepository;



    /**
     * GET endpoint that computes the device's location based on the last X minutes of RSSI data.
     *
     * @param deviceId ID of the device for which we want to compute location
     * @param minutes  how many minutes back in time we look for detections
     */
    @GetMapping("/compute/{deviceId}")
    public ResponseEntity<?> computeTrilateration(
            @PathVariable Long deviceId,
            @RequestParam(defaultValue = "5") int minutes
    ) {
        Map<Long, OffsetDateTime> latestTimesMap = new HashMap<>();

        // Fetch the latest 1000 positions from the database
        int limit = 1000000;
        List<PositionSanitized> positionsSubset = positionRepository.findTopPositions(limit);

        // Determine the latest timestamp for each deviceId
        for (PositionSanitized pos : positionsSubset) {
            Long deviceId1 = pos.getDeviceId();
            OffsetDateTime currentTime = pos.getDeviceTime();

            OffsetDateTime existingTime = latestTimesMap.get(deviceId1);
            if (existingTime == null || currentTime.isAfter(existingTime)) {
                latestTimesMap.put(deviceId1, currentTime);
            }
        }


        System.out.println("total device to  check "+ String.valueOf(latestTimesMap.size()));
        // Ensure the requested deviceId has a position recorded
        if (!latestTimesMap.containsKey(deviceId)) {
            return ResponseEntity.ok("No recent detections found for the given device.");
        }

        // Use the latest timestamp for the given device
        OffsetDateTime latestTimeForDevice = latestTimesMap.get(deviceId);
        OffsetDateTime windowStart = latestTimeForDevice.minusMinutes(minutes);

        // Fetch relevant positions for the device within the time window
        List<PositionSanitized> recentDetections = positionRepository
                .findAllByDeviceIdAndDeviceTimeBetween(deviceId, windowStart, latestTimeForDevice);

        if (recentDetections.isEmpty()) {
            return ResponseEntity.ok("No detections found in the last " + minutes + " minutes.");
        }

        // Compute reference lat/lon from all beacon positions
        List<BeaconEntity> allBeacons = beaconRepository.findAll();
        double sumLat = 0.0, sumLon = 0.0;
        int count = 0;
        for (BeaconEntity b : allBeacons) {
            if (b.getLatitude() != null && b.getLongitude() != null) {
                sumLat += b.getLatitude();
                sumLon += b.getLongitude();
                count++;
            }
        }

        if (count == 0) {
            return ResponseEntity.badRequest().body("No valid beacon lat/lons found.");
        }

        double refLat = sumLat / count;
        double refLon = sumLon / count;

        // Prepare data for trilateration
        List<double[]> beaconXY = new ArrayList<>();
        List<Double> distances = new ArrayList<>();

        for (PositionSanitized pos : recentDetections) {
            if (pos.getBeaconId() == null) continue;

            BeaconEntity beacon = beaconRepository.findById(pos.getBeaconId()).orElse(null);
            if (beacon == null || beacon.getLatitude() == null || beacon.getLongitude() == null) {
                continue;
            }

            double[] xy = TrilaterationService.latlonToXY(beacon.getLatitude(), beacon.getLongitude(), refLat, refLon);

            int rssi = (pos.getRssi() == null) ? -100 : pos.getRssi();
            double d = TrilaterationService.rssiToDistance(rssi);

            beaconXY.add(xy);
            distances.add(d);
        }

        if (beaconXY.isEmpty()) {
            return ResponseEntity.ok("No beacon positions found for the recent detections.");
        }

        // Solve position using trilateration
        double[] finalXY = TrilaterationService.solvePosition(beaconXY, distances);
        double finalX = finalXY[0];
        double finalY = finalXY[1];

        // Convert local XY coordinates back to lat/lon
        double[] finalLatLon = TrilaterationService.xyToLatlon(finalX, finalY, refLat, refLon);
        double finalLat = finalLatLon[0];
        double finalLon = finalLatLon[1];

        // Return response JSON
        var result = new HashMap<String, Object>();
        result.put("deviceId", deviceId);
        result.put("latitude", finalLat);
        result.put("longitude", finalLon);
        result.put("timestamp", latestTimeForDevice.toString());
        result.put("message", "Estimated location computed via trilateration.");

        return ResponseEntity.ok(result);
    }


    @GetMapping("/compute/all")
    public ResponseEntity<?> computeTrilaterationForAllDevices(@RequestParam(defaultValue = "5") int minutes) {
        logger.info("Starting trilateration process for all devices within the last {} minutes", minutes);

        Map<Long, OffsetDateTime> latestTimesMap = new HashMap<>();

        // Fetch the latest 1000 positions from the database
        int limit = 1000;
        logger.info("Fetching the latest {} positions from the database...", limit);
        List<PositionSanitized> positionsSubset = positionRepository.findTopPositions(limit);

        if (positionsSubset.isEmpty()) {
            logger.warn("No positions found in the database.");
            return ResponseEntity.ok("No recent detections found.");
        }

        logger.info("Processing {} positions to determine latest timestamps for each device...", positionsSubset.size());

        // Determine the latest timestamp for each deviceId
        for (PositionSanitized pos : positionsSubset) {
            Long deviceId = pos.getDeviceId();
            OffsetDateTime currentTime = pos.getDeviceTime();

            OffsetDateTime existingTime = latestTimesMap.get(deviceId);
            if (existingTime == null || currentTime.isAfter(existingTime)) {
                latestTimesMap.put(deviceId, currentTime);
                logger.debug("Updated latest time for device {}: {}", deviceId, currentTime);
            }
        }
        System.out.println("total device to  check "+ String.valueOf(latestTimesMap.size()));


        if (latestTimesMap.isEmpty()) {
            logger.warn("No recent timestamps found for any device.");
            return ResponseEntity.ok("No recent detections found.");
        }

        logger.info("Computing reference latitude and longitude using beacon data...");

        List<BeaconEntity> allBeacons = beaconRepository.findAll();
        double sumLat = 0.0, sumLon = 0.0;
        int count = 0;
        for (BeaconEntity b : allBeacons) {
            if (b.getLatitude() != null && b.getLongitude() != null) {
                sumLat += b.getLatitude();
                sumLon += b.getLongitude();
                count++;
            }
        }

        if (count == 0) {
            logger.error("No valid beacon lat/lons found in the database.");
            return ResponseEntity.badRequest().body("No valid beacon lat/lons found.");
        }

        double refLat = sumLat / count;
        double refLon = sumLon / count;
        logger.info("Reference position set to Lat: {}, Lon: {}", refLat, refLon);

        // Prepare results
        List<Map<String, Object>> results = new ArrayList<>();

        for (Map.Entry<Long, OffsetDateTime> entry : latestTimesMap.entrySet()) {
            Long deviceId = entry.getKey();
            OffsetDateTime latestTimeForDevice = entry.getValue();
            OffsetDateTime windowStart = latestTimeForDevice.minusMinutes(minutes);

            logger.info("Fetching recent detections for device {} between {} and {}", deviceId, windowStart, latestTimeForDevice);
            List<PositionSanitized> recentDetections = positionRepository.findAllByDeviceIdAndDeviceTimeBetween(deviceId, windowStart, latestTimeForDevice);

            if (recentDetections.isEmpty()) {
                logger.warn("No recent detections found for device {}", deviceId);
                continue;
            }
            String deviceName = recentDetections.get(0).getDeviceName();
            List<double[]> beaconXY = new ArrayList<>();
            List<Double> distances = new ArrayList<>();

            for (PositionSanitized pos : recentDetections) {
                if (pos.getBeaconId() == null) {
                    logger.debug("Skipping position {} due to missing beacon ID", pos.getId());
                    continue;
                }

                BeaconEntity beacon = beaconRepository.findById(pos.getBeaconId()).orElse(null);
                if (beacon == null || beacon.getLatitude() == null || beacon.getLongitude() == null) {
                    logger.debug("Skipping beacon ID {} due to missing or invalid data", pos.getBeaconId());
                    continue;
                }

                // Convert beacon lat/lon -> local XY
                double[] xy = TrilaterationService.latlonToXY(beacon.getLatitude(), beacon.getLongitude(), refLat, refLon);

                // Convert RSSI -> distance
                int rssi = (pos.getRssi() == null) ? -100 : pos.getRssi();
                double d = TrilaterationService.rssiToDistance(rssi);

                beaconXY.add(xy);
                distances.add(d);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("deviceId", deviceId);
            result.put("deviceName", deviceName);
            result.put("timestamp", latestTimeForDevice.toString());

            if (!beaconXY.isEmpty()) {
                try {
                    logger.info("Performing trilateration for device {}", deviceId);
                    double[] finalXY = TrilaterationService.solvePosition(beaconXY, distances);
                    double[] finalLatLon = TrilaterationService.xyToLatlon(finalXY[0], finalXY[1], refLat, refLon);

                    logger.info("Estimated position for device {}: Lat: {}, Lon: {}", deviceId, finalLatLon[0], finalLatLon[1]);

                    result.put("latitude", finalLatLon[0]);
                    result.put("longitude", finalLatLon[1]);
                    result.put("message", "Estimated location computed via trilateration.");
                } catch (Exception e) {
                    logger.error("Trilateration failed for device {}. Falling back to weighted average method.", deviceId);

                    // Fallback to weighted averaging method
                    double[] fallbackPosition = fallbackWeightedAverage(recentDetections);
                    result.put("latitude", fallbackPosition[0]);
                    result.put("longitude", fallbackPosition[1]);
                    result.put("message", "Estimated location computed using weighted average method.");
                }
            } else {
                logger.warn("No beacon positions found for device {}. Falling back to weighted averaging method.", deviceId);

                // Fallback to weighted averaging method
                double[] fallbackPosition = fallbackWeightedAverage(recentDetections);
                result.put("latitude", fallbackPosition[0]);
                result.put("longitude", fallbackPosition[1]);
                result.put("message", "Estimated location computed using weighted average method.");
            }

            results.add(result);
        }

        if (results.isEmpty()) {
            logger.warn("No valid positions could be estimated.");
            return ResponseEntity.ok("No valid positions could be estimated.");
        }

        logger.info("Trilateration process completed successfully for {} devices", results.size());
        return ResponseEntity.ok(results);
    }

    /**
     * Fallback method using weighted average based on RSSI and scan RSSI values
     */
    private double[] fallbackWeightedAverage(List<PositionSanitized> recentDetections) {
        double sumLat = 0.0, sumLon = 0.0, weightSum = 0.0;

        for (PositionSanitized pos : recentDetections) {
            double lat = pos.getLatitude();
            double lon = pos.getLongitude();
            int rssi = (pos.getRssi() != null) ? pos.getRssi() : -100;
            int scanRssi = (pos.getScanRssi() != null) ? pos.getScanRssi() : -100;

            // Weight is inversely proportional to RSSI strength (stronger signal = higher weight)
            double weight = Math.pow(10, rssi / 10.0) + Math.pow(10, scanRssi / 10.0);

            sumLat += lat * weight;
            sumLon += lon * weight;
            weightSum += weight;
        }

        if (weightSum == 0) {
            return new double[]{0.0, 0.0}; // Default position if no valid weights
        }

        return new double[]{sumLat / weightSum, sumLon / weightSum};
    }





    @GetMapping("/compute/all/v2")
    public ResponseEntity<?> computeTrilaterationForAllDevicesV2(@RequestParam(defaultValue = "5") int minutes) {
        logger.info("Starting trilateration process for all devices within the last {} minutes", minutes);

        Map<Long, OffsetDateTime> latestTimesMap = new HashMap<>();

        // Fetch the latest 1000 positions from the database
        int limit = 1000;
        logger.info("Fetching the latest {} positions from the database...", limit);
        List<PositionSanitized> positionsSubset = positionRepository.findTopPositions(limit);

        if (positionsSubset.isEmpty()) {
            logger.warn("No positions found in the database.");
            return ResponseEntity.ok("No recent detections found.");
        }

        // Determine the latest timestamp for each deviceId
        for (PositionSanitized pos : positionsSubset) {
            Long deviceId = pos.getDeviceId();
            OffsetDateTime currentTime = pos.getDeviceTime();
            OffsetDateTime existingTime = latestTimesMap.get(deviceId);

            if (existingTime == null || currentTime.isAfter(existingTime)) {
                latestTimesMap.put(deviceId, currentTime);
            }
        }

        if (latestTimesMap.isEmpty()) {
            logger.warn("No recent timestamps found for any device.");
            return ResponseEntity.ok("No recent detections found.");
        }

        logger.info("Computing reference latitude and longitude using beacon data...");
        List<BeaconEntity> allBeacons = beaconRepository.findAll();
        double sumLat = 0.0, sumLon = 0.0;
        int count = 0;
        for (BeaconEntity b : allBeacons) {
            if (b.getLatitude() != null && b.getLongitude() != null) {
                sumLat += b.getLatitude();
                sumLon += b.getLongitude();
                count++;
            }
        }

        if (count == 0) {
            logger.error("No valid beacon lat/lons found in the database.");
            return ResponseEntity.badRequest().body("No valid beacon lat/lons found.");
        }

        // Calculate reference lat/lon as average of all beacon lat/lons
        double refLat = sumLat / count;
        double refLon = sumLon / count;
        logger.info("Reference position set to Lat: {}, Lon: {}", refLat, refLon);

        // Prepare final results
        List<Map<String, Object>> results = new ArrayList<>();

        for (Map.Entry<Long, OffsetDateTime> entry : latestTimesMap.entrySet()) {
            Long deviceId = entry.getKey();
            OffsetDateTime latestTimeForDevice = entry.getValue();
            OffsetDateTime windowStart = latestTimeForDevice.minusMinutes(minutes);

            List<PositionSanitized> recentDetections =
                    positionRepository.findAllByDeviceIdAndDeviceTimeBetween(deviceId, windowStart, latestTimeForDevice);

            if (recentDetections.isEmpty()) {
                continue;
            }

            String deviceName = recentDetections.get(0).getDeviceName();

            // Collect beacon XY and distances for trilateration
            List<double[]> beaconXY = new ArrayList<>();
            List<Double> distances = new ArrayList<>();

            // This list will hold metadata for each detection
            List<Map<String, Object>> detectionsInfo = new ArrayList<>();

            for (PositionSanitized pos : recentDetections) {
                if (pos.getBeaconId() == null) {
                    continue;
                }

                BeaconEntity beacon = beaconRepository.findById(pos.getBeaconId()).orElse(null);
                if (beacon == null || beacon.getLatitude() == null || beacon.getLongitude() == null) {
                    continue;
                }

                // Convert beacon lat/lon -> local XY
                double[] xy = TrilaterationService.latlonToXY(
                        beacon.getLatitude(),
                        beacon.getLongitude(),
                        refLat,
                        refLon);

                // Convert RSSI -> distance
                int rssiValue = (pos.getRssi() == null) ? -100 : pos.getRssi();
                double d = TrilaterationService.rssiToDistance(rssiValue);

                beaconXY.add(xy);
                distances.add(d);

                // Enrich data: store relevant details about this detection
                Map<String, Object> detectionDetail = new HashMap<>();
                detectionDetail.put("beaconId", beacon.getId());
                detectionDetail.put("beaconName", beacon.getName());
                detectionDetail.put("beaconLatitude", beacon.getLatitude());
                detectionDetail.put("beaconLongitude", beacon.getLongitude());
                detectionDetail.put("rssi", rssiValue);
                detectionDetail.put("scanRssi", pos.getScanRssi());
                detectionDetail.put("deviceTime", pos.getDeviceTime());  // or fixTime, if relevant
                detectionsInfo.add(detectionDetail);
            }

            // Build result map for this device
            Map<String, Object> result = new HashMap<>();
            result.put("deviceId", deviceId);
            result.put("deviceName", deviceName);
            result.put("latestTimeForDevice", latestTimeForDevice.toString());
            result.put("participatingDetections", detectionsInfo);

            // Perform Trilateration if we have enough beacons
            if (!beaconXY.isEmpty()) {
                try {
                    double[] finalXY = TrilaterationService.solvePosition(beaconXY, distances);
                    double[] finalLatLon = TrilaterationService.xyToLatlon(finalXY[0], finalXY[1], refLat, refLon);

                    result.put("latitude", finalLatLon[0]);
                    result.put("longitude", finalLatLon[1]);
                    result.put("message", "Estimated location computed via trilateration.");
                } catch (Exception e) {
                    logger.error("Trilateration failed for device {}. Falling back to weighted average method.", deviceId);
                    double[] fallbackPosition = fallbackWeightedAverage(recentDetections);
                    result.put("latitude", fallbackPosition[0]);
                    result.put("longitude", fallbackPosition[1]);
                    result.put("message", "Estimated location computed using weighted average method.");
                }
            } else {
                // Fallback if no beacon data is available
                double[] fallbackPosition = fallbackWeightedAverage(recentDetections);
                result.put("latitude", fallbackPosition[0]);
                result.put("longitude", fallbackPosition[1]);
                result.put("message", "Estimated location computed using weighted average method (no beacons).");
            }

            results.add(result);
        }

        if (results.isEmpty()) {
            logger.warn("No valid positions could be estimated.");
            return ResponseEntity.ok("No valid positions could be estimated.");
        }

        logger.info("Trilateration process completed successfully for {} devices", results.size());
        return ResponseEntity.ok(results);
    }


    @GetMapping("/compute/all/v3")
    public ResponseEntity<?> computeTrilaterationFosddrAllDevices(@RequestParam(defaultValue = "5") int minutes) {
        logger.info("Starting trilateration process for all devices within the last {} minutes", minutes);

        Map<Long, OffsetDateTime> latestTimesMap = new HashMap<>();

        // Fetch the latest 1000 positions from the database
        int limit = 1000;
        logger.info("Fetching the latest {} positions from the database...", limit);
        List<PositionSanitized> positionsSubset = positionRepository.findTopPositions(limit);

        if (positionsSubset.isEmpty()) {
            logger.warn("No positions found in the database.");
            return ResponseEntity.ok("No recent detections found.");
        }

        // Determine the latest timestamp for each deviceId
        for (PositionSanitized pos : positionsSubset) {
            Long deviceId = pos.getDeviceId();
            OffsetDateTime currentTime = pos.getDeviceTime();
            OffsetDateTime existingTime = latestTimesMap.get(deviceId);

            if (existingTime == null || currentTime.isAfter(existingTime)) {
                latestTimesMap.put(deviceId, currentTime);
            }
        }

        if (latestTimesMap.isEmpty()) {
            logger.warn("No recent timestamps found for any device.");
            return ResponseEntity.ok("No recent detections found.");
        }

        logger.info("Computing reference latitude and longitude using beacon data...");
        List<BeaconEntity> allBeacons = beaconRepository.findAll();
        double sumLat = 0.0, sumLon = 0.0;
        int count = 0;
        for (BeaconEntity b : allBeacons) {
            if (b.getLatitude() != null && b.getLongitude() != null) {
                sumLat += b.getLatitude();
                sumLon += b.getLongitude();
                count++;
            }
        }

        if (count == 0) {
            logger.error("No valid beacon lat/lons found in the database.");
            return ResponseEntity.badRequest().body("No valid beacon lat/lons found.");
        }

        // Calculate reference lat/lon as average of all beacon lat/lons
        double refLat = sumLat / count;
        double refLon = sumLon / count;
        logger.info("Reference position set to Lat: {}, Lon: {}", refLat, refLon);

        // Prepare final results
        List<Map<String, Object>> devicesResults = new ArrayList<>();

        // This map will capture unique beacons used across all devices
        Map<Long, Map<String, Object>> uniqueBeaconsMap = new HashMap<>();

        for (Map.Entry<Long, OffsetDateTime> entry : latestTimesMap.entrySet()) {
            Long deviceId = entry.getKey();
            OffsetDateTime latestTimeForDevice = entry.getValue();
            OffsetDateTime windowStart = latestTimeForDevice.minusMinutes(minutes);

            List<PositionSanitized> recentDetections =
                    positionRepository.findAllByDeviceIdAndDeviceTimeBetween(deviceId, windowStart, latestTimeForDevice);

            if (recentDetections.isEmpty()) {
                continue;
            }

            String deviceName = recentDetections.get(0).getDeviceName();

            // Collect beacon XY and distances for trilateration
            List<double[]> beaconXY = new ArrayList<>();
            List<Double> distances = new ArrayList<>();

            // This list will hold metadata for each detection
            List<Map<String, Object>> detectionsInfo = new ArrayList<>();

            for (PositionSanitized pos : recentDetections) {
                if (pos.getBeaconId() == null) {
                    continue;
                }

                BeaconEntity beacon = beaconRepository.findById(pos.getBeaconId()).orElse(null);
                if (beacon == null || beacon.getLatitude() == null || beacon.getLongitude() == null) {
                    continue;
                }

                // Convert beacon lat/lon -> local XY
                double[] xy = TrilaterationService.latlonToXY(
                        beacon.getLatitude(),
                        beacon.getLongitude(),
                        refLat,
                        refLon);

                // Convert RSSI -> distance
                int rssiValue = (pos.getRssi() == null) ? -100 : pos.getRssi();
                double d = TrilaterationService.rssiToDistance(rssiValue);

                beaconXY.add(xy);
                distances.add(d);

                // Enrich data: store relevant details about this detection
                Map<String, Object> detectionDetail = new HashMap<>();
                detectionDetail.put("beaconId", beacon.getId());
                detectionDetail.put("beaconName", beacon.getName());
                detectionDetail.put("beaconLatitude", beacon.getLatitude());
                detectionDetail.put("beaconLongitude", beacon.getLongitude());
                detectionDetail.put("rssi", rssiValue);
                detectionDetail.put("scanRssi", pos.getScanRssi());
                detectionDetail.put("deviceTime", pos.getDeviceTime());  // or fixTime, if relevant
                detectionsInfo.add(detectionDetail);

                // Also track this beacon in our global map of unique beacons
                // Only add if not already present
                if (!uniqueBeaconsMap.containsKey(beacon.getId())) {
                    Map<String, Object> beaconMeta = new HashMap<>();
                    beaconMeta.put("beaconId", beacon.getId());
                    beaconMeta.put("beaconName", beacon.getName());
                    beaconMeta.put("beaconLatitude", beacon.getLatitude());
                    beaconMeta.put("beaconLongitude", beacon.getLongitude());
                    uniqueBeaconsMap.put(beacon.getId(), beaconMeta);
                }
            }

            // Build result map for this device
            Map<String, Object> deviceResult = new HashMap<>();
            deviceResult.put("deviceId", deviceId);
            deviceResult.put("deviceName", deviceName);
            deviceResult.put("latestTimeForDevice", latestTimeForDevice.toString());
            deviceResult.put("participatingDetections", detectionsInfo);

            // Perform Trilateration if we have enough beacons
            if (!beaconXY.isEmpty()) {
                try {
                    double[] finalXY = TrilaterationService.solvePosition(beaconXY, distances);
                    double[] finalLatLon = TrilaterationService.xyToLatlon(finalXY[0], finalXY[1], refLat, refLon);

                    deviceResult.put("latitude", finalLatLon[0]);
                    deviceResult.put("longitude", finalLatLon[1]);
                    deviceResult.put("message", "Estimated location computed via trilateration.");
                } catch (Exception e) {
                    logger.error("Trilateration failed for device {}. Falling back to weighted average method.", deviceId);
                    double[] fallbackPosition = fallbackWeightedAverage(recentDetections);
                    deviceResult.put("latitude", fallbackPosition[0]);
                    deviceResult.put("longitude", fallbackPosition[1]);
                    deviceResult.put("message", "Estimated location computed using weighted average method.");
                }
            } else {
                // Fallback if no beacon data is available
                double[] fallbackPosition = fallbackWeightedAverage(recentDetections);
                deviceResult.put("latitude", fallbackPosition[0]);
                deviceResult.put("longitude", fallbackPosition[1]);
                deviceResult.put("message", "Estimated location computed using weighted average method (no beacons).");
            }

            devicesResults.add(deviceResult);
        }

        if (devicesResults.isEmpty()) {
            logger.warn("No valid positions could be estimated.");
            return ResponseEntity.ok("No valid positions could be estimated.");
        }

        // Convert uniqueBeaconsMap to a list
        List<Map<String, Object>> uniqueBeaconsUsed = new ArrayList<>(uniqueBeaconsMap.values());

        // Build final response: one top-level object with both devices & unique beacons
        Map<String, Object> finalResponse = new HashMap<>();
        finalResponse.put("devices", devicesResults);
        finalResponse.put("allBeaconsUsed", uniqueBeaconsUsed);

        logger.info("Trilateration process completed successfully for {} devices", devicesResults.size());
        return ResponseEntity.ok(finalResponse);
    }

































    @GetMapping("/compute/all/csv")
    public ResponseEntity<?> fromcsv(@RequestParam(defaultValue = "5") int minutes) {
        logger.info("Starting trilateration process for all devices within the last {} minutes", minutes);

        Map<Long, OffsetDateTime> latestTimesMap = new HashMap<>();

        // Fetch the latest 1000 positions from the database
//        int limit = 1000;
//        logger.info("Fetching the latest {} positions from the database...", limit);

        // Read positions from JSON file instead of database
        String jsonFilePath = "C:\\Users\\hikev\\Desktop\\thesis\\2024-MA-Kevin-George\\services\\LocationSimulationService\\data.json";
        List<PositionSanitized> positionsSubset = new ArrayList<>();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Register Java 8 Date/Time module
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);  // Ignore unknown fields


        try {
            positionsSubset = objectMapper.readValue(
                    new File(jsonFilePath),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, PositionSanitized.class)
            );

            logger.info("Successfully read {} positions from JSON file.", positionsSubset.size());
        } catch (IOException e) {
            logger.error("Error reading JSON file: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error reading JSON file.");
        }

        var datsStrems = groupPositionsByTimeFrame(positionsSubset, 10);

        if (positionsSubset.isEmpty()) {
            logger.warn("No positions found in the JSON file.");
            return ResponseEntity.ok("No recent detections found.");
        }

        // Determine the latest timestamp for each deviceId
        for (PositionSanitized pos : positionsSubset) {
            Long deviceId = pos.getDeviceId();
            OffsetDateTime currentTime = pos.getDeviceTime();
            latestTimesMap.merge(deviceId, currentTime, (existingTime, newTime) -> existingTime.isAfter(newTime) ? existingTime : newTime);
        }

        if (latestTimesMap.isEmpty()) {
            logger.warn("No recent timestamps found for any device.");
            return ResponseEntity.ok("No recent detections found.");
        }

        logger.info("Computing reference latitude and longitude using beacon data...");
        List<BeaconEntity> allBeacons = beaconRepository.findAll();
        double sumLat = 0.0, sumLon = 0.0;
        int count = 0;

        for (BeaconEntity b : allBeacons) {
            if (b.getLatitude() != null && b.getLongitude() != null) {
                sumLat += b.getLatitude();
                sumLon += b.getLongitude();
                count++;
            }
        }

        if (count == 0) {
            logger.error("No valid beacon lat/lons found in the database.");
            return ResponseEntity.badRequest().body("No valid beacon lat/lons found.");
        }

        double refLat = sumLat / count;
        double refLon = sumLon / count;
        logger.info("Reference position set to Lat: {}, Lon: {}", refLat, refLon);

        List<Map<String, Object>> devicesResults = new ArrayList<>();
        Map<Long, Map<String, Object>> uniqueBeaconsMap = new HashMap<>();

        for (Map.Entry<Long, OffsetDateTime> entry : latestTimesMap.entrySet()) {
            Long deviceId = entry.getKey();
            OffsetDateTime latestTimeForDevice = entry.getValue();
            OffsetDateTime windowStart = latestTimeForDevice.minusMinutes(minutes);
//
//            List<PositionSanitized> recentDetections =
//                    positionRepository.findAllByDeviceIdAndDeviceTimeBetween(deviceId, windowStart, latestTimeForDevice);

//            List<PositionSanitized> recentDetections = positionsSubset.stream()
//                    .filter(pos -> pos.getDeviceId().equals(deviceId) &&
//                            (pos.getDeviceTime().isAfter(windowStart) || pos.getDeviceTime().isEqual(windowStart)) &&
//                            (pos.getDeviceTime().isBefore(latestTimeForDevice) || pos.getDeviceTime().isEqual(latestTimeForDevice)))
//                    .collect(Collectors.toList());

//            List<PositionSanitized> recentDetections = positionsSubset.stream()
//                    .filter(pos -> pos.getDeviceId().equals(deviceId) &&
//                            (pos.getDeviceTime().isAfter(windowStart) || pos.getDeviceTime().isEqual(windowStart)) &&
//                            (pos.getDeviceTime().isBefore(latestTimeForDevice) || pos.getDeviceTime().isEqual(latestTimeForDevice)))
//                    .sorted(Comparator.comparing(PositionSanitized::getRssi).reversed()) // Sort by RSSI descending
//                    .limit(4)  // Take top 3 positions with highest RSSI
//                    .collect(Collectors.toList());
            List<PositionSanitized> recentDetections = positionsSubset.stream()
                    .filter(pos -> pos.getDeviceId().equals(deviceId) &&
                            (pos.getDeviceTime().isAfter(windowStart) || pos.getDeviceTime().isEqual(windowStart)) &&
                            (pos.getDeviceTime().isBefore(latestTimeForDevice) || pos.getDeviceTime().isEqual(latestTimeForDevice)))
                    .sorted(Comparator.comparing(PositionSanitized::getDeviceTime, Comparator.reverseOrder())  // Sort by deviceTime descending
                            .thenComparing(pos -> {
                                Map<String, String> attributes = pos.getAttributes();
                                double rssi = pos.getRssi();
                                double scanRssi = attributes.containsKey("scanRssi") ?
                                        Double.parseDouble(attributes.get("scanRssi")) : Double.NEGATIVE_INFINITY;
                                return (rssi + scanRssi) / 2;
                            }, Comparator.reverseOrder()) // Sort by combined RSSI descending
                    )
                    .limit(3)  // Select top 3 positions
                    .collect(Collectors.toList());


            if (recentDetections.isEmpty()) {
                continue;
            }

            String deviceName = recentDetections.get(0).getDeviceName();
            List<double[]> beaconXY = new ArrayList<>();
            List<Double> distances = new ArrayList<>();
            List<Map<String, Object>> detectionsInfo = new ArrayList<>();

            for (PositionSanitized pos : recentDetections) {
                if (pos.getBeaconId() == null) {
                    continue;
                }

//                BeaconEntity beacon = beaconRepository.findById(pos.getBeaconId()).orElse(null);
                BeaconEntity beacon = beaconRepository.findByName(pos.getBeaconName()).orElse(null);
                if (beacon == null || beacon.getLatitude() == null || beacon.getLongitude() == null) {
                    continue;
                }

                double[] xy = TrilaterationService.latlonToXY(beacon.getLatitude(), beacon.getLongitude(), refLat, refLon);
                int rssiValue = (pos.getRssi() == null) ? -100 : pos.getRssi();
                double d = TrilaterationService.rssiToDistance(rssiValue);

                beaconXY.add(xy);
                distances.add(d);

                Map<String, Object> detectionDetail = new HashMap<>();
                detectionDetail.put("beaconId", beacon.getId());
                detectionDetail.put("beaconName", beacon.getName());
                detectionDetail.put("beaconLatitude", beacon.getLatitude());
                detectionDetail.put("beaconLongitude", beacon.getLongitude());
                detectionDetail.put("rssi", rssiValue);
                detectionDetail.put("scanRssi", pos.getScanRssi());
                detectionDetail.put("deviceTime", pos.getDeviceTime());
                detectionsInfo.add(detectionDetail);

                uniqueBeaconsMap.putIfAbsent(beacon.getId(), detectionDetail);
            }

            Map<String, Object> deviceResult = new HashMap<>();
            deviceResult.put("deviceId", deviceId);
            deviceResult.put("deviceName", deviceName);
            deviceResult.put("latestTimeForDevice", latestTimeForDevice.toString());
            deviceResult.put("participatingDetections", detectionsInfo);

            if (!beaconXY.isEmpty()) {
                try {
                    double[] finalXY = TrilaterationService.solvePosition(beaconXY, distances);
                    double[] finalLatLon = TrilaterationService.xyToLatlon(finalXY[0], finalXY[1], refLat, refLon);
                    BeaconEntity closestBeacon = findClosestBeacon(finalLatLon[0], finalLatLon[1], allBeacons);
                    if (closestBeacon != null) {
                        Map<String, Object> closestBeaconInfo = new HashMap<>();
                        closestBeaconInfo.put("beaconId", closestBeacon.getId());
                        closestBeaconInfo.put("beaconName", closestBeacon.getName());
                        closestBeaconInfo.put("beaconLatitude", closestBeacon.getLatitude());
                        closestBeaconInfo.put("beaconLongitude", closestBeacon.getLongitude());
                        deviceResult.put("closestBeacon", closestBeaconInfo);
                    }
                    deviceResult.put("latitude", finalLatLon[0]);
                    deviceResult.put("longitude", finalLatLon[1]);
                    deviceResult.put("message", "Estimated location computed via trilateration.");
                } catch (Exception e) {
                    logger.error("Trilateration failed for device {}.", deviceId);
                    double[] fallbackPosition = fallbackWeightedAverage(recentDetections);
                    deviceResult.put("latitude", fallbackPosition[0]);
                    deviceResult.put("longitude", fallbackPosition[1]);
                    deviceResult.put("message", "Estimated location computed using fallback method.");
                }
            }

            devicesResults.add(deviceResult);
        }

        if (devicesResults.isEmpty()) {
            logger.warn("No valid positions could be estimated.");
            return ResponseEntity.ok("No valid positions could be estimated.");
        }

        Map<String, Object> finalResponse = new HashMap<>();
        finalResponse.put("devices", devicesResults);
        finalResponse.put("allBeaconsUsed", new ArrayList<>(uniqueBeaconsMap.values()));

        logger.info("Trilateration process completed successfully for {} devices", devicesResults.size());
        return ResponseEntity.ok(finalResponse);
    }


    private BeaconEntity findClosestBeacon(double deviceLat, double deviceLon, List<BeaconEntity> beacons) {
        BeaconEntity closestBeacon = null;
        double minDistance = Double.MAX_VALUE;

        for (BeaconEntity beacon : beacons) {
            if (beacon.getLatitude() != null && beacon.getLongitude() != null) {
                double distance = calculateDistance(deviceLat, deviceLon, beacon.getLatitude(), beacon.getLongitude());
                if (distance < minDistance) {
                    minDistance = distance;
                    closestBeacon = beacon;
                }
            }
        }
        return closestBeacon;
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth's radius in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    /// testng

    public  Map<OffsetDateTime, List<PositionSanitized>> groupPositionsByTimeFrame(
            List<PositionSanitized> positions, int minutes) {

        if (positions == null || positions.isEmpty()) {
            return Collections.emptyMap();
        }

        // Find the earliest and latest device times
        OffsetDateTime earliestTime = positions.stream()
                .map(PositionSanitized::getDeviceTime)
                .min(OffsetDateTime::compareTo)
                .orElseThrow();

        OffsetDateTime latestTime = positions.stream()
                .map(PositionSanitized::getDeviceTime)
                .max(OffsetDateTime::compareTo)
                .orElseThrow();

        // Calculate time frames with the given interval (e.g., 5 minutes)
        Map<OffsetDateTime, List<PositionSanitized>> groupedPositions = new TreeMap<>();
        OffsetDateTime currentFrameStart = earliestTime.truncatedTo(ChronoUnit.MINUTES);

        while (!currentFrameStart.isAfter(latestTime)) {
            OffsetDateTime frameEnd = currentFrameStart.plusMinutes(minutes);
            OffsetDateTime finalCurrentFrameStart = currentFrameStart;

            List<PositionSanitized> positionsInFrame = positions.stream()
                    .filter(pos -> !pos.getDeviceTime().isBefore(finalCurrentFrameStart)
                            && pos.getDeviceTime().isBefore(frameEnd))
                    .collect(Collectors.toList());

            if (!positionsInFrame.isEmpty()) {
                groupedPositions.put(currentFrameStart, positionsInFrame);
            }

            currentFrameStart = frameEnd;
        }

        return groupedPositions;
    }


    private static final int RSSI_DIFFERENCE_THRESHOLD = 52; // RSSI difference range for equidistant assumption
    private static final int SCAN_RSSI_DIFFERENCE_THRESHOLD = 5; // Scan RSSI difference range
    private static final int MIN_SCAN_RSSI = -95; // Ignore outliers with scan RSSI below this value
    private static final int TIME_DIFFERENCE_SECONDS = 169; // Time window for filtering




//    public PositionSanitized findCowLocation(List<PositionSanitized> positions) {
//        if (positions.isEmpty()) {
//            return null;
//        }
//
//        positions.sort(Comparator.comparing(PositionSanitized::getDeviceTime));
//
//        List<PositionSanitized> bestMatches = new ArrayList<>();
//        double minRssiDifference = Double.MAX_VALUE;
//
//        // Compare each position pair to determine if the cow is in between the beacons
//        for (int i = 0; i < positions.size() - 1; i++) {
//            PositionSanitized pos1 = positions.get(i);
//            PositionSanitized pos2 = positions.get(i + 1);
//
//            double scanRssi1 = Double.parseDouble(pos1.getAttributes().get("scanRssi"));
//            double scanRssi2 = Double.parseDouble(pos2.getAttributes().get("scanRssi"));
//
//            double rssiDiff = Math.abs(pos1.getRssi() - pos2.getRssi());
//            double scanRssiDiff = Math.abs(scanRssi1 - scanRssi2);
//            long timeDiff = Math.abs(ChronoUnit.SECONDS.between(pos1.getDeviceTime(), pos2.getDeviceTime()));
//
//            if (rssiDiff <= RSSI_DIFFERENCE_THRESHOLD &&
//                    scanRssiDiff <= SCAN_RSSI_DIFFERENCE_THRESHOLD &&
//                    timeDiff <= TIME_DIFFERENCE_SECONDS) {
//
//                System.out.println("The cow is in between beacons " + pos1.getBeaconName() + " and " + pos2.getBeaconName());
//
//                if (rssiDiff < minRssiDifference) {
//                    minRssiDifference = rssiDiff;
//                    bestMatches.clear();
//                    bestMatches.add(pos1);
//                    bestMatches.add(pos2);
//                }
//            }
//        }
//
//        if (!bestMatches.isEmpty()) {
//            PositionSanitized pos1 = bestMatches.get(0);
//            PositionSanitized pos2 = bestMatches.get(1);
//
//            double estimatedLatitude = (pos1.getLatitude() + pos2.getLatitude()) / 2;
//            double estimatedLongitude = (pos1.getLongitude() + pos2.getLongitude()) / 2;
//
//            System.out.println("Selected optimal pair: " + pos1.getBeaconName() + " and " + pos2.getBeaconName());
//
//            return new PositionSanitized(
//                    null, "Estimated Position", Map.of(),
//                    "BLE", OffsetDateTime.now(), pos1.getDeviceTime(), pos2.getDeviceTime(),
//                    false, true, estimatedLatitude, estimatedLongitude, null, pos1.getDeviceId(),
//                    null, null, 0L, "Midway", 0, 0, null, 0L, "Device", 0.0
//            );
//        }
//
//        return null; // No valid position found
//    }



    public PositionSanitized findCowLocation(List<PositionSanitized> positions) {
        if (positions.isEmpty()) {
            return null;
        }

        positions.sort(Comparator.comparing(PositionSanitized::getDeviceTime));

        List<PositionSanitized> bestMatches = new ArrayList<>();
        double minDistance = Double.MAX_VALUE;
        double minRssiDifference = Double.MAX_VALUE;

        Map<String, Integer> beaconFrequency = new HashMap<>();

        for (int i = 0; i < positions.size(); i++) {
            for (int j = i + 1; j < positions.size(); j++) {
                PositionSanitized pos1 = positions.get(i);
                PositionSanitized pos2 = positions.get(j);

                double scanRssi1 = Double.parseDouble(pos1.getAttributes().get("scanRssi"));
                double scanRssi2 = Double.parseDouble(pos2.getAttributes().get("scanRssi"));

                double rssiDiff = Math.abs(pos1.getRssi() - pos2.getRssi());
                double scanRssiDiff = Math.abs(scanRssi1 - scanRssi2);
                long timeDiff = Math.abs(ChronoUnit.SECONDS.between(pos1.getDeviceTime(), pos2.getDeviceTime()));

                System.out.println("rssiDiff");
                System.out.println(rssiDiff);

                if (rssiDiff <= RSSI_DIFFERENCE_THRESHOLD &&
                        scanRssiDiff <= SCAN_RSSI_DIFFERENCE_THRESHOLD &&
                        timeDiff <= TIME_DIFFERENCE_SECONDS) {

                    List<BeaconEntity> beacons = beaconRepository.findByNameIn(List.of(pos1.getBeaconName(), pos2.getBeaconName()));

                    if (beacons.size() == 2) {
                        BeaconEntity beacon1 = beacons.get(0);
                        BeaconEntity beacon2 = beacons.get(1);

                        double distance = calculateDistance(beacon1.getLatitude(), beacon1.getLongitude(),
                                beacon2.getLatitude(), beacon2.getLongitude());

                        System.out.println("The cow is in between beacons " + pos1.getBeaconName() + " and " + pos2.getBeaconName());
                        System.out.println("Distance: " + distance);
                        System.out.println("RSSI Difference: " + rssiDiff);

                        // Count beacon occurrences to determine the most frequently appearing beacon
                        beaconFrequency.put(pos1.getBeaconName(), beaconFrequency.getOrDefault(pos1.getBeaconName(), 0) + 1);
                        beaconFrequency.put(pos2.getBeaconName(), beaconFrequency.getOrDefault(pos2.getBeaconName(), 0) + 1);

                        // Prioritize based on distance first, then RSSI difference
                        if (distance < minDistance ||
                                (distance == minDistance && rssiDiff < minRssiDifference) ||
                                (distance == minDistance && rssiDiff == minRssiDifference &&
                                        pos2.getDeviceTime().isAfter(bestMatches.get(1).getDeviceTime()))) {

                            minDistance = distance;
                            minRssiDifference = rssiDiff;
                            bestMatches.clear();
                            bestMatches.add(pos1);
                            bestMatches.add(pos2);
                        }
                    }
                }
            }
        }


        // uncommnet

//        if (!bestMatches.isEmpty()) {
//            PositionSanitized pos1 = bestMatches.get(0);
//            PositionSanitized pos2 = bestMatches.get(1);
//
//            double estimatedLatitude = (pos1.getLatitude() + pos2.getLatitude()) / 2;
//            double estimatedLongitude = (pos1.getLongitude() + pos2.getLongitude()) / 2;
//
//            System.out.println("Selected optimal pair: " + pos1.getBeaconName() + " and " + pos2.getBeaconName());
//
//            // Find the most frequently appearing beacon
//            String closestBeacon = beaconFrequency.entrySet().stream()
//                    .max(Map.Entry.comparingByValue())
//                    .get()
//                    .getKey();
//
//            System.out.println("The cow is most likely closest to beacon: " + closestBeacon);
//
//            return new PositionSanitized(
//                    null, closestBeacon, Map.of(),
//                    "BLE", OffsetDateTime.now(), pos1.getDeviceTime(), pos2.getDeviceTime(),
//                    false, true, estimatedLatitude, estimatedLongitude, null, pos1.getDeviceId(),
//                    null, null, 0L, "Midway", 0, 0, null, 0L, "Device", 0.0
//            );
//        }





























        // First pass: Collect all differences to compute dynamic thresholds
        List<Double> rssiDiffs = new ArrayList<>();
        List<Double> scanRssiDiffs = new ArrayList<>();
        List<Long> timeDiffs = new ArrayList<>();

        for (int i = 0; i < positions.size(); i++) {
            for (int j = i + 1; j < positions.size(); j++) {
                PositionSanitized pos1 = positions.get(i);
                PositionSanitized pos2 = positions.get(j);

                double rssiDiff = Math.abs(pos1.getRssi() - pos2.getRssi());
                rssiDiffs.add(rssiDiff);

                double scanRssi1 = Double.parseDouble(pos1.getAttributes().get("scanRssi"));
                double scanRssi2 = Double.parseDouble(pos2.getAttributes().get("scanRssi"));
                double scanRssiDiff = Math.abs(scanRssi1 - scanRssi2);
                scanRssiDiffs.add(scanRssiDiff);

                long timeDiff = Math.abs(ChronoUnit.SECONDS.between(pos1.getDeviceTime(), pos2.getDeviceTime()));
                timeDiffs.add(timeDiff);
            }
        }

// Compute dynamic thresholds using Median + 3*MAD
        double RSSI_DIFFERENCE_THRESHOLD = computeThreshold(rssiDiffs);
        double SCAN_RSSI_DIFFERENCE_THRESHOLD = computeThreshold(scanRssiDiffs);
        long TIME_DIFFERENCE_SECONDS = computeTimeThreshold(timeDiffs);

//        System.out.printf("Computed thresholds: RSSI_DIFFERENCE_THRESHOLD=%d, SCAN_RSSI_DIFFERENCE_THRESHOLD=%d, TIME_DIFFERENCE_SECONDS=%d%n",
//                RSSI_DIFFERENCE_THRESHOLD, SCAN_RSSI_DIFFERENCE_THRESHOLD, TIME_DIFFERENCE_SECONDS);



        double RSSI_DIFFERENCE_THRESHOLD_percentaile= percentile(rssiDiffs, 0.7);
        double SCAN_RSSI_DIFFERENCE_THRESHOLD_percentaile = percentile(scanRssiDiffs, .7);
        double TIME_DIFFERENCE_SECOND_percentaileS =  percentileDouble(timeDiffs, 0.7);


        logger.info("Computed Percentiles -> RSSI_DIFF: {}, SCAN_RSSI_DIFF: {}, TIME_DIFF: {}",
                RSSI_DIFFERENCE_THRESHOLD_percentaile,
                SCAN_RSSI_DIFFERENCE_THRESHOLD_percentaile,
                TIME_DIFFERENCE_SECOND_percentaileS);
        return null; // No valid position found
    }



    public static double percentile(List<Double> data, double percentile) {
        if (data.isEmpty()) {
            throw new IllegalArgumentException("Data list cannot be empty");
        }

        data.sort(Collections.reverseOrder()); // Sort in descending order
        double index = percentile * (data.size() - 1);
        int lowerIndex = (int) Math.floor(index);
        int upperIndex = (int) Math.ceil(index);

        if (lowerIndex == upperIndex) {
            return data.get(lowerIndex);
        }

        // Linear interpolation between lowerIndex and upperIndex
        double fraction = index - lowerIndex;
        return data.get(lowerIndex) + fraction * (data.get(upperIndex) - data.get(lowerIndex));
    }

    public static double percentileDouble(List<Long> data, double percentile) {
        if (data.isEmpty()) {
            throw new IllegalArgumentException("Data list cannot be empty");
        }

        data.sort(Collections.reverseOrder()); // Sort in descending order
        double index = percentile * (data.size() - 1);
        int lowerIndex = (int) Math.floor(index);
        int upperIndex = (int) Math.ceil(index);

        if (lowerIndex == upperIndex) {
            return data.get(lowerIndex);
        }

        // Linear interpolation between lowerIndex and upperIndex
        double fraction = index - lowerIndex;
        return data.get(lowerIndex) + fraction * (data.get(upperIndex) - data.get(lowerIndex));
    }

    // Helper methods to compute thresholds using Median + 3*MAD
    private double computeThreshold(List<Double> diffs) {
        if (diffs.isEmpty()) return 0.0;
        Collections.sort(diffs);
        double median = computeMedian(diffs);
        List<Double> deviations = diffs.stream().map(d -> Math.abs(d - median)).sorted().toList();
        double mad = computeMedian(deviations);
        return median + 3 * mad;
    }

    private long computeTimeThreshold(List<Long> timeDiffs) {
        if (timeDiffs.isEmpty()) return 0L;
        Collections.sort(timeDiffs);
        long median = computeMedianLong(timeDiffs);
        List<Long> deviations = timeDiffs.stream().map(t -> Math.abs(t - median)).sorted().toList();
        long mad = computeMedianLong(deviations);
        return median + 3 * mad;
    }

    private double computeMedian(List<Double> list) {
        int size = list.size();
        if (size % 2 == 0) {
            return (list.get(size/2 - 1) + list.get(size/2)) / 2.0;
        } else {
            return list.get(size/2);
        }
    }

    private long computeMedianLong(List<Long> list) {
        int size = list.size();
        if (size % 2 == 0) {
            return (list.get(size / 2 - 1) + list.get(size / 2)) / 2;
        } else {
            return list.get(size / 2);
        }
    }











    // Haversine formula to calculate the distance between two lat-long coordinates
//    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
//        final int EARTH_RADIUS = 6371; // Radius of the Earth in km
//        double latDistance = Math.toRadians(lat2 - lat1);
//        double lonDistance = Math.toRadians(lon2 - lon1);
//        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
//                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
//                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
//        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
//        return EARTH_RADIUS * c; // Distance in kilometers
//    }








    @GetMapping("/compute/all/test/csv")
    public ResponseEntity<?> test(@RequestParam(defaultValue = "5") int minutes) {
        logger.info("Starting trilateration process for all devices within the last {} minutes", minutes);

        Map<Long, OffsetDateTime> latestTimesMap = new HashMap<>();

        // Read positions from JSON file instead of database
        String jsonFilePath = "C:\\Users\\hikev\\Desktop\\thesis\\2024-MA-Kevin-George\\services\\LocationSimulationService\\data.json";
        List<PositionSanitized> positionsSubset = new ArrayList<>();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Register Java 8 Date/Time module
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);  // Ignore unknown fields


        try {
            positionsSubset = objectMapper.readValue(
                    new File(jsonFilePath),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, PositionSanitized.class)
            );

            logger.info("Successfully read {} positions from JSON file.", positionsSubset.size());
        } catch (IOException e) {
            logger.error("Error reading JSON file: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error reading JSON file.");
        }




        if (positionsSubset.isEmpty()) {
            logger.warn("No positions found in the JSON file.");
            return ResponseEntity.ok("No recent detections found.");
        }

        // Determine the latest timestamp for each deviceId
        for (PositionSanitized pos : positionsSubset) {
            Long deviceId = pos.getDeviceId();
            OffsetDateTime currentTime = pos.getDeviceTime(); // let's pass it
            latestTimesMap.merge(deviceId, currentTime, (existingTime, newTime) -> existingTime.isAfter(newTime) ? existingTime : newTime);
        }

        if (latestTimesMap.isEmpty()) {
            logger.warn("No recent timestamps found for any device.");
            return ResponseEntity.ok("No recent detections found.");
        }



        List<Map<String, Object>> devicesResults = new ArrayList<>();
        Map<Long, Map<String, Object>> uniqueBeaconsMap = new HashMap<>();

        for (Map.Entry<Long, OffsetDateTime> entry : latestTimesMap.entrySet()) {
            Long deviceId = entry.getKey();
//            OffsetDateTime latestTimeForDevice = entry.getValue();

//            OffsetDateTime latestTimeForDevice = OffsetDateTime.of(
//                    2022, 4, 26, 12, 30, 0, 0, ZoneOffset.UTC
//            );
//            OffsetDateTime latestTimeForDevice = OffsetDateTime.of(
//                    2022, 4, 26, 13, 07, 0, 0, ZoneOffset.UTC
//            );
//            OffsetDateTime windowStart = latestTimeForDevice;
////
//            OffsetDateTime windowStart = latestTimeForDevice.minusMinutes(minutes);
            OffsetDateTime windowStart;
            int x;

//            // Entry 1
//            windowStart = OffsetDateTime.of(2022, 4, 26, 13, 56, 25, 0, ZoneOffset.UTC);
//            x = (1 * 60) + 15;  // Δt = 01:15:53 -> 75 minutes
//            System.out.println("Observed Location: A5, X (minutes): " + x); //correct W10T_A7H_B-1-19 Tränke
//
//            // Entry 2
//            windowStart = OffsetDateTime.of(2022, 4, 26, 13, 56, 5, 0, ZoneOffset.UTC);
//            x = (0 * 60) + 2;  // Δt = 00:00:02 -> 0 minutes
//            System.out.println("Observed Location: T2, X (minutes): " + x); // not enough data

//            // Entry 3
//            windowStart = OffsetDateTime.of(2022, 4, 26, 13, 55, 10, 0, ZoneOffset.UTC);
//            x = (0 * 60) + 27;  // Δt = 00:00:27 -> 0 minutes
//            System.out.println("Observed Location: C7, X (minutes): " + x); // missig data from becon 5 at this time rage

//            // Entry 4
//            windowStart = OffsetDateTime.of(2022, 4, 26, 13, 52, 45, 0, ZoneOffset.UTC);
//            x = (0 * 60) + 1;  // Δt = 00:01:01 -> 1 minute
//            System.out.println("Observed Location: T3, X (minutes): " + x); // no data point becon 18
//
//            // Entry 5
//            windowStart = OffsetDateTime.of(2022, 4, 26, 13, 10, 32, 0, ZoneOffset.UTC);
//            x = (0 * 60) + 1;  // Δt = 00:01:21 -> 1 minute
//            System.out.println("Observed Location: T3, X (minutes): " + x);  // no data point becon 18
//
            // Entry 6
//            windowStart = OffsetDateTime.of(2022, 4, 26, 13, 6, 59, 0, ZoneOffset.UTC);
//            x = (0 * 60) + 3;  // Δt = 00:03:05 -> 3 minutes
//            System.out.println("Observed Location: T1, X (minutes): " + x); // correct W10T_A7H_B-1-19 Tränke

            // Entry 7
//            windowStart = OffsetDateTime.of(2022, 4, 26, 13, 5, 7, 0, ZoneOffset.UTC);
//            x = (10 * 60) + 10;  // Δt = 00:10:50 -> 10 minutes
//            System.out.println("Observed Location: F2_5;4;3;2, X (minutes): " + x);

            // Entry 8
            windowStart = OffsetDateTime.of(2022, 4, 26, 12, 23, 21, 0, ZoneOffset.UTC);
            x = 20;  // Δt = 00:43:12 -> 43 minutes
            System.out.println("Observed Location: C4, X (minutes): " + x); // corect
//            int x = 3;

            // Kalman Filters for BLE Positioning
            KalmanFilter latitudeFilter = new KalmanFilter(0.01, 0.5, 1);  // More responsive to movement
            KalmanFilter longitudeFilter = new KalmanFilter(0.01, 0.5, 1);
            KalmanFilter rssiFilter = new KalmanFilter(0.005, 12, 1);


            // Apply Kalman Filter to the positions


//            logger.info("Kalman Filter applied to all positions.");
//            List<PositionSanitized> recentDetections = positionsSubset.stream()
//                    .filter(pos -> pos.getDeviceId().equals(deviceId) &&
//                            (pos.getDeviceTime().isAfter(windowStart) || pos.getDeviceTime().isEqual(windowStart)) &&
//                            (pos.getDeviceTime().isBefore(windowStart.plusMinutes(x)) || pos.getDeviceTime().isEqual(windowStart.plusMinutes(x))) &&  // Adding X minutes to windowStart
//                            (128 - pos.getRssi() > -75
//                                    && Double.parseDouble(pos.getAttributes().get("scanRssi") )> -90
//                            )  // Filtering out weak signals
//                    )
//                    .sorted(Comparator.comparing(PositionSanitized::getDeviceTime, Comparator.reverseOrder())  // Sort by deviceTime descending
//                            .thenComparing(pos -> Math.abs(pos.getRssi()), Comparator.reverseOrder())  // Sort by RSSI descending
//                    )
//                    .collect(Collectors.toList());

//
            logger.info("Applying Kalman Filter to all positions and filtering data.");

            List<PositionSanitized> recentDetections = positionsSubset.stream()
                    .filter(pos -> pos.getDeviceId().equals(deviceId) &&
                            (pos.getDeviceTime().isAfter(windowStart) || pos.getDeviceTime().isEqual(windowStart)) &&
                            (pos.getDeviceTime().isBefore(windowStart.plusMinutes(x)) || pos.getDeviceTime().isEqual(windowStart.plusMinutes(x))) &&  // Adding X minutes to windowStart
                            (128 - pos.getRssi() > -75 &&
                                    Double.parseDouble(pos.getAttributes().get("scanRssi")) > -90) // Filtering out weak signals
                    )
                    .sorted(Comparator.comparing(PositionSanitized::getDeviceTime, Comparator.reverseOrder())  // Sort by deviceTime descending
                            .thenComparing(pos -> Math.abs(pos.getRssi()), Comparator.reverseOrder())  // Sort by RSSI descending
                    )
                    .map(pos -> {
                        // Apply Kalman Filter to latitude, longitude, and RSSI
                        pos.setLatitude(latitudeFilter.update(pos.getLatitude()));
                        pos.setLongitude(longitudeFilter.update(pos.getLongitude()));
                        pos.setRssi((int) rssiFilter.update(pos.getRssi()));
                        return pos;
                    })
                    .collect(Collectors.toList());

            logger.info("Kalman Filter applied to {} recent detections.", recentDetections.size());

            if (recentDetections.isEmpty()) {
                continue;
            }
            var k = findCowLocation(recentDetections);

            List<String> beaconNames = recentDetections.stream()
                    .filter(Objects::nonNull) // Avoid null elements
                    .map(PositionSanitized::getBeaconName)
                    .filter(Objects::nonNull) // Avoid null beacon names
                    .collect(Collectors.toList());



            String deviceName = recentDetections.get(0).getDeviceName();
            List<double[]> beaconXY = new ArrayList<>();
            List<Double> distances = new ArrayList<>();
            List<Map<String, Object>> detectionsInfo = new ArrayList<>();




            logger.info("Computing reference latitude and longitude using beacon data...");
//            List<BeaconEntity> allBeacons = beaconRepository.findAll();
            List<BeaconEntity> allBeacons = beaconRepository.findByNameIn(beaconNames);


            double sumLat = 0.0, sumLon = 0.0;
            int count = 0;

            for (BeaconEntity b : allBeacons) {
                if (b.getLatitude() != null && b.getLongitude() != null) {
                    sumLat += b.getLatitude();
                    sumLon += b.getLongitude();
                    count++;
                }
            }

            if (count == 0) {
                logger.error("No valid beacon lat/lons found in the database.");
                return ResponseEntity.badRequest().body("No valid beacon lat/lons found.");
            }

            double refLat = sumLat / count;
            double refLon = sumLon / count;
            logger.info("Reference position set to Lat: {}, Lon: {}", refLat, refLon);





            for (PositionSanitized pos : recentDetections) {
                if (pos.getBeaconId() == null) {
                    continue;
                }

//                BeaconEntity beacon = beaconRepository.findById(pos.getBeaconId()).orElse(null);
                BeaconEntity beacon = beaconRepository.findByName(pos.getBeaconName()).orElse(null);
                if (beacon == null || beacon.getLatitude() == null || beacon.getLongitude() == null) {
                    continue;
                }

                double[] xy = TrilaterationService.latlonToXY(beacon.getLatitude(), beacon.getLongitude(), refLat, refLon);
                int rssiValue = (pos.getRssi() == null) ? -100 : 128 -pos.getRssi();
                double d = TrilaterationService.rssiToDistance(rssiValue);

                beaconXY.add(xy);
                distances.add(d);

                Map<String, Object> detectionDetail = new HashMap<>();
                detectionDetail.put("beaconId", beacon.getId());
                detectionDetail.put("beaconName", beacon.getName());
                detectionDetail.put("beaconLatitude", beacon.getLatitude());
                detectionDetail.put("beaconLongitude", beacon.getLongitude());
                detectionDetail.put("rssi", rssiValue);
                detectionDetail.put("scanRssi", pos.getAttributes().get("scanRssi"));
                detectionDetail.put("deviceTime", pos.getDeviceTime());
                detectionsInfo.add(detectionDetail);

                uniqueBeaconsMap.putIfAbsent(beacon.getId(), detectionDetail);
            }

            Map<String, Object> deviceResult = new HashMap<>();
            deviceResult.put("deviceId", deviceId);
            deviceResult.put("deviceName", deviceName);
            deviceResult.put("latestTimeForDevice", windowStart.toString());
            deviceResult.put("participatingDetections", detectionsInfo);

            if (!beaconXY.isEmpty()) {
                try {
                    double[] finalXY = TrilaterationService.solvePosition(beaconXY, distances);
                    double[] finalLatLon = TrilaterationService.xyToLatlon(finalXY[0], finalXY[1], refLat, refLon);
                    BeaconEntity closestBeacon = findClosestBeacon(finalLatLon[0], finalLatLon[1], allBeacons);

                    var xz = recentDetections.stream()
                            .filter(position -> position.getBeaconName().equals(closestBeacon.getName()))  // Filter by beacon name
                            .sorted(Comparator.comparing(PositionSanitized::getDeviceTime))  // Order by device time
                            .collect(Collectors.toList()).stream().findFirst();


//                    var closestDetection = recentDetections.stream()
//                            .filter(position -> position.getBeaconName().equals("W10T_A7H_B-1-19 Tränke"))  // Filter by beacon name
//                            .min(Comparator.comparing(position -> position.getDistanceToBeacon(beaconRepository.findByName("W10T_A7H_B-1-19 Tränke").get())))  // Get closest
//                            .orElse(null);
//k.
                    var closestDetection = recentDetections.stream()
                            .filter(position -> position.getBeaconName().equals(k.getName()))  // Filter by beacon name
                            .min(Comparator.comparing(position -> position.getDistanceToBeacon(beaconRepository.findByName(k.getName()).get())))  // Get closest
                            .orElse(null);
                    if (closestBeacon != null) {
                        Map<String, Object> closestBeaconInfo = new HashMap<>();
                        closestBeaconInfo.put("beaconId", closestBeacon.getId());
                        closestBeaconInfo.put("beaconName", closestBeacon.getName());
                        closestBeaconInfo.put("beaconLatitude", closestBeacon.getLatitude());
                        closestBeaconInfo.put("beaconLongitude", closestBeacon.getLongitude());
                        closestBeaconInfo.put("deviceLatitude", closestDetection.getLatitude());
                        closestBeaconInfo.put("deviceLongitude", closestDetection.getLongitude());
                        deviceResult.put("closestBeacon", closestBeaconInfo);
                    }
                    deviceResult.put("latitude", finalLatLon[0]);
                    deviceResult.put("longitude", finalLatLon[1]);
                    deviceResult.put("message", "Estimated location computed via trilateration.");
                } catch (Exception e) {
                    logger.error("Trilateration failed for device {}.", deviceId);
                    double[] fallbackPosition = fallbackWeightedAverage(recentDetections);
                    deviceResult.put("latitude", fallbackPosition[0]);
                    deviceResult.put("longitude", fallbackPosition[1]);
                    deviceResult.put("message", "Estimated location computed using fallback method.");
                }
            }

            devicesResults.add(deviceResult);
        }

        if (devicesResults.isEmpty()) {
            logger.warn("No valid positions could be estimated.");
            return ResponseEntity.ok("No valid positions could be estimated.");
        }

        Map<String, Object> finalResponse = new HashMap<>();
        finalResponse.put("devices", devicesResults);
        finalResponse.put("allBeaconsUsed", new ArrayList<>(uniqueBeaconsMap.values()));

        logger.info("Trilateration process completed successfully for {} devices", devicesResults.size());
        return ResponseEntity.ok(finalResponse);
    }

































    @GetMapping("/compute/all/test2time/csv")
    public ResponseEntity<?> test(
            @RequestParam(defaultValue = "5") int minutes,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime endTime,
            @RequestParam(required = false) Long deviceId) {

        logger.info("Starting trilateration process for all devices between {} and {}", startTime, endTime);

        if (startTime == null || endTime == null) {
            return ResponseEntity.badRequest().body("Start time and end time must be provided.");
        }

        // Read positions from JSON file
        String jsonFilePath = "C:\\Users\\hikev\\Desktop\\thesis\\2024-MA-Kevin-George\\services\\LocationSimulationService\\data.json";
        List<PositionSanitized> positionsSubset = new ArrayList<>();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            positionsSubset = objectMapper.readValue(
                    new File(jsonFilePath),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, PositionSanitized.class)
            );

            logger.info("Successfully read {} positions from JSON file.", positionsSubset.size());
        } catch (IOException e) {
            logger.error("Error reading JSON file: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error reading JSON file.");
        }

        if (positionsSubset.isEmpty()) {
            logger.warn("No positions found in the JSON file.");
            return ResponseEntity.ok("No recent detections found.");
        }

        // Filter by deviceId if provided
        if (deviceId != null) {
            positionsSubset = positionsSubset.stream()
                    .filter(pos -> pos.getDeviceId().equals(deviceId))
                    .collect(Collectors.toList());
        }

        // Filter by the provided time range
        List<PositionSanitized> filteredPositions = positionsSubset.stream()
                .filter(pos -> !pos.getDeviceTime().isBefore(startTime) && !pos.getDeviceTime().isAfter(endTime))
                .collect(Collectors.toList());

        if (filteredPositions.isEmpty()) {
            return ResponseEntity.ok("No data available for the specified time range.");
        }

        // Perform trilateration on the filtered positions
        return performTrilateration(filteredPositions);
    }















    private ResponseEntity<?> performTrilateration(List<PositionSanitized> filteredPositions) {
        List<Map<String, Object>> devicesResults = new ArrayList<>();

        logger.info("Computing reference latitude and longitude using beacon data...");
        List<BeaconEntity> allBeacons = beaconRepository.findAll();
        double sumLat = 0.0, sumLon = 0.0;
        int count = 0;

        for (BeaconEntity b : allBeacons) {
            if (b.getLatitude() != null && b.getLongitude() != null) {
                sumLat += b.getLatitude();
                sumLon += b.getLongitude();
                count++;
            }
        }

        if (count == 0) {
            logger.error("No valid beacon lat/lons found in the database.");
            return ResponseEntity.badRequest().body("No valid beacon lat/lons found.");
        }

        double refLat = sumLat / count;
        double refLon = sumLon / count;
        logger.info("Reference position set to Lat: {}, Lon: {}", refLat, refLon);

        for (PositionSanitized pos : filteredPositions) {
            if (pos.getBeaconId() == null) {
                continue;
            }

            BeaconEntity beacon = beaconRepository.findByName(pos.getBeaconName()).orElse(null);
            if (beacon == null || beacon.getLatitude() == null || beacon.getLongitude() == null) {
                continue;
            }

            double[] xy = TrilaterationService.latlonToXY(beacon.getLatitude(), beacon.getLongitude(), refLat, refLon);
            int rssiValue = (pos.getRssi() == null) ? -100 : 128 - pos.getRssi();
            double d = TrilaterationService.rssiToDistance(rssiValue);

            Map<String, Object> deviceResult = new HashMap<>();
            deviceResult.put("deviceId", pos.getDeviceId());
            deviceResult.put("latitude", xy[0]);
            deviceResult.put("longitude", xy[1]);
            deviceResult.put("message", "Estimated location computed via trilateration.");

            devicesResults.add(deviceResult);
        }

        if (devicesResults.isEmpty()) {
            logger.warn("No valid positions could be estimated.");
            return ResponseEntity.ok("No valid positions could be estimated.");
        }

        Map<String, Object> finalResponse = new HashMap<>();
        finalResponse.put("devices", devicesResults);

        return ResponseEntity.ok(finalResponse);
    }












    // Helper method to safely parse scanRssi
    private static double parseDouble(Object value, double defaultValue) {
        if (value == null) {
            return defaultValue;  // Handle null gracefully
        }
        try {
            return Double.parseDouble(value.toString());  // Convert safely
        } catch (NumberFormatException e) {
            return defaultValue;  // Return default if parsing fails
        }
    }

}
