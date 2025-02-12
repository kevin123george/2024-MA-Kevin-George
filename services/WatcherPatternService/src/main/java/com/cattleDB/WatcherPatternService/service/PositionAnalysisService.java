package com.cattleDB.WatcherPatternService.service;

import com.cattleDB.WatcherPatternService.models.*;
import com.cattleDB.WatcherPatternService.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import smile.clustering.DBSCAN;
import smile.math.distance.Distance;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
public class PositionAnalysisService {

    private static final Logger logger = LoggerFactory.getLogger(PositionAnalysisService.class);

    private final ExecutorService executorService;

    @Autowired
    private PositionSanitizedRepository positionSanitizedRepository;

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private DeviceMovementPatternRepository deviceMovementPatternRepository;

    @Autowired
    private DeviceAnalysisRepository deviceAnalysisRepository;

    @Autowired
    private ConfigService configService;

    // Constructor to initialize ExecutorService dynamically
    public PositionAnalysisService(ConfigService configService) {
        int threadPoolSize = configService.getConfigValueAsInt("executor_config", "executor_thread_pool_size", 8);
        this.executorService = Executors.newFixedThreadPool(threadPoolSize);
    }

    // Main method to analyze positions and detect anomalies
    public void analyzePositions() {
        OffsetDateTime analysisTimeWindow = OffsetDateTime.now()
                .minusWeeks(configService.getConfigValueAsInt("location_analysis", "analysis_time_window_weeks", 2));

        List<PositionSanitized> positions = positionSanitizedRepository.findRecentPositions(analysisTimeWindow);
        Map<Long, List<PositionSanitized>> positionsByDevice = positions.stream()
                .filter(position -> position.getDeviceId() != null)
                .collect(Collectors.groupingBy(PositionSanitized::getDeviceId));

        // Verify executor status before submitting tasks
        if (executorService.isShutdown() || executorService.isTerminated()) {
            logger.error("ExecutorService is already shut down. Cannot submit tasks.");
            return;
        }

        List<Future<?>> futures = new ArrayList<>();

        // Submit analysis tasks
        for (Map.Entry<Long, List<PositionSanitized>> entry : positionsByDevice.entrySet()) {
            Long deviceId = entry.getKey();
            List<PositionSanitized> devicePositions = entry.getValue();

            futures.add(executorService.submit(() -> analyzeDeviceMovementPatterns(deviceId, devicePositions)));
        }

        futures.add(executorService.submit(() -> signalStrengthAnalysis(positionsByDevice)));
        futures.add(executorService.submit(() -> locationAnalysis(positionsByDevice)));
        futures.add(executorService.submit(() -> beaconAndDeviceAnalysis(positionsByDevice)));

        waitForCompletion(futures);
    }

