package com.cattleDB.LocationSimulationService.controller;

import com.cattleDB.LocationSimulationService.Service.TrilaterationService;
import com.cattleDB.LocationSimulationService.models.BeaconEntity;
import com.cattleDB.LocationSimulationService.models.PositionSanitized;
import com.cattleDB.LocationSimulationService.repository.BeaconRepository;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.cattleDB.LocationSimulationService.DBSCANLocalizationExample;

@RestController
@RequestMapping("/api/proximity")
public class ProximityFind {


    private static final Logger logger = LoggerFactory.getLogger(TrilaterationController.class);

    @Autowired
    private BeaconRepository beaconRepository;



//    private static final int RSSI_DIFFERENCE_THRESHOLD = 0; // RSSI difference range for equidistant assumption
//    private static final int SCAN_RSSI_DIFFERENCE_THRESHOLD = 1; // Scan RSSI difference range
//    private static final int TIME_DIFFERENCE_SECONDS = 2735; // Time window for filtering




    @GetMapping("/compute/all/test/csv")
//    public ResponseEntity<?> test(@RequestParam(defaultValue = "5") int minutes) {
//        logger.info("Starting trilateration process for all devices within the last {} minutes", minutes);
//
//        Map<Long, OffsetDateTime> latestTimesMap = new HashMap<>();
//
//        // Read positions from JSON file instead of database
//        String jsonFilePath = "C:\\Users\\hikev\\Desktop\\thesis\\2024-MA-Kevin-George\\services\\LocationSimulationService\\data.json";
//        List<PositionSanitized> positionsSubset = new ArrayList<>();
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.registerModule(new JavaTimeModule()); // Register Java 8 Date/Time module
//        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);  // Ignore unknown fields
//
//
//        try {
//            positionsSubset = objectMapper.readValue(
//                    new File(jsonFilePath),
//                    objectMapper.getTypeFactory().constructCollectionType(List.class, PositionSanitized.class)
//            );
//
//            logger.info("Successfully read {} positions from JSON file.", positionsSubset.size());
//        } catch (IOException e) {
//            logger.error("Error reading JSON file: {}", e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error reading JSON file.");
//        }
//
//
//
//        if (positionsSubset.isEmpty()) {
//            logger.warn("No positions found in the JSON file.");
//            return ResponseEntity.ok("No recent detections found.");
//        }
//
//        // Determine the latest timestamp for each deviceId
//        for (PositionSanitized pos : positionsSubset) {
//            Long deviceId = pos.getDeviceId();
//            OffsetDateTime currentTime = pos.getDeviceTime(); // let's pass it
//            latestTimesMap.merge(deviceId, currentTime, (existingTime, newTime) -> existingTime.isAfter(newTime) ? existingTime : newTime);
//        }
//
//        if (latestTimesMap.isEmpty()) {
//            logger.warn("No recent timestamps found for any device.");
//            return ResponseEntity.ok("No recent detections found.");
//        }
//
//        List<Map<String, Object>> devicesResults = new ArrayList<>();
//        Map<Long, Map<String, Object>> uniqueBeaconsMap = new HashMap<>();
//
//        for (Map.Entry<Long, OffsetDateTime> entry : latestTimesMap.entrySet()) {
//            Long deviceId = entry.getKey();
//
//            int entryNumber = 8; // Change this to 1, 6, or 8 for testing
//
//            OffsetDateTime windowStart;
//            int x;
//
//            switch (entryNumber) {
//                case 1:
//                    windowStart = OffsetDateTime.of(2022, 4, 26, 13, 56, 25, 0, ZoneOffset.UTC);
//                    x = (1 * 60) + 15;  // 75 minutes
//                    System.out.println("Observed Location: A5, X (minutes): " + x);  //correct W10T_A7H_B-1-19 Tränke
//                    break;
//
//                case 6:
//                    windowStart = OffsetDateTime.of(2022, 4, 26, 13, 6, 59, 0, ZoneOffset.UTC);
//                    x = (0 * 60) + 3;  // 3 minutes
//                    System.out.println("Observed Location: T1, X (minutes): " + x);// correct W10T_A7H_B-1-19 Tränke
//                    break;
//
//                case 8:
//                    windowStart = OffsetDateTime.of(2022, 4, 26, 12, 23, 21, 0, ZoneOffset.UTC);
//                    x = 20;  // 43 minutes
//                    System.out.println("Observed Location: C4, X (minutes): " + x);
//                    break;
//
//                default:
//                    windowStart = OffsetDateTime.of(2022, 4, 26, 13, 56, 25, 0, ZoneOffset.UTC);
//                    x = (1 * 60) + 15;  // 75 minutes
//                    System.out.println("Default : A5, X (minutes): " + x);//                    return;
//            }
//
//            // Kalman Filters for BLE Positioning
//            KalmanFilter latitudeFilter = new KalmanFilter(0.01, 0.5, 1);
//            KalmanFilter longitudeFilter = new KalmanFilter(0.01, 0.5, 1);
//            KalmanFilter rssiFilter = new KalmanFilter(0.005, 12, 1);
//
//
//            // Apply Kalman Filter to the positions
//            List<PositionSanitized> recentDetections;
//
//            boolean kalmanfiler = true;
//            if (kalmanfiler){
//                logger.info("Kalman Filter applied to all positions.");
//
//                logger.info("Applying Kalman Filter to all positions and filtering data.");
//
//               recentDetections = positionsSubset.stream()
//                        .filter(pos -> pos.getDeviceId().equals(deviceId) &&
//                                (pos.getDeviceTime().isAfter(windowStart) || pos.getDeviceTime().isEqual(windowStart)) &&
//                                (pos.getDeviceTime().isBefore(windowStart.plusMinutes(x)) || pos.getDeviceTime().isEqual(windowStart.plusMinutes(x))) &&  // Adding X minutes to windowStart
//                                (128 - pos.getRssi() > -75 &&
//                                        Double.parseDouble(pos.getAttributes().get("scanRssi")) > -90) // Filtering out weak signals
//                        )
//                        .sorted(Comparator.comparing(PositionSanitized::getDeviceTime, Comparator.reverseOrder())  // Sort by deviceTime descending
//                                .thenComparing(pos -> Math.abs(pos.getRssi()), Comparator.reverseOrder())  // Sort by RSSI descending
//                        )
//                        .map(pos -> {
//                            // Apply Kalman Filter to latitude, longitude, and RSSI
//                            pos.setLatitude(latitudeFilter.update(pos.getLatitude()));
//                            pos.setLongitude(longitudeFilter.update(pos.getLongitude()));
//                            pos.setRssi((int) rssiFilter.update(pos.getRssi()));
//                            return pos;
//                        })
//                        .collect(Collectors.toList());
//            }else{
//
//               recentDetections = positionsSubset.stream()
//                        .filter(pos -> pos.getDeviceId().equals(deviceId) &&
//                                (pos.getDeviceTime().isAfter(windowStart) || pos.getDeviceTime().isEqual(windowStart)) &&
//                                (pos.getDeviceTime().isBefore(windowStart.plusMinutes(x)) || pos.getDeviceTime().isEqual(windowStart.plusMinutes(x))) &&  // Adding X minutes to windowStart
//                                (128 - pos.getRssi() > -75
//                                        && Double.parseDouble(pos.getAttributes().get("scanRssi") )> -90
//                                )  // Filtering out weak signals
//                        )
//                        .sorted(Comparator.comparing(PositionSanitized::getDeviceTime, Comparator.reverseOrder())  // Sort by deviceTime descending
//                                .thenComparing(pos -> Math.abs(pos.getRssi()), Comparator.reverseOrder())  // Sort by RSSI descending
//                        )
//                        .collect(Collectors.toList());
//            }
//
//
//
//
//            logger.info("Kalman Filter applied to {} recent detections.", recentDetections.size());
//
//            if (recentDetections.isEmpty()) {
//                continue;
//            }
//
//            var k = findCowLocation(recentDetections);
//
//            List<String> beaconNames = recentDetections.stream()
//                    .filter(Objects::nonNull) // Avoid null elements
//                    .map(PositionSanitized::getBeaconName)
//                    .filter(Objects::nonNull) // Avoid null beacon names
//                    .collect(Collectors.toList());
//
//
//
//            String deviceName = recentDetections.get(0).getDeviceName();
//            List<double[]> beaconXY = new ArrayList<>();
//            List<Double> distances = new ArrayList<>();
//            List<Map<String, Object>> detectionsInfo = new ArrayList<>();
//
//
//
//
//            logger.info("Computing reference latitude and longitude using beacon data...");
//            List<BeaconEntity> allBeacons = beaconRepository.findByNameIn(beaconNames);
//
//
//            double sumLat = 0.0, sumLon = 0.0;
//            int count = 0;
//
//            for (BeaconEntity b : allBeacons) {
//                if (b.getLatitude() != null && b.getLongitude() != null) {
//                    sumLat += b.getLatitude();
//                    sumLon += b.getLongitude();
//                    count++;
//                }
//            }
//
//            if (count == 0) {
//                logger.error("No valid beacon lat/lons found in the database.");
//                return ResponseEntity.badRequest().body("No valid beacon lat/lons found.");
//            }
//
//            double refLat = sumLat / count;
//            double refLon = sumLon / count;
//            logger.info("Reference position set to Lat: {}, Lon: {}", refLat, refLon);
//
//            for (PositionSanitized pos : recentDetections) {
//                if (pos.getBeaconId() == null) {
//                    continue;
//                }
//
//                BeaconEntity beacon = beaconRepository.findByName(pos.getBeaconName()).orElse(null);
//                if (beacon == null || beacon.getLatitude() == null || beacon.getLongitude() == null) {
//                    continue;
//                }
//
//                double[] xy = TrilaterationService.latlonToXY(beacon.getLatitude(), beacon.getLongitude(), refLat, refLon);
//                int rssiValue = (pos.getRssi() == null) ? -100 : 128 -pos.getRssi();
//                double d = TrilaterationService.rssiToDistance(rssiValue);
//
//                beaconXY.add(xy);
//                distances.add(d);
//
//                Map<String, Object> detectionDetail = new HashMap<>();
//                detectionDetail.put("beaconId", beacon.getId());
//                detectionDetail.put("beaconName", beacon.getName());
//                detectionDetail.put("beaconLatitude", beacon.getLatitude());
//                detectionDetail.put("beaconLongitude", beacon.getLongitude());
//                detectionDetail.put("rssi", rssiValue);
//                detectionDetail.put("scanRssi", pos.getAttributes().get("scanRssi"));
//                detectionDetail.put("deviceTime", pos.getDeviceTime());
//                detectionsInfo.add(detectionDetail);
//
//                uniqueBeaconsMap.putIfAbsent(beacon.getId(), detectionDetail);
//            }
//
//            Map<String, Object> deviceResult = new HashMap<>();
//            deviceResult.put("deviceId", deviceId);
//            deviceResult.put("deviceName", deviceName);
//            deviceResult.put("latestTimeForDevice", windowStart.toString());
//            deviceResult.put("participatingDetections", detectionsInfo);
//
//            if (!beaconXY.isEmpty()) {
//                try {
//                    double[] finalXY = TrilaterationService.solvePosition(beaconXY, distances);
//                    double[] finalLatLon = TrilaterationService.xyToLatlon(finalXY[0], finalXY[1], refLat, refLon);
//                    BeaconEntity closestBeacon = findClosestBeacon(finalLatLon[0], finalLatLon[1], allBeacons);
//
//                    var closestDetection = recentDetections.stream()
//                            .filter(position -> position.getBeaconName().equals(k.getName()))  // Filter by beacon name
//                            .min(Comparator.comparing(position -> position.getDistanceToBeacon(beaconRepository.findByName(k.getName()).get())))  // Get closest
//                            .orElse(null);
//                    if (closestBeacon != null) {
//                        Map<String, Object> closestBeaconInfo = new HashMap<>();
//                        closestBeaconInfo.put("beaconId", closestBeacon.getId());
//                        closestBeaconInfo.put("beaconName", closestBeacon.getName());
//                        closestBeaconInfo.put("beaconLatitude", closestBeacon.getLatitude());
//                        closestBeaconInfo.put("beaconLongitude", closestBeacon.getLongitude());
//                        closestBeaconInfo.put("deviceLatitude", closestDetection.getLatitude());
//                        closestBeaconInfo.put("deviceLongitude", closestDetection.getLongitude());
//                        deviceResult.put("closestBeacon", closestBeaconInfo);
//                    }
//                    deviceResult.put("latitude", finalLatLon[0]);
//                    deviceResult.put("longitude", finalLatLon[1]);
//                    deviceResult.put("message", "Estimated location computed via trilateration.");
//                } catch (Exception e) {
//                    logger.error("Trilateration failed for device {}.", deviceId);
//                    double[] fallbackPosition = fallbackWeightedAverage(recentDetections);
//                    deviceResult.put("latitude", fallbackPosition[0]);
//                    deviceResult.put("longitude", fallbackPosition[1]);
//                    deviceResult.put("message", "Estimated location computed using fallback method.");
//                }
//            }
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
//        finalResponse.put("allBeaconsUsed", new ArrayList<>(uniqueBeaconsMap.values()));
//
//        logger.info("Trilateration process completed successfully for {} devices", devicesResults.size());
//        return ResponseEntity.ok(finalResponse);
//    }

