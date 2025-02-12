package com.cattleDB.LocationSimulationService.controller;

import com.cattleDB.LocationSimulationService.models.BeaconEntity;
import com.cattleDB.LocationSimulationService.models.PositionSanitized;
import com.cattleDB.LocationSimulationService.repository.BeaconRepository;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@RestController
@RequestMapping("/api/proximity/test")
public class TestLocation {

    private static final Logger logger = LoggerFactory.getLogger(TrilaterationController.class);


    @Autowired
    private BeaconRepository beaconRepository;








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







    public Map<Integer, Map<String, String>> findCowLocationdiff34(int minutes, double alpha, double beta, int cow) {

        List<PositionSanitized> allpositions = getTestPositions(cow );

        if (allpositions.isEmpty()) {
            return null;
        }

        // Group positions by time
        List<List<PositionSanitized>> min10split = splitPositionsByTime(allpositions, minutes);
        Map<Integer, Map<String, String>> debugLog = new HashMap<>();

        // Example call (you have DBSCAN code already)
//        if (!min10split.isEmpty()) {
//            DBSCANLocalizationExample.clusterPositions(min10split.get(0));
//        }

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
//                    double scanRssiDiff = Math.abs(
//                            Double.parseDouble(pos1.getAttributes().get("scanRssi")) -
//                                    Double.parseDouble(pos2.getAttributes().get("scanRssi"))
//                    );
//                    long timeDiff = Math.abs(ChronoUnit.SECONDS.between(pos1.getDeviceTime(), pos2.getDeviceTime()));

                    synchronized (rssiDiffs) {
                        rssiDiffs.add(rssiDiff);
//                        scanRssiDiffs.add(scanRssiDiff);
//                        timeDiffs.add(timeDiff);
                    }
                }
            });

//            if (rssiDiffs.isEmpty() || scanRssiDiffs.isEmpty() || timeDiffs.isEmpty()) {
//                continue;
//            }

            if (rssiDiffs.isEmpty()){
                System.out.println("emty rssi");
                continue;
            }
            double RSSI_DIFFERENCE_THRESHOLD = average(rssiDiffs);
//            double SCAN_RSSI_DIFFERENCE_THRESHOLD = percentile(scanRssiDiffs, 0.8);
//            double TIME_DIFFERENCE_SECONDS = percentileDouble(timeDiffs, 0.9);

//            positions.sort(Comparator.comparing(PositionSanitized::getDeviceTime));