    private void waitForCompletion(List<Future<?>> futures) {
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                logger.error("Error during analysis execution: {}", e.getMessage(), e);
            }
        }
    }

    private void saveAlert(Long positionId, String message) {
        Alert alert = new Alert();
        alert.setPositionId(positionId);
        alert.setMessage(message);
        alert.setTimestamp(OffsetDateTime.now());
        alertRepository.save(alert);
    }

    // 1. Location Analysis
    public void locationAnalysis(Map<Long, List<PositionSanitized>> positionsByDevice) {
        String analysisType = "location_analysis";

        int locationThreshold = configService.getConfigValueAsInt(analysisType, "location_frequency_threshold_percentage", 90);
        int topLocationsLimit = configService.getConfigValueAsInt(analysisType, "top_frequent_locations_limit", 5);

        positionsByDevice.forEach((deviceId, devicePositions) -> {
            Map<String, Long> locationFrequency = devicePositions.stream()
                    .collect(Collectors.groupingBy(
                            pos -> pos.getLatitude() + "," + pos.getLongitude(),
                            Collectors.counting()
                    ));

            String topLocations = locationFrequency.entrySet().stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                    .limit(topLocationsLimit)
                    .map(entry -> "Location: " + entry.getKey() + " Frequency: " + entry.getValue())
                    .collect(Collectors.joining("; "));

            long totalPositions = devicePositions.size();
            locationFrequency.entrySet().stream().findFirst().ifPresent(mostFrequent -> {
                if (mostFrequent.getValue() > locationThreshold / 100.0 * totalPositions) {
                    saveAlert(devicePositions.get(0).getId(), "Device " + deviceId + " stayed too long at: " + mostFrequent.getKey());
                }
            });

            saveDeviceAnalysis(deviceId, null, null, topLocations, null, null);
        });
    }

    // 2. Signal Strength Analysis
    public void signalStrengthAnalysis(Map<Long, List<PositionSanitized>> positionsByDevice) {
        String analysisType = "signal_strength_analysis";

        double weakThreshold = configService.getConfigValueAsDouble(analysisType, "rssi_anomaly_weak_threshold", -90);
        double strongThreshold = configService.getConfigValueAsDouble(analysisType, "rssi_anomaly_strong_threshold", -30);

        positionsByDevice.forEach((deviceId, devicePositions) -> {
            double averageRssi = devicePositions.stream()
                    .filter(pos -> pos.getRssi() != null)
                    .mapToInt(PositionSanitized::getRssi)
                    .average()
                    .orElse(Double.NaN);

            if (averageRssi < weakThreshold || averageRssi > strongThreshold) {
                saveAlert(devicePositions.get(0).getId(), "Anomalous signal strength for Device " + deviceId + ": Average RSSI = " + averageRssi);
            }

            double averageScanRssi = devicePositions.stream()
                    .filter(pos -> pos.getScanRssi() != null)
                    .mapToInt(PositionSanitized::getScanRssi)
                    .average()
                    .orElse(Double.NaN);

            saveDeviceAnalysis(deviceId, averageRssi, averageScanRssi, null, null, null);
        });
    }

    // 3. Beacon and Device Analysis
    public void beaconAndDeviceAnalysis(Map<Long, List<PositionSanitized>> positionsByDevice) {
        String analysisType = "beacon_analysis";

        deviceMovementPatternRepository.deleteAll();
        int minPositionsWithBeacon = configService.getConfigValueAsInt(analysisType, "min_positions_with_beacon", 1);

        positionsByDevice.forEach((deviceId, devicePositions) -> {
            long devicesWithBeacon = devicePositions.stream()
                    .filter(pos -> pos.getBeaconMajor() != null && pos.getBeaconMinor() != null && pos.getBeaconName() != null)
                    .count();

            long devicesWithoutBeacon = devicePositions.size() - devicesWithBeacon;

            if (devicesWithBeacon < minPositionsWithBeacon) {
                saveAlert(devicePositions.get(0).getId(), "Insufficient beacon data for Device " + deviceId);
            }

            saveDeviceAnalysis(deviceId, null, null, null, devicesWithBeacon, devicesWithoutBeacon);
        });
    }

    // 4. Movement Pattern Analysis
    private void analyzeDeviceMovementPatterns(Long deviceId, List<PositionSanitized> positions) {
        if (positions == null || positions.isEmpty()) {
            return;
        }

        System.out.println("analizing positon");
        System.out.println(deviceId);
        String analysisType = "movement_pattern_analysis";


        double epsilon = 0.00009; // 10 meters in degrees for latitude
        int minPts = 10; // Require at least 10 points for a cluster

        double[][] data = positions.stream()
                .map(pos -> new double[]{pos.getLatitude(), pos.getLongitude()})
                .toArray(double[][]::new);

        DBSCAN<double[]> dbscan = DBSCAN.fit(data, new GeodesicDistance(), minPts, epsilon);
        int[] labels = dbscan.y;

        Map<Integer, List<PositionSanitized>> clusters = new HashMap<>();
        for (int i = 0; i < labels.length; i++) {
            if (labels[i] != -1) {
                clusters.computeIfAbsent(labels[i], k -> new ArrayList<>()).add(positions.get(i));
            }
        }

        System.out.println("cluster size: " + clusters.size());

        long noisePoints = Arrays.stream(labels).filter(label -> label == -1).count();

        System.out.println("noice points: " + noisePoints);
        if (noisePoints >0.00009 * positions.size()) {

            System.out.println("---------------------------");
            saveAlert(positions.get(0).getId(), "High noise in movement data for Device " + deviceId);
        }

        clusters.forEach((clusterId, clusterPositions) -> {
            double avgLat = clusterPositions.stream().mapToDouble(PositionSanitized::getLatitude).average().orElse(0);
            double avgLon = clusterPositions.stream().mapToDouble(PositionSanitized::getLongitude).average().orElse(0);

            DeviceMovementPattern pattern = new DeviceMovementPattern();
            pattern.setDeviceId(deviceId);
            pattern.setCenterLatitude(avgLat);
            pattern.setCenterLongitude(avgLon);
            pattern.setClusterSize(clusterPositions.size());
            pattern.setTimestamp(OffsetDateTime.now());
            pattern.setClusterID(clusterId);


            deviceMovementPatternRepository.save(pattern);
        });


        // Print out the cluster statistics
        clusters.forEach((clusterId, clusterPositions) -> {
            System.out.println("Cluster ID: " + clusterId);
            System.out.println("Cluster Size: " + clusterPositions.size());
            double avgLat = clusterPositions.stream().mapToDouble(PositionSanitized::getLatitude).average().orElse(0);
            double avgLon = clusterPositions.stream().mapToDouble(PositionSanitized::getLongitude).average().orElse(0);
            System.out.println("Cluster Center Latitude: " + avgLat);
            System.out.println("Cluster Center Longitude: " + avgLon);
        });

        System.out.println("Total Clusters: " + clusters.size());
        System.out.println("Largest Cluster: " +
                clusters.values().stream().mapToInt(List::size).max().orElse(0) + " points");
        System.out.println("Smallest Cluster: " +
                clusters.values().stream().mapToInt(List::size).min().orElse(0) + " points");

        System.out.println("Noise Points: " + noisePoints);
    }

    // Save Device Analysis
    private void saveDeviceAnalysis(Long deviceId, Double averageRssi, Double averageScanRssi, String topLocations,
                                    Long devicesWithBeacon, Long devicesWithoutBeacon) {
        DeviceAnalysis analysis = new DeviceAnalysis();
        analysis.setDeviceId(deviceId);
        analysis.setAverageRssi(averageRssi);
        analysis.setAverageScanRssi(averageScanRssi);
        analysis.setTopLocations(topLocations);
        analysis.setDevicesWithBeacon(devicesWithBeacon);
        analysis.setDevicesWithoutBeacon(devicesWithoutBeacon);
        analysis.setTimestamp(OffsetDateTime.now());
        deviceAnalysisRepository.save(analysis);
    }

    // Custom GeodesicDistance class using the Haversine formula
    public static class GeodesicDistance implements Distance<double[]> {
        private static final int EARTH_RADIUS = 6371;

        @Override
        public double d(double[] point1, double[] point2) {
            double lat1 = point1[0];
            double lon1 = point1[1];
            double lat2 = point2[0];
            double lon2 = point2[1];

            double latDistance = Math.toRadians(lat2 - lat1);
            double lonDistance = Math.toRadians(lon2 - lon1);

            double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                    + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                    * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            return EARTH_RADIUS * c;
        }
    }
}