    public PositionSanitized findCowLocation(List<PositionSanitized> allpositions) {
        if (allpositions.isEmpty()) {
            return null;
        }

        List<List<PositionSanitized>>  min10split = splitPositionsByTime(allpositions, 5);
        Map<Integer, Map<String, String>> debugLog = new HashMap<>();

        int count = 0;
        for (List<PositionSanitized> positions : min10split) {
            count = count+1;
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

            if (rssiDiffs.isEmpty() || scanRssiDiffs.isEmpty() || timeDiffs.isEmpty()) {
                continue;
            }
            double RSSI_DIFFERENCE_THRESHOLD= percentile(rssiDiffs, 0.8);
            double SCAN_RSSI_DIFFERENCE_THRESHOLD = percentile(scanRssiDiffs, .8);
            double TIME_DIFFERENCE_SECONDS =  percentileDouble(timeDiffs, 0.9);


            logger.info("Computed Percentiles -> RSSI_DIFF: {}, SCAN_RSSI_DIFF: {}, TIME_DIFF: {}",
                    RSSI_DIFFERENCE_THRESHOLD,
                    SCAN_RSSI_DIFFERENCE_THRESHOLD,
                    TIME_DIFFERENCE_SECONDS);
            positions.sort(Comparator.comparing(PositionSanitized::getDeviceTime));

            List<PositionSanitized> bestMatches = new ArrayList<>();


            Map<String, BeaconPairStats> beaconPairStats = new HashMap<>();

            double minDistance = Double.MAX_VALUE;
            double minRssiDifference = Double.MAX_VALUE;

            Map<String, Integer> beaconFrequency = new HashMap<>();
            Map<String, Integer> beaconPairCount = new HashMap<>();

            for (int i = 0; i < positions.size(); i++) {
                for (int j = i + 1; j < positions.size(); j++) {
                    PositionSanitized pos1 = positions.get(i);
                    PositionSanitized pos2 = positions.get(j);

                    double scanRssi1 = Double.parseDouble(pos1.getAttributes().get("scanRssi"));
                    double scanRssi2 = Double.parseDouble(pos2.getAttributes().get("scanRssi"));

                    double rssiDiff = Math.abs(pos1.getRssi() - pos2.getRssi());
                    double scanRssiDiff = Math.abs(scanRssi1 - scanRssi2);
                    long timeDiff = Math.abs(ChronoUnit.SECONDS.between(pos1.getDeviceTime(), pos2.getDeviceTime()));


                    if (rssiDiff <= RSSI_DIFFERENCE_THRESHOLD &&
                            scanRssiDiff <= SCAN_RSSI_DIFFERENCE_THRESHOLD &&
                            timeDiff <= TIME_DIFFERENCE_SECONDS) {

                        List<BeaconEntity> beacons = beaconRepository.findByNameIn(List.of(pos1.getBeaconName(), pos2.getBeaconName()));

                        if (beacons.size() == 2) {
                            BeaconEntity beacon1 = beacons.get(0);
                            BeaconEntity beacon2 = beacons.get(1);

                            double distance = calculateDistance(beacon1.getLatitude(), beacon1.getLongitude(),
                                    beacon2.getLatitude(), beacon2.getLongitude());
                            if (distance > 0.008){
                                continue;
                            }

                            System.out.println("The cow is in between beacons " + pos1.getBeaconName() + " and " + pos2.getBeaconName());
                            System.out.println("Distance: " + distance);
                            System.out.println("RSSI Difference: " + rssiDiff);

                            String beaconPair = pos1.getBeaconName().compareTo(pos2.getBeaconName()) < 0
                                    ? pos1.getBeaconName() + " - " + pos2.getBeaconName()
                                    : pos2.getBeaconName() + " - " + pos1.getBeaconName();

                            // Increment the count
                            beaconPairCount.put(beaconPair, beaconPairCount.getOrDefault(beaconPair, 0) + 1);



                            beaconPairStats.merge(beaconPair,
                                    new BeaconPairStats(),
                                    (existing, newStats) -> existing.addMeasurement(rssiDiff, scanRssiDiff, timeDiff));


                            // Count beacon occurrences to determine the most frequently appearing beacon
                            beaconFrequency.put(pos1.getBeaconName(), beaconFrequency.getOrDefault(pos1.getBeaconName(), 0) + 1);
                            beaconFrequency.put(pos2.getBeaconName(), beaconFrequency.getOrDefault(pos2.getBeaconName(), 0) + 1);

                            // Prioritize based on distance first, then RSSI difference
//                            if (distance < minDistance ||
//                                    (distance == minDistance && rssiDiff < minRssiDifference) ||
//                                    (distance == minDistance && rssiDiff == minRssiDifference &&
//                                            pos2.getDeviceTime().isAfter(bestMatches.get(1).getDeviceTime()))) {
                            if (true) {

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

//            for (Map.Entry<String, Integer> entry : beaconPairCount.entrySet()) {
//                System.out.println(entry.getKey() + ": " + entry.getValue());
//            }




            if (!bestMatches.isEmpty()) {

                System.out.println("----------------------------------");
                System.out.println(bestMatches.size());
                System.out.println("----------------------------------");
                PositionSanitized pos1 = bestMatches.get(0);
                PositionSanitized pos2 = bestMatches.get(1);

                double estimatedLatitude = (pos1.getLatitude() + pos2.getLatitude()) / 2;
                double estimatedLongitude = (pos1.getLongitude() + pos2.getLongitude()) / 2;

                System.out.println("Selected optimal pair: " + pos1.getBeaconName() + " and " + pos2.getBeaconName());
                Map<String, String> logDetails = new HashMap<>();
                String selectedPairLog = "Selected optimal pair: " + pos1.getBeaconName() + " and " + pos2.getBeaconName();
                logDetails.put("Optimal Pair", selectedPairLog);

                // Find the most frequently appearing beacon
                String closestBeacon = beaconFrequency.entrySet().stream()
                        .max(Map.Entry.comparingByValue())
                        .get()
                        .getKey();

                System.out.println("The cow is most likely closest to beacon: " + closestBeacon);

                String closestBeaconLog = "The cow is most likely closest to beacon: " + closestBeacon;
                logDetails.put("Closest Beacon", closestBeaconLog);
                debugLog.put(count, logDetails);

                return new PositionSanitized(
                        null, closestBeacon, Map.of(),
                        "BLE", OffsetDateTime.now(), pos1.getDeviceTime(), pos2.getDeviceTime(),
                        false, true, estimatedLatitude, estimatedLongitude, null, pos1.getDeviceId(),
                        null, null, 0L, "Midway", 0, 0, null, 0L, "Device", 0.0
                );
            }


        }



        return null; // No valid position found
    }



    public static double calculatePositionRatio(double beacon1Rssi, double beacon2Rssi) {
        if (beacon1Rssi + beacon2Rssi == 0) {
            return 0.5; // Default midpoint if RSSI values are zero (unlikely case)
        }
        return Math.abs(beacon1Rssi) / (Math.abs(beacon1Rssi) + Math.abs(beacon2Rssi));
    }

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
    public static double average(List<Double> data) {
        if (data.isEmpty()) {
            throw new IllegalArgumentException("Data list cannot be empty");
        }

        return data.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }



    public static double percentileDouble(List<Long> data, double percentile) {
        if (data.isEmpty()) {
            throw new IllegalArgumentException("Data list cannot be empty");
        }

//        data.sort(Collections.reverseOrder()); // Sort in descending order
        Collections.sort(data);
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

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth's radius in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }




    record BeaconPairStats(
            int count,
            double totalRssiDiff,
            double totalScanRssiDiff,
            long totalTimeDiff
    ) {
        // Constructor with default values
        public BeaconPairStats() {
            this(0, 0.0, 0.0, 0L);
        }

        // Method to create a new record with updated values
        public BeaconPairStats addMeasurement(double rssiDiff, double scanRssiDiff, long timeDiff) {
            return new BeaconPairStats(
                    count + 1,
                    totalRssiDiff + rssiDiff,
                    totalScanRssiDiff + scanRssiDiff,
                    totalTimeDiff + timeDiff
            );
        }

        // Derived statistics methods
        public double getAvgRssiDiff() {
            return count > 0 ? totalRssiDiff / count : 0;
        }

        public double getAvgScanRssiDiff() {
            return count > 0 ? totalScanRssiDiff / count : 0;
        }

        public double getAvgTimeDiff() {
            return count > 0 ? (double)totalTimeDiff / count : 0;
        }
    }



    public List<List<PositionSanitized>> splitPositionsByTime(List<PositionSanitized> positions, int split ) {
        // Sort positions by device time first
        positions.sort(Comparator.comparing(PositionSanitized::getDeviceTime));

        List<List<PositionSanitized>> chunks = new ArrayList<>();


        //

//        split by number

//        if (positions.size() > 0) {
//            chunks.add(positions.subList(0, Math.min(split, positions.size())));
//        }
//
//        return chunks;

        //

        if (positions.isEmpty()) {
            return chunks;
        }

        List<PositionSanitized> currentChunk = new ArrayList<>();
        OffsetDateTime chunkStartTime = positions.get(0).getDeviceTime();

        for (PositionSanitized position : positions) {
            Duration timeDifference = Duration.between(chunkStartTime, position.getDeviceTime());

            if (timeDifference.toMinutes() >= split) {
                // Start a new chunk
                chunks.add(currentChunk);
                currentChunk = new ArrayList<>();
                chunkStartTime = position.getDeviceTime();
            }

            currentChunk.add(position);
        }

        // Add the last chunk if not empty
        if (!currentChunk.isEmpty()) {
            chunks.add(currentChunk);
        }

        return chunks;




//        if (positions.isEmpty() || split <= 0) {
//            return chunks;
//        }
//
//        for (int i = 0; i < positions.size(); i += split) {
//            chunks.add(new ArrayList<>(positions.subList(i, Math.min(i + split, positions.size()))));
//        }
//
//        return chunks;
    }



    double[] calculateMidpoint(double x1, double y1, double x2, double y2) {
        double midX = (x1 + x2) / 2;
        double midY = (y1 + y2) / 2;
        return new double[]{midX, midY};
    }



    public Map<Integer, Map<String, String>> findCowLocationdiff(List<PositionSanitized> allpositions, int minutes) {
        if (allpositions.isEmpty()) {
            return null;
        }

        List<List<PositionSanitized>>  min10split = splitPositionsByTime(allpositions, minutes);
        Map<Integer, Map<String, String>> debugLog = new HashMap<>();

        int count = 0;
        for (List<PositionSanitized> positions : min10split) {
            count = count+1;
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

            if (rssiDiffs.isEmpty() || scanRssiDiffs.isEmpty() || timeDiffs.isEmpty()) {
                continue;
            }
            double RSSI_DIFFERENCE_THRESHOLD= percentile(rssiDiffs, 0.8);
//            double RSSI_DIFFERENCE_THRESHOLD= average(rssiDiffs);
            double SCAN_RSSI_DIFFERENCE_THRESHOLD = percentile(scanRssiDiffs, .8);
            double TIME_DIFFERENCE_SECONDS =  percentileDouble(timeDiffs, 0.9);


            logger.info("Computed Percentiles -> RSSI_DIFF: {}, SCAN_RSSI_DIFF: {}, TIME_DIFF: {}",
                    RSSI_DIFFERENCE_THRESHOLD,
                    SCAN_RSSI_DIFFERENCE_THRESHOLD,
                    TIME_DIFFERENCE_SECONDS);
            positions.sort(Comparator.comparing(PositionSanitized::getDeviceTime));

            List<PositionSanitized> bestMatches = new ArrayList<>();


            Map<String, BeaconPairStats> beaconPairStats = new HashMap<>();

            Map<String, Integer> beaconFrequency = new HashMap<>();
            Map<String, Integer> beaconPairCount = new HashMap<>();

            for (int i = 0; i < positions.size(); i++) {
                for (int j = i+1; j < positions.size(); j++) {

                    PositionSanitized pos1 = positions.get(i);
                    PositionSanitized pos2 = positions.get(j);
                    if (i ==  j ){
                        continue;
                    }

                    double scanRssi1 = Double.parseDouble(pos1.getAttributes().get("scanRssi"));
                    double scanRssi2 = Double.parseDouble(pos2.getAttributes().get("scanRssi"));

                    double rssiDiff = Math.abs(pos1.getRssi() - pos2.getRssi());
                    double scanRssiDiff = Math.abs(scanRssi1 - scanRssi2);
                    long timeDiff = Math.abs(ChronoUnit.SECONDS.between(pos1.getDeviceTime(), pos2.getDeviceTime()));


                    if (rssiDiff <= RSSI_DIFFERENCE_THRESHOLD

//                            &&
//                            scanRssiDiff <= SCAN_RSSI_DIFFERENCE_THRESHOLD &&
//                            timeDiff <= TIME_DIFFERENCE_SECONDS

                    ) {

                        List<BeaconEntity> beacons = beaconRepository.findByNameIn(List.of(pos1.getBeaconName(), pos2.getBeaconName()));

                        if (beacons.size() == 2) {
                            BeaconEntity beacon1 = beacons.get(0);
                            BeaconEntity beacon2 = beacons.get(1);

                            double distance = calculateDistance(beacon1.getLatitude(), beacon1.getLongitude(),
                                    beacon2.getLatitude(), beacon2.getLongitude());
                            if (distance > 0.009){
                                continue;
                            }

                            System.out.println("The cow is in between beacons " + pos1.getBeaconName() + " and " + pos2.getBeaconName());
                            System.out.println("Distance: " + distance);
                            System.out.println("RSSI Difference: " + rssiDiff);

                            String beaconPair = pos1.getBeaconName().compareTo(pos2.getBeaconName()) < 0
                                    ? pos1.getBeaconName() + " - " + pos2.getBeaconName()
                                    : pos2.getBeaconName() + " - " + pos1.getBeaconName();

                            // Increment the count
                            beaconPairCount.put(beaconPair, beaconPairCount.getOrDefault(beaconPair, 0) + 1);



                            beaconPairStats.merge(beaconPair,
                                    new BeaconPairStats(),
                                    (existing, newStats) -> existing.addMeasurement(rssiDiff, scanRssiDiff, timeDiff));


                            // Count beacon occurrences to determine the most frequently appearing beacon
                            beaconFrequency.put(pos1.getBeaconName(), beaconFrequency.getOrDefault(pos1.getBeaconName(), 0) + 1);
                            beaconFrequency.put(pos2.getBeaconName(), beaconFrequency.getOrDefault(pos2.getBeaconName(), 0) + 1);

                                bestMatches.add(pos1);
                                bestMatches.add(pos2);
//                            }
                        }
                    }
                }
            }


            if (!bestMatches.isEmpty()) {
                PositionSanitized pos1 = bestMatches.get(0);
                PositionSanitized pos2 = bestMatches.get(1);

                double estimatedLatitude = (pos1.getLatitude() + pos2.getLatitude()) / 2;
                double estimatedLongitude = (pos1.getLongitude() + pos2.getLongitude()) / 2;

                System.out.println("Selected optimal pair: " + pos1.getBeaconName() + " and " + pos2.getBeaconName());
                Map<String, String> logDetails = new HashMap<>();

                String blah = beaconPairCount.entrySet().stream()
                        .max(Map.Entry.comparingByValue())
                        .get()
                        .getKey();
                String[] beaconPari = blah.split(" - ");;

                String target1 = beaconPari[0];
                String target2 = beaconPari[1];

                String closestBeacon = beaconFrequency.entrySet().stream()
                        .filter(entry -> entry.getKey().equals(target1) || entry.getKey().equals(target2)) // Filter by key
                        .max(Map.Entry.comparingByValue()) // Find max by value
                        .map(Map.Entry::getKey) // Extract key
                        .orElse(null); // Handle case where no match is found

                System.out.println("The cow is most likely closest to beacon: " + closestBeacon);

                String closestBeaconLog = "The cow is most likely closest to beacon: " + closestBeacon;
                logDetails.put("Closest Beacon", closestBeaconLog);
                logDetails.put("Approximate Location",
                        "Lat: " + estimatedLatitude + ", Lon: " + estimatedLongitude);


                logDetails.put("estimated time  ",  pos1.getDeviceTime().toString());
                debugLog.put(count, logDetails);

                logDetails.put("Optimal Pair", blah);

            }

        }

        return debugLog; // No valid position found
    }




    public Map<Integer, Map<String, String>> findCowLocationdiff3(List<PositionSanitized> allpositions, int minutes) {
        if (allpositions.isEmpty()) {
            return null;
        }

        // Group positions by time
        List<List<PositionSanitized>> min10split = splitPositionsByTime(allpositions, minutes);
        Map<Integer, Map<String, String>> debugLog = new HashMap<>();

        DBSCANLocalizationExample.clusterPositions(min10split.get(0));


        // Preload beacon data to avoid redundant queries
        Map<String, BeaconEntity> beaconMap = beaconRepository.findAll()
                .stream()
                .collect(Collectors.toMap(BeaconEntity::getName, Function.identity()));

        int count = 0;
        for (List<PositionSanitized> positions : min10split) {
        count++;

        List<Double> rssiDiffs = new ArrayList<>();
        List<Double> scanRssiDiffs = new ArrayList<>();
        List<Long> timeDiffs = new ArrayList<>();

        // Compute pairwise differences in parallel
        IntStream.range(0, positions.size()).parallel().forEach(i -> {
            for (int j = i + 1; j < positions.size(); j++) {
                PositionSanitized pos1 = positions.get(i);
                PositionSanitized pos2 = positions.get(j);

                double rssiDiff = Math.abs(pos1.getRssi() - pos2.getRssi());
                double scanRssiDiff = Math.abs(
                        Double.parseDouble(pos1.getAttributes().get("scanRssi")) -
                                Double.parseDouble(pos2.getAttributes().get("scanRssi"))
                );
                long timeDiff = Math.abs(ChronoUnit.SECONDS.between(pos1.getDeviceTime(), pos2.getDeviceTime()));

                synchronized (rssiDiffs) {
                    rssiDiffs.add(rssiDiff);
                    scanRssiDiffs.add(scanRssiDiff);
                    timeDiffs.add(timeDiff);
                }
            }
        });

        if (rssiDiffs.isEmpty() || scanRssiDiffs.isEmpty() || timeDiffs.isEmpty()) {
            continue;
        }

        // Compute percentiles once
        double RSSI_DIFFERENCE_THRESHOLD = percentile(rssiDiffs, 0.8);
        double SCAN_RSSI_DIFFERENCE_THRESHOLD = percentile(scanRssiDiffs, 0.8);
        double TIME_DIFFERENCE_SECONDS = percentileDouble(timeDiffs, 0.9);

        positions.sort(Comparator.comparing(PositionSanitized::getDeviceTime));

        Map<String, Integer> beaconFrequency = new HashMap<>();
        Map<String, Integer> beaconPairCount = new HashMap<>();

        List<PositionSanitized> bestMatches = new ArrayList<>();

        // Optimize pairwise comparisons by sorting and avoiding duplicates
        for (int i = 0; i < positions.size(); i++) {
            for (int j = i+1; j < positions.size(); j++) {


                if (i == j ){
                    continue;
                }
                PositionSanitized pos1 = positions.get(i);
                PositionSanitized pos2 = positions.get(j);

                double scanRssi1 = Double.parseDouble(pos1.getAttributes().get("scanRssi"));
                double scanRssi2 = Double.parseDouble(pos2.getAttributes().get("scanRssi"));

                double rssiDiff = Math.abs(pos1.getRssi() - pos2.getRssi());
                double scanRssiDiff = Math.abs(scanRssi1 - scanRssi2);
                long timeDiff = Math.abs(ChronoUnit.SECONDS.between(pos1.getDeviceTime(), pos2.getDeviceTime()));

                if (rssiDiff > RSSI_DIFFERENCE_THRESHOLD

                            ||
                            scanRssiDiff > SCAN_RSSI_DIFFERENCE_THRESHOLD
                    //                        ||
    //                        timeDiff > TIME_DIFFERENCE_SECONDS

                ) {
                    continue;
                }

                BeaconEntity beacon1 = beaconMap.get(pos1.getBeaconName());
                BeaconEntity beacon2 = beaconMap.get(pos2.getBeaconName());

                if (beacon1 == null || beacon2 == null) {
                    continue;
                }

                double distance = calculateDistance(beacon1.getLatitude(), beacon1.getLongitude(),
                        beacon2.getLatitude(), beacon2.getLongitude());

                if (distance > 0.008) {
                    continue;
                }
//                if (pos1.getBeaconName().equals(pos2.getBeaconName())){
//                    continue;
//                }

                String beaconPair = pos1.getBeaconName().compareTo(pos2.getBeaconName()) < 0
                        ? pos1.getBeaconName() + " - " + pos2.getBeaconName()
                        : pos2.getBeaconName() + " - " + pos1.getBeaconName();

                beaconPairCount.merge(beaconPair, 1, Integer::sum);

                beaconFrequency.merge(pos1.getBeaconName(), 1, Integer::sum);
                beaconFrequency.merge(pos2.getBeaconName(), 1, Integer::sum);

                bestMatches.add(pos1);
                bestMatches.add(pos2);
            }
        }

        if (!bestMatches.isEmpty()) {

            PositionSanitized pos1 = bestMatches.get(0);
            PositionSanitized pos2 = bestMatches.get(1);

            double estimatedLatitude = (pos1.getLatitude() + pos2.getLatitude()) / 2;
            double estimatedLongitude = (pos1.getLongitude() + pos2.getLongitude()) / 2;

            // Find the most frequently occurring beacon pair
            String mostFrequentPair = beaconPairCount.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse(null);

            if (mostFrequentPair == null) {
                continue;
            }

            String[] beaconPairArray = mostFrequentPair.split(" - ");
            String target1 = beaconPairArray[0];
            String target2 = beaconPairArray[1];

            // Weighted voting for closest beacon selection
            String closestBeacon = beaconFrequency.entrySet().stream()
                    .filter(entry -> entry.getKey().equals(target1) || entry.getKey().equals(target2))
                    .max(Comparator.comparingDouble(entry -> entry.getValue() /
                            (1 + Math.abs(RSSI_DIFFERENCE_THRESHOLD))))
                    .map(Map.Entry::getKey)
                    .orElse(null);

            Map<String, String> logDetails = new HashMap<>();
            logDetails.put("Optimal Pair", mostFrequentPair);
            logDetails.put("Closest Beacon", closestBeacon);

            debugLog.put(count, logDetails);
        }
    }

        return debugLog;
    }



    @GetMapping("/compute/all/test/csv/v2")
    public ResponseEntity<Map<Integer, Map<String, String>>> test(@RequestParam(defaultValue = "5") int minutes, @RequestParam(defaultValue = "1") int entryNumber) {

        logger.info("working with entry  {} ", entryNumber);


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
            return null;
        }



        if (positionsSubset.isEmpty()) {
            logger.warn("No positions found in the JSON file.");
            return null;
        }

        // Determine the latest timestamp for each deviceId
        for (PositionSanitized pos : positionsSubset) {
            Long deviceId = pos.getDeviceId();
            OffsetDateTime currentTime = pos.getDeviceTime(); // let's pass it
            latestTimesMap.merge(deviceId, currentTime, (existingTime, newTime) -> existingTime.isAfter(newTime) ? existingTime : newTime);
        }

        if (latestTimesMap.isEmpty()) {
            logger.warn("No recent timestamps found for any device.");
            return null;
        }

        List<Map<String, Object>> devicesResults = new ArrayList<>();
        Map<Long, Map<String, Object>> uniqueBeaconsMap = new HashMap<>();

        for (Map.Entry<Long, OffsetDateTime> entry : latestTimesMap.entrySet()) {
            Long deviceId = entry.getKey();

//            int entryNumber = 1; // Change this to 1, 6, or 8 for testing

            OffsetDateTime windowStart;
            int x;

            switch (entryNumber) {
                case 1:
                    windowStart = OffsetDateTime.of(2022, 4, 26, 13, 56, 25, 0, ZoneOffset.UTC);
                    x = 15;  // 75 minutes
                    System.out.println("Observed Location: A5, X (minutes): " + x);  //correct W10T_A7H_B-1-2 Tränke
                    break;

                case 6:
                    windowStart = OffsetDateTime.of(2022, 4, 26, 13, 6, 59, 0, ZoneOffset.UTC);
                    x = 3;  // 3 minutes
                    System.out.println("Observed Location: T1, X (minutes): " + x);// correct W10T_A7H_B-1-19 Tränke
                    break;

                case 8:
                    windowStart = OffsetDateTime.of(2022, 4, 26, 12, 23, 21, 0, ZoneOffset.UTC);
                    x = 20;  // 43 minutes
                    System.out.println("Observed Location: C4, X (minutes): " + x);
                    break;

                default:
                    windowStart = OffsetDateTime.of(2022, 4, 26, 13, 56, 25, 0, ZoneOffset.UTC);
                    x = (1 * 60) + 15;  // 75 minutes
                    System.out.println("Default : A5, X (minutes): " + x);//                    return;
            }

            // Kalman Filters for BLE Positioning
            KalmanFilter latitudeFilter = new KalmanFilter(0.01, 0.5, 1);
            KalmanFilter longitudeFilter = new KalmanFilter(0.01, 0.5, 1);
            KalmanFilter rssiFilter = new KalmanFilter(0.005, 12, 1);


            // Apply Kalman Filter to the positions
            List<PositionSanitized> recentDetections;


            boolean kalmanfiler = false;
            if (kalmanfiler) {
                logger.info("Kalman Filter applied to all positions.");

                logger.info("Applying Kalman Filter to all positions and filtering data.");

                recentDetections = positionsSubset.stream()
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
            } else {

                recentDetections = positionsSubset.stream()
                        .filter(pos -> pos.getDeviceId().equals(deviceId) &&
                                (pos.getDeviceTime().isAfter(windowStart) || pos.getDeviceTime().isEqual(windowStart)) &&
                                (pos.getDeviceTime().isBefore(windowStart.plusMinutes(x)) || pos.getDeviceTime().isEqual(windowStart.plusMinutes(x))) &&  // Adding X minutes to windowStart
                                (128 - pos.getRssi() > -75
                                        && Double.parseDouble(pos.getAttributes().get("scanRssi")) > -90
                                )  // Filtering out weak signals
                        )
                        .sorted(Comparator.comparing(PositionSanitized::getDeviceTime, Comparator.reverseOrder())  // Sort by deviceTime descending
                                .thenComparing(pos -> Math.abs(pos.getRssi()), Comparator.reverseOrder())  // Sort by RSSI descending
                        )
                        .collect(Collectors.toList());
            }


            logger.info("Kalman Filter applied to {} recent detections.", recentDetections.size());

            if (recentDetections.isEmpty()) {
                continue;
            }
//            var k = findCowLocationdiff(recentDetections, minutes);
//            var k = findCowLocationdiff3(recentDetections, minutes);
            var k = findCowLocationdiff34(recentDetections, minutes);


            return ResponseEntity.ok(k);
//        return (ResponseEntity<Map<Integer, Map<String, String>>>) k;

        }

        return  null ;

    }


    /**
     * Convert RSSI to approximate distance (in meters) via a simple path-loss model.
     *
     * @param rssi            measured RSSI (dBm)
     * @param rssiAtOneMeter  known RSSI at 1m distance (e.g., -59dBm for BLE)
     * @param pathLossExponent typical range 2.0 - 3.5
     */
    private double rssiToDistance(double rssi, double rssiAtOneMeter, double pathLossExponent) {
        // distance = 10 ^ ((RSSI_1m - RSSI_measured) / (10 * n))
        return Math.pow(10, (rssiAtOneMeter - rssi) / (10.0 * pathLossExponent));
    }



    /**
     * Trilaterate using a simple gradient descent approach.
     *
     * @param beaconPositions list of double[]{lat, lon} (or x,y if using local coords)
     * @param distances       corresponding distances from each beacon
     * @return double[]{lat, lon} best guess of the receiver location
     */
    private double[] trilaterateLeastSquares(List<double[]> beaconPositions, List<Double> distances) {
        // 1) Initialize guess as average of beacon positions
        double x = 0.0, y = 0.0;
        for (double[] bpos : beaconPositions) {
            x += bpos[0];
            y += bpos[1];
        }
        x /= beaconPositions.size();
        y /= beaconPositions.size();

        // Hyperparameters for gradient descent
        double learningRate = 0.0005;
        int maxIterations = 300;

        for (int iter = 0; iter < maxIterations; iter++) {
            double gradX = 0.0;
            double gradY = 0.0;

            for (int i = 0; i < beaconPositions.size(); i++) {
                double[] bpos = beaconPositions.get(i);
                double dist = distances.get(i);

                double dx = x - bpos[0];
                double dy = y - bpos[1];
                double range = Math.sqrt(dx * dx + dy * dy);

                if (range < 1e-8) {
                    continue; // avoid division by zero if guess ~ same as beacon
                }

                double diff = (range - dist);
                // derivative for sum of (range - dist)^2
                gradX += diff * (dx / range);
                gradY += diff * (dy / range);
            }

            x -= learningRate * gradX;
            y -= learningRate * gradY;
        }

        return new double[]{ x, y };
    }





    public Map<Integer, Map<String, String>> findCowLocationdiff34(List<PositionSanitized> allpositions, int minutes) {
        if (allpositions.isEmpty()) {
            return null;
        }

        // Group positions by time
        List<List<PositionSanitized>> min10split = splitPositionsByTime(allpositions, minutes);
        Map<Integer, Map<String, String>> debugLog = new HashMap<>();

        // Example call (you have DBSCAN code already)
        if (!min10split.isEmpty()) {
            DBSCANLocalizationExample.clusterPositions(min10split.get(0));
        }

        // Preload beacon data
        Map<String, BeaconEntity> beaconMap = beaconRepository.findAll()
                .stream()
                .collect(Collectors.toMap(BeaconEntity::getName, Function.identity()));

        int count = 0;
        for (List<PositionSanitized> positions : min10split) {
            count++;

            // 1) Gather stats to compute thresholds
            List<Double> rssiDiffs = new ArrayList<>();
            List<Double> scanRssiDiffs = new ArrayList<>();
            List<Long> timeDiffs = new ArrayList<>();

            // Pairwise differences in parallel
            IntStream.range(0, positions.size()).parallel().forEach(i -> {
                for (int j = i + 1; j < positions.size(); j++) {
                    PositionSanitized pos1 = positions.get(i);
                    PositionSanitized pos2 = positions.get(j);

                    double rssiDiff = Math.abs(pos1.getRssi() - pos2.getRssi());
                    double scanRssiDiff = Math.abs(
                            Double.parseDouble(pos1.getAttributes().get("scanRssi")) -
                                    Double.parseDouble(pos2.getAttributes().get("scanRssi"))
                    );
                    long timeDiff = Math.abs(ChronoUnit.SECONDS.between(pos1.getDeviceTime(), pos2.getDeviceTime()));

                    synchronized (rssiDiffs) {
                        rssiDiffs.add(rssiDiff);
                        scanRssiDiffs.add(scanRssiDiff);
                        timeDiffs.add(timeDiff);
                    }
                }
            });

            if (rssiDiffs.isEmpty() || scanRssiDiffs.isEmpty() || timeDiffs.isEmpty()) {
                continue;
            }

            double RSSI_DIFFERENCE_THRESHOLD = percentile(rssiDiffs, 0.8);
            double SCAN_RSSI_DIFFERENCE_THRESHOLD = percentile(scanRssiDiffs, 0.8);
            double TIME_DIFFERENCE_SECONDS = percentileDouble(timeDiffs, 0.9);

            positions.sort(Comparator.comparing(PositionSanitized::getDeviceTime));

            Map<String, Integer> beaconFrequency = new HashMap<>();
            Map<String, Integer> beaconPairCount = new HashMap<>();

            List<PositionSanitized> bestMatches = new ArrayList<>();

            // 2) Build bestMatches (pairs that pass thresholds)
            for (int i = 0; i < positions.size(); i++) {
                for (int j = i + 1; j < positions.size(); j++) {
                    if (i == j) continue;

                    PositionSanitized pos1 = positions.get(i);
                    PositionSanitized pos2 = positions.get(j);

                    double scanRssi1 = Double.parseDouble(pos1.getAttributes().get("scanRssi"));
                    double scanRssi2 = Double.parseDouble(pos2.getAttributes().get("scanRssi"));
                    double rssiDiff = Math.abs(pos1.getRssi() - pos2.getRssi());
                    double scanRssiDiff = Math.abs(scanRssi1 - scanRssi2);
                    long timeDiff = Math.abs(ChronoUnit.SECONDS.between(pos1.getDeviceTime(), pos2.getDeviceTime()));

                    // Filter by thresholds
                    if (rssiDiff > RSSI_DIFFERENCE_THRESHOLD) {
                        // Optionally also consider scanRssiDiff, timeDiff, etc.
                        continue;
                    }

                    BeaconEntity beacon1 = beaconMap.get(pos1.getBeaconName());
                    BeaconEntity beacon2 = beaconMap.get(pos2.getBeaconName());
                    if (beacon1 == null || beacon2 == null) {
                        continue;
                    }

                    double distance = calculateDistance(
                            beacon1.getLatitude(), beacon1.getLongitude(),
                            beacon2.getLatitude(), beacon2.getLongitude()
                    );

                    // Only consider pairs < ~0.008 degrees (~<1km if each degree ~111km)
                    if (distance > 0.008) {
                        continue;
                    }

                    // Record pair usage
                    String beaconPair = (pos1.getBeaconName().compareTo(pos2.getBeaconName()) < 0)
                            ? (pos1.getBeaconName() + " - " + pos2.getBeaconName())
                            : (pos2.getBeaconName() + " - " + pos1.getBeaconName());

                    beaconPairCount.merge(beaconPair, 1, Integer::sum);
                    beaconFrequency.merge(pos1.getBeaconName(), 1, Integer::sum);
                    beaconFrequency.merge(pos2.getBeaconName(), 1, Integer::sum);

                    bestMatches.add(pos1);
                    bestMatches.add(pos2);
                }
            }

            if (bestMatches.isEmpty()) {
                continue;
            }

            // We'll store final logs here
            Map<String, String> logDetails = new HashMap<>();

            // == A) Identify the "Optimal Pair" (mostFrequentPair) ==
            String mostFrequentPair = beaconPairCount.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse(null);

            // We'll also find the "closest beacon" among that pair
            String closestBeacon = null;
            if (mostFrequentPair != null) {
                logDetails.put("OptimalPair", mostFrequentPair);

                String[] beaconPairArray = mostFrequentPair.split(" - ");
                String target1 = beaconPairArray[0];
                String target2 = beaconPairArray[1];

                // Weighted voting for "closest beacon" selection
                closestBeacon = beaconFrequency.entrySet().stream()
                        .filter(e -> e.getKey().equals(target1) || e.getKey().equals(target2))
                        .max(Comparator.comparingDouble(e -> e.getValue() / (1 + Math.abs(RSSI_DIFFERENCE_THRESHOLD))))
                        .map(Map.Entry::getKey)
                        .orElse(null);

                logDetails.put("ClosestBeacon", closestBeacon);
            }

            // == B) Build a 2-beacon midpoint from bestMatches[0], bestMatches[1] as fallback
            //    or better yet, from the actual positions associated with the "mostFrequentPair"?
            // Let's use bestMatches[0] & [1] for simplicity
            PositionSanitized p1 = bestMatches.get(0);
            PositionSanitized p2 = bestMatches.get(1);
            double midpointLat = (p1.getLatitude() + p2.getLatitude()) / 2.0;
            double midpointLon = (p1.getLongitude() + p2.getLongitude()) / 2.0;

            // If we want the midpoint specifically of the "mostFrequentPair," we could find
            // those exact two PositionSanitized objects. For brevity, we'll keep it simple.

            // == C) Attempt Triangulation with 3+ unique beacons
            //    We'll gather them in a map: {beaconName -> averaged RSSI}
            Map<String, Double> beaconRssiMap = new HashMap<>();
            for (PositionSanitized pp : bestMatches) {
                // Combine/average if same beacon shows multiple times
                beaconRssiMap.merge(
                        pp.getBeaconName(),
                        Double.valueOf(pp.getRssi()),
                        (oldVal, newVal) -> (oldVal + newVal) / 2.0
                );
            }

            double triLat = Double.NaN;
            double triLon = Double.NaN;
            boolean usedTriangulation = false;

            if (beaconRssiMap.size() >= 3) {
                // We have enough beacons for triangulation
                List<double[]> beaconPositions = new ArrayList<>();
                List<Double> distances = new ArrayList<>();

                for (Map.Entry<String, Double> entry : beaconRssiMap.entrySet()) {
                    String bName = entry.getKey();
                    double avgRssi = entry.getValue();

                    BeaconEntity bEntity = beaconMap.get(bName);
                    if (bEntity == null) continue;

                    double lat = bEntity.getLatitude();
                    double lon = bEntity.getLongitude();
                    beaconPositions.add(new double[]{ lat, lon });

                    // Convert RSSI -> distance (calibration needed)
                    double dist = rssiToDistance(avgRssi, -59, 2.0);
                    distances.add(dist);
                }

                if (beaconPositions.size() >= 3) {
                    double[] trilatResult = trilaterateLeastSquares(beaconPositions, distances);
                    triLat = trilatResult[0];
                    triLon = trilatResult[1];

                    logDetails.put("TrilateratedLat", String.valueOf(triLat));
                    logDetails.put("TrilateratedLon", String.valueOf(triLon));
                    logDetails.put("Method", "Trilateration with " + beaconPositions.size() + " beacons");

                    usedTriangulation = true;
                } else {
                    logDetails.put("Method", "Not enough final beacons for triangulation");
                }
            } else {
                logDetails.put("Method", "Fallback to 2-Beacon Midpoint (not enough beacons for triangulation)");
            }

            // == D) Compute final Weighted Positions ==

            // Step D1: If we have a "closest beacon" among the pair, shift the midpoint
            // We'll do a small weighting that pulls midpoint 70% M, 30% toward that beacon
            double revisedMidLat = midpointLat;
            double revisedMidLon = midpointLon;

            if (closestBeacon != null) {
                BeaconEntity cBeacon = beaconMap.get(closestBeacon);
                if (cBeacon != null) {
                    double cLat = cBeacon.getLatitude();
                    double cLon = cBeacon.getLongitude();

                    double alpha = 0.5;  // how much we trust the midpoint vs. beacon
                    revisedMidLat = alpha * midpointLat + (1 - alpha) * cLat;
                    revisedMidLon = alpha * midpointLon + (1 - alpha) * cLon;

                    logDetails.put("MidpointShiftedToClosestBeacon",
                            String.format("Alpha=%.2f, cBeacon=%s", alpha, closestBeacon));
                }
            }

            // Step D2: If we did Triangulation (T) as well, we can blend T & revisedMid (M')
            // Weighted final approach: finalLat = beta*T + (1-beta)*M'
            double finalLat;
            double finalLon;

            if (usedTriangulation && !Double.isNaN(triLat)) {
                double beta = 0.1; // example: 70% trust in triangulation
                finalLat = beta * triLat + (1 - beta) * revisedMidLat;
                finalLon = beta * triLon + (1 - beta) * revisedMidLon;
                logDetails.put("WeightedBlend", String.format("Beta=%.2f -> 60%% Tri, 40%% Mid", beta));
            } else {
                // No tri location -> just use the revised midpoint
                finalLat = revisedMidLat;
                finalLon = revisedMidLon;
            }

            // Log final lat/lon
            logDetails.put("FinalLat", String.valueOf(finalLat));
            logDetails.put("FinalLon", String.valueOf(finalLon));

            debugLog.put(count, logDetails);
        }

        return debugLog;
    }
























    private List<PositionSanitized> getTestPositions( int entryNumber) {


        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Register Java 8 Date/Time module
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);  // Ignore unknown fields

        // Read positions from JSON file instead of database
        String jsonFilePath = "C:\\Users\\hikev\\Desktop\\thesis\\2024-MA-Kevin-George\\services\\LocationSimulationService\\data.json";
        List<PositionSanitized> positionsSubset = new ArrayList<>();

        try {
            positionsSubset = objectMapper.readValue(
                    new File(jsonFilePath),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, PositionSanitized.class)
            );

            logger.info("Successfully read {} positions from JSON file.", positionsSubset.size());
        } catch (IOException e) {
            logger.error("Error reading JSON file: {}", e.getMessage());
            return null;
        }



        if (positionsSubset.isEmpty()) {
            logger.warn("No positions found in the JSON file.");
            return null;
        }

        List<PositionSanitized> positions = new ArrayList<>();
        OffsetDateTime windowStart;
        int x;
        // Example: Creating dummy positions for demonstration.
        switch (entryNumber) {
            case 1:
                windowStart = OffsetDateTime.of(2022, 4, 26, 13, 56, 25, 0, ZoneOffset.UTC);
                x = 35;  // 75 minutes
                System.out.println("Observed Location: A5, X (minutes): " + x);  //correct W10T_A7H_B-1-2 Tränke
                break;

            case 6:
                windowStart = OffsetDateTime.of(2022, 4, 26, 13, 6, 59, 0, ZoneOffset.UTC);
                x = 3;  // 3 minutes
                System.out.println("Observed Location: T1, X (minutes): " + x);// correct W10T_A7H_B-1-19 Tränke
                break;

            case 8:
                windowStart = OffsetDateTime.of(2022, 4, 26, 12, 23, 21, 0, ZoneOffset.UTC);
                x = 20;  // 43 minutes
                System.out.println("Observed Location: C4, X (minutes): " + x);
                break;

            default:
                windowStart = OffsetDateTime.of(2022, 4, 26, 13, 56, 25, 0, ZoneOffset.UTC);
                x = (1 * 60) + 15;  // 75 minutes
                System.out.println("Default : A5, X (minutes): " + x);//                    return;
        }

        // Kalman Filters for BLE Positioning
        KalmanFilter latitudeFilter = new KalmanFilter(0.01, 0.5, 1);
        KalmanFilter longitudeFilter = new KalmanFilter(0.01, 0.5, 1);
        KalmanFilter rssiFilter = new KalmanFilter(0.005, 12, 1);


        // Apply Kalman Filter to the positions
        List<PositionSanitized> recentDetections;


        boolean kalmanfiler = false;
        if (kalmanfiler) {
            logger.info("Kalman Filter applied to all positions.");

            logger.info("Applying Kalman Filter to all positions and filtering data.");

            recentDetections = positionsSubset.stream()
                    .filter(pos -> true &&
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
        } else {

            recentDetections = positionsSubset.stream()
                    .filter(pos ->
                            (pos.getDeviceTime().isAfter(windowStart) || pos.getDeviceTime().isEqual(windowStart)) &&
                            (pos.getDeviceTime().isBefore(windowStart.plusMinutes(x)) || pos.getDeviceTime().isEqual(windowStart.plusMinutes(x))) &&  // Adding X minutes to windowStart
                            (128 - pos.getRssi() > -75
                                    && Double.parseDouble(pos.getAttributes().get("scanRssi")) > -90
                            )  // Filtering out weak signals
                    )
                    .sorted(Comparator.comparing(PositionSanitized::getDeviceTime, Comparator.reverseOrder())  // Sort by deviceTime descending
                            .thenComparing(pos -> Math.abs(pos.getRssi()), Comparator.reverseOrder())  // Sort by RSSI descending
                    )
                    .collect(Collectors.toList());
        }

        // You can add more positions as needed.
        return recentDetections;
    }






    /// api test


    public Map<Integer, Map<String, String>> findCowLocationdiff34(int minutes, double alpha, double beta, int cow) {

        List<PositionSanitized> allpositions = getTestPositions(cow );

        if (allpositions.isEmpty()) {
            return null;
        }

        // Group positions by time
        List<List<PositionSanitized>> min10split = splitPositionsByTime(allpositions, minutes);
        Map<Integer, Map<String, String>> debugLog = new HashMap<>();

        // Example call (you have DBSCAN code already)
        if (!min10split.isEmpty()) {
            DBSCANLocalizationExample.clusterPositions(min10split.get(0));
        }

        // Preload beacon data
        Map<String, BeaconEntity> beaconMap = beaconRepository.findAll()
                .stream()
                .collect(Collectors.toMap(BeaconEntity::getName, Function.identity()));

        int count = 0;
        for (List<PositionSanitized> positions : min10split) {
            count++;

            // 1) Gather stats to compute thresholds
            List<Double> rssiDiffs = new ArrayList<>();
            List<Double> scanRssiDiffs = new ArrayList<>();
            List<Long> timeDiffs = new ArrayList<>();

            // Pairwise differences in parallel
            IntStream.range(0, positions.size()).parallel().forEach(i -> {
                for (int j = i + 1; j < positions.size(); j++) {
                    PositionSanitized pos1 = positions.get(i);
                    PositionSanitized pos2 = positions.get(j);

                    double rssiDiff = Math.abs(pos1.getRssi() - pos2.getRssi());
                    double scanRssiDiff = Math.abs(
                            Double.parseDouble(pos1.getAttributes().get("scanRssi")) -
                                    Double.parseDouble(pos2.getAttributes().get("scanRssi"))
                    );
                    long timeDiff = Math.abs(ChronoUnit.SECONDS.between(pos1.getDeviceTime(), pos2.getDeviceTime()));

                    synchronized (rssiDiffs) {
                        rssiDiffs.add(rssiDiff);
                        scanRssiDiffs.add(scanRssiDiff);
                        timeDiffs.add(timeDiff);
                    }
                }
            });

            if (rssiDiffs.isEmpty() || scanRssiDiffs.isEmpty() || timeDiffs.isEmpty()) {
                continue;
            }

            double RSSI_DIFFERENCE_THRESHOLD = percentile(rssiDiffs, 0.8);
            double SCAN_RSSI_DIFFERENCE_THRESHOLD = percentile(scanRssiDiffs, 0.8);
            double TIME_DIFFERENCE_SECONDS = percentileDouble(timeDiffs, 0.9);

            positions.sort(Comparator.comparing(PositionSanitized::getDeviceTime));


            Map<String, Integer> beaconFrequency = new HashMap<>();
            Map<String, Integer> beaconPairCount = new HashMap<>();

            List<PositionSanitized> bestMatches = new ArrayList<>();

            // 2) Build bestMatches (pairs that pass thresholds)
            for (int i = 0; i < positions.size(); i++) {
                for (int j = i + 1; j < positions.size(); j++) {
                    // Avoid duplicate pairs
                    if (i == j ){
                        continue;
                    }
                    PositionSanitized pos1 = positions.get(i);
                    PositionSanitized pos2 = positions.get(j);

                    double scanRssi1 = Double.parseDouble(pos1.getAttributes().get("scanRssi"));
                    double scanRssi2 = Double.parseDouble(pos2.getAttributes().get("scanRssi"));
                    double rssiDiff = Math.abs(pos1.getRssi() - pos2.getRssi());
                    double scanRssiDiff = Math.abs(scanRssi1 - scanRssi2);
                    long timeDiff = Math.abs(ChronoUnit.SECONDS.between(pos1.getDeviceTime(), pos2.getDeviceTime()));

                    // Filter by thresholds
                    if (rssiDiff > RSSI_DIFFERENCE_THRESHOLD) {
                        continue;
                    }

                    BeaconEntity beacon1 = beaconMap.get(pos1.getBeaconName());
                    BeaconEntity beacon2 = beaconMap.get(pos2.getBeaconName());
                    if (beacon1 == null || beacon2 == null) {
                        continue;
                    }

                    double distance = calculateDistance(
                            beacon1.getLatitude(), beacon1.getLongitude(),
                            beacon2.getLatitude(), beacon2.getLongitude()
                    );

                    // Only consider pairs < ~0.008 degrees (~<1km if each degree ~111km)
                    if (distance > 0.008) {
                        continue;
                    }

                    // Record pair usage
                    String beaconPair = (pos1.getBeaconName().compareTo(pos2.getBeaconName()) < 0)
                            ? (pos1.getBeaconName() + " - " + pos2.getBeaconName())
                            : (pos2.getBeaconName() + " - " + pos1.getBeaconName());

                    beaconPairCount.merge(beaconPair, 1, Integer::sum);
                    beaconFrequency.merge(pos1.getBeaconName(), 1, Integer::sum);
                    beaconFrequency.merge(pos2.getBeaconName(), 1, Integer::sum);

                    bestMatches.add(pos1);
                    bestMatches.add(pos2);
                }
            }

            if (bestMatches.isEmpty()) {
                continue;
            }

            // We'll store final logs here
            Map<String, String> logDetails = new HashMap<>();

            // == A) Identify the "Optimal Pair" (mostFrequentPair) ==
            String mostFrequentPair = beaconPairCount.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse(null);

            // We'll also find the "closest beacon" among that pair
            String closestBeacon = null;
            if (mostFrequentPair != null) {
                logDetails.put("OptimalPair", mostFrequentPair);

                String[] beaconPairArray = mostFrequentPair.split(" - ");
                String target1 = beaconPairArray[0];
                String target2 = beaconPairArray[1];

                // Weighted voting for "closest beacon" selection
                closestBeacon = beaconFrequency.entrySet().stream()
                        .filter(e -> e.getKey().equals(target1) || e.getKey().equals(target2))
                        .max(Comparator.comparingDouble(e -> e.getValue() / (1 + Math.abs(RSSI_DIFFERENCE_THRESHOLD))))
                        .map(Map.Entry::getKey)
                        .orElse(null);

                logDetails.put("ClosestBeacon", closestBeacon);
            }

            // == B) Build a 2-beacon midpoint from bestMatches[0], bestMatches[1] as fallback ==
            PositionSanitized p1 = bestMatches.get(0);
            PositionSanitized p2 = bestMatches.get(1);
            double midpointLat = (p1.getLatitude() + p2.getLatitude()) / 2.0;
            double midpointLon = (p1.getLongitude() + p2.getLongitude()) / 2.0;

            // == C) Attempt Triangulation with 3+ unique beacons ==
            Map<String, Double> beaconRssiMap = new HashMap<>();
            for (PositionSanitized pp : bestMatches) {
                beaconRssiMap.merge(
                        pp.getBeaconName(),
                        Double.valueOf(pp.getRssi()),
                        (oldVal, newVal) -> (oldVal + newVal) / 2.0
                );
            }


            double triLat = Double.NaN;
            double triLon = Double.NaN;
            boolean usedTriangulation = false;

            if (beaconRssiMap.size() >= 3) {
                List<double[]> beaconPositions = new ArrayList<>();
                List<Double> distances = new ArrayList<>();

                for (Map.Entry<String, Double> entry : beaconRssiMap.entrySet()) {
                    String bName = entry.getKey();
                    double avgRssi = entry.getValue();

                    BeaconEntity bEntity = beaconMap.get(bName);
                    if (bEntity == null) continue;

                    double lat = bEntity.getLatitude();
                    double lon = bEntity.getLongitude();
                    beaconPositions.add(new double[]{ lat, lon });

                    // Convert RSSI -> distance (calibration needed)
                    double dist = rssiToDistance(avgRssi, -59, 2.0);
                    distances.add(dist);
                }

                if (beaconPositions.size() >= 3) {
                    double[] trilatResult = trilaterateLeastSquares(beaconPositions, distances);
                    triLat = trilatResult[0];
                    triLon = trilatResult[1];

                    logDetails.put("TrilateratedLat", String.valueOf(triLat));
                    logDetails.put("TrilateratedLon", String.valueOf(triLon));
                    logDetails.put("Method", "Trilateration with " + beaconPositions.size() + " beacons");

                    usedTriangulation = true;
                } else {
                    logDetails.put("Method", "Not enough final beacons for triangulation");
                }
            } else {
                logDetails.put("Method", "Fallback to 2-Beacon Midpoint (not enough beacons for triangulation)");
            }

            // == D) Compute final Weighted Positions ==
            // D1: Shift the midpoint toward the "closest beacon" if available.
            double revisedMidLat = midpointLat;
            double revisedMidLon = midpointLon;

            if (closestBeacon != null) {
                BeaconEntity cBeacon = beaconMap.get(closestBeacon);
                if (cBeacon != null) {
                    double cLat = cBeacon.getLatitude();
                    double cLon = cBeacon.getLongitude();

                    // Use the passed alpha value (e.g., 0.5 means 50% midpoint, 50% beacon)
                    revisedMidLat = alpha * midpointLat + (1 - alpha) * cLat;
                    revisedMidLon = alpha * midpointLon + (1 - alpha) * cLon;

                    logDetails.put("MidpointShiftedToClosestBeacon",
                            String.format("Alpha=%.2f, cBeacon=%s", alpha, closestBeacon));
                }
            }

            // D2: Blend triangulation (if available) with the revised midpoint using beta.
            double finalLat;
            double finalLon;

            if (usedTriangulation && !Double.isNaN(triLat)) {
                finalLat = beta * triLat + (1 - beta) * revisedMidLat;
                finalLon = beta * triLon + (1 - beta) * revisedMidLon;
                logDetails.put("WeightedBlend", String.format("Beta=%.2f applied", beta));
            } else {
                finalLat = revisedMidLat;
                finalLon = revisedMidLon;
            }

            // Log final lat/lon
            logDetails.put("FinalLat", String.valueOf(finalLat));
            logDetails.put("FinalLon", String.valueOf(finalLon));

            debugLog.put(count, logDetails);


        }

        return debugLog;
    }




    @GetMapping("/apiTest")
    public ResponseEntity<Map<Integer, Map<String, String>>> apiTest(
            @RequestParam(name = "minutes", defaultValue = "10") int minutes,
            @RequestParam(name = "alpha", defaultValue = "0.5") double alpha,
            @RequestParam(name = "beta", defaultValue = "0.1") double beta,
            @RequestParam(name = "entry", defaultValue = "0") int entry) {

        Map<Integer, Map<String, String>> debugLog =
                findCowLocationdiff34(minutes, alpha, beta, entry);


        System.out.println(minutes);
        System.out.println(beta);
        System.out.println(entry );
        return ResponseEntity.ok(debugLog);
    }
}
