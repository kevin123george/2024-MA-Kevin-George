package com.cattleDb.DataInjectionService;


import com.cattleDb.DataInjectionService.models.BeaconEntity;
import com.cattleDb.DataInjectionService.models.Position;
import com.cattleDb.DataInjectionService.repository.BeaconRepository;
import com.cattleDb.DataInjectionService.repository.PositionRepository;
import com.cattleDb.DataInjectionService.service.TrilaterationService; // your custom code
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Example REST controller for trilateration
 */
@RestController
@RequestMapping("/api/trilateration")
public class TrilaterationController {


    private static final Logger logger = LoggerFactory.getLogger(TrilaterationController.class);

    @Autowired
    private PositionRepository positionRepository;

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
        int limit = 100;
        List<Position> positionsSubset = positionRepository.findTopPositions(limit);

        // Determine the latest timestamp for each deviceId
        for (Position pos : positionsSubset) {
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
        List<Position> recentDetections = positionRepository
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

        for (Position pos : recentDetections) {
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
        List<Position> positionsSubset = positionRepository.findTopPositions(limit);

        if (positionsSubset.isEmpty()) {
            logger.warn("No positions found in the database.");
            return ResponseEntity.ok("No recent detections found.");
        }

        logger.info("Processing {} positions to determine latest timestamps for each device...", positionsSubset.size());

        // Determine the latest timestamp for each deviceId
        for (Position pos : positionsSubset) {
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
            List<Position> recentDetections = positionRepository.findAllByDeviceIdAndDeviceTimeBetween(deviceId, windowStart, latestTimeForDevice);

            if (recentDetections.isEmpty()) {
                logger.warn("No recent detections found for device {}", deviceId);
                continue;
            }

            List<double[]> beaconXY = new ArrayList<>();
            List<Double> distances = new ArrayList<>();

            for (Position pos : recentDetections) {
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
    private double[] fallbackWeightedAverage(List<Position> recentDetections) {
        double sumLat = 0.0, sumLon = 0.0, weightSum = 0.0;

        for (Position pos : recentDetections) {
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


}