//            positions.sort(Comparator
//                    .comparing(PositionSanitized::getRssi) // Sort by RSSI in increasing order
//                    .thenComparing(Comparator.comparing(PositionSanitized::getDeviceTime).reversed())); // Sort by time in decreasing order


            Map<String, Integer> beaconFrequency = new HashMap<>();
            Map<String, Integer> beaconPairCount = new HashMap<>();

            List<PositionSanitized> bestMatches = new ArrayList<>();

            // 2) Build bestMatches (pairs that pass thresholds)
            for (int i = 0; i < positions.size(); i++) {
                for (int j = i + 1; j < positions.size(); j++) {
                    // Avoid duplicate pairs
                    PositionSanitized pos1 = positions.get(i);
                    PositionSanitized pos2 = positions.get(j);

//                    double scanRssi1 = Double.parseDouble(pos1.getAttributes().get("scanRssi"));
//                    double scanRssi2 = Double.parseDouble(pos2.getAttributes().get("scanRssi"));
                    double rssiDiff = Math.abs(pos1.getRssi() - pos2.getRssi());
//                    double scanRssiDiff = Math.abs(scanRssi1 - scanRssi2);
//                    long timeDiff = Math.abs(ChronoUnit.SECONDS.between(pos1.getDeviceTime(), pos2.getDeviceTime()));

                    // Filter by thresholds
                    if (rssiDiff > RSSI_DIFFERENCE_THRESHOLD) {
                        continue;
                    }

                    if (pos1.getBeaconName().equals("W10T_A7H_B-1-19 TrÃ¤nke")){
                        System.out.println("------------------------");
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

                    if (beacon1.getId() == beacon2.getId()){
                        System.out.println(beacon1.getName() +"same"+ beacon2.getName());
                        continue;
                    }
                    // Only consider pairs with distance ≤ 10 meters
                    if (distance > 0.011) { // 0.01 degrees ~ 10 meters
                        System.out.println("higer distance  ");
                        System.out.println(distance);
                        System.out.println(beacon1.getName() +"and"+ beacon2.getName());
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

            logDetails.put("total postions", String.valueOf(positions.size()));
//            logDetails.put("startTime", positions.get(0).getDeviceTime().toString());
//            logDetails.put("endTime", positions.get(positions.size()-1).getDeviceTime().toString());
            debugLog.put(count, logDetails);
        }

        return debugLog;
    }


























    private List<PositionSanitized> getTestPositions( int entryNumber) {


        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Register Java 8 Date/Time module
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);  // Ignore unknown fields

        String jsonFilePath = "C:\\Users\\hikev\\Desktop\\thesis\\2024-MA-Kevin-George\\services\\LocationSimulationService\\src\\main\\resources\\data\\beacon_data_cow3at13_52to13_53.json"; // b3
//        String jsonFilePath = "C:\\Users\\hikev\\Desktop\\thesis\\2024-MA-Kevin-George\\services\\LocationSimulationService\\src\\main\\resources\\data\\beacon_data_cow3_13pmto13_56pm_continues.json"; // continues
//        String jsonFilePath = "C:\\Users\\hikev\\Desktop\\thesis\\2024-MA-Kevin-George\\services\\LocationSimulationService\\src\\main\\resources\\data\\beacon_data_cow3at13_06to13_09_T1__camera_8_3_min.json";
//        String jsonFilePath = "C:\\Users\\hikev\\Desktop\\thesis\\2024-MA-Kevin-George\\services\\LocationSimulationService\\data.json";// all data for contiues data

        // Read positions from JSON file instead of database
//        String jsonFilePath = "resources/data/beacon_data_cow3at13_52to13_53.csv";
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


        // Kalman Filters for BLE Positioning
        KalmanFilter latitudeFilter = new KalmanFilter(0.01, 0.5, 1);
        KalmanFilter longitudeFilter = new KalmanFilter(0.01, 0.5, 1);
        KalmanFilter rssiFilter = new KalmanFilter(0.005, 12, 1);


        // Apply Kalman Filter to the positions
        List<PositionSanitized> recentDetections = List.of();


        boolean kalmanfiler = true;
        if (kalmanfiler) {
            logger.info("Kalman Filter applied to all positions.");

            logger.info("Applying Kalman Filter to all positions and filtering data.");

            recentDetections = positionsSubset.stream()
                    .filter(pos -> true &&
                            (pos.getRssi() >= -75) // Filtering out weak signals
                    )
//                    .sorted(Comparator.comparing(PositionSanitized::getDeviceTime, Comparator.reverseOrder())  // Sort by deviceTime descending
//                            .thenComparing(pos -> Math.abs(pos.getRssi()), Comparator.reverseOrder())  // Sort by RSSI descending
//                    )
                    .map(pos -> {
                        // Apply Kalman Filter to latitude, longitude, and RSSI
//                        pos.setLatitude(latitudeFilter.update(pos.getLatitude()));
//                        pos.setLongitude(longitudeFilter.update(pos.getLongitude()));
//                        pos.setRssi((int) rssiFilter.update(pos.getRssi()));
                        return pos;
                    })
                    .collect(Collectors.toList());
        } else {
            System.out.println("---------------");
        }

        // You can add more positions as needed.
        return recentDetections;
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



    public List<List<PositionSanitized>> splitPositionsByTime(List<PositionSanitized> positions, int split ) {
        // Sort positions by device time first
//        positions.sort(Comparator.comparing(PositionSanitized::getDeviceTime));

        List<List<PositionSanitized>> chunks = new ArrayList<>();


        //

//        split by number

//        if (positions.size() > 0) {
        if (1000000 > 0) {
            chunks.add(positions.subList(0, Math.min(split, positions.size())));
        }

        return chunks;

        //

//        if (positions.isEmpty()) {
//            return chunks;
//        }
//
//        positions.sort(Comparator.comparing(PositionSanitized::getDeviceTime));
//        List<PositionSanitized> currentChunk = new ArrayList<>();
//        OffsetDateTime chunkStartTime = positions.get(0).getDeviceTime();
//
//        for (PositionSanitized position : positions) {
//            Duration timeDifference = Duration.between(chunkStartTime, position.getDeviceTime());
//
//            if (timeDifference.toMinutes() >= split) {
//                // Start a new chunk
//                chunks.add(currentChunk);
//                currentChunk = new ArrayList<>();
//                chunkStartTime = position.getDeviceTime();
//            }
//
//            currentChunk.add(position);
//        }
//
//        // Add the last chunk if not empty
//        if (!currentChunk.isEmpty()) {
//            chunks.add(currentChunk);
//        }
//
//        return chunks;
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

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth's radius in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }


}
