package com.cattleDB.LocationSimulationService.Service;

import com.cattleDB.LocationSimulationService.dto.TestCaseResult;
import com.cattleDB.LocationSimulationService.models.PositionSanitized;
import com.cattleDB.LocationSimulationService.models.SimulatedLocation;
import com.cattleDB.LocationSimulationService.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EvaluationService {

    private final SimulatedLocationRepository simulatedLocationRepository;
    private final PositionSanitizedRepository positionSanitizedRepository;


    private static final Logger logger = LoggerFactory.getLogger(SimulationService.class);

    public EvaluationService( SimulatedLocationRepository simulatedLocationRepository, PositionSanitizedRepository positionSanitizedRepository) {

        this.simulatedLocationRepository = simulatedLocationRepository;
        this.positionSanitizedRepository = positionSanitizedRepository;
    }

    public TestCaseResult calculateTestCase(List<SimulatedLocation> locations) {
        TestCaseResult result = new TestCaseResult();
        result.testCase = 1;

        // Total Locations
        result.totalLocations = locations.size();

        // Distinct Entities
        result.distinctEntities = locations.stream()
                .map(SimulatedLocation::getCattleID)
                .distinct()
                .count();

        // Covered Spaces
        result.coveredSpaces = locations.stream()
                .map(SimulatedLocation::getSpaceID)
                .distinct()
                .count();

        // Most Visited Space
        result.mostVisitedSpace = locations.stream()
                .collect(Collectors.groupingBy(SimulatedLocation::getSpaceID, Collectors.counting()))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        // Total and Average Spaces Moved
        Map<String, List<SimulatedLocation>> groupedByCattle = locations.stream()
                .sorted(Comparator.comparing(SimulatedLocation::getStartTime))
                .collect(Collectors.groupingBy(SimulatedLocation::getCattleID));

        long totalSpaceChanges = 0;
        List<Long> spaceChangesPerCattle = new ArrayList<>();

        for (List<SimulatedLocation> cattleLocations : groupedByCattle.values()) {
            long changes = 0;
            String prevSpaceId = null;

            for (SimulatedLocation loc : cattleLocations) {
                if (prevSpaceId != null && !prevSpaceId.equals(loc.getSpaceID())) {
                    changes++;
                }
                prevSpaceId = loc.getSpaceID();
            }

            totalSpaceChanges += changes;
            spaceChangesPerCattle.add(changes);
        }

        result.totalSpacesMoved = totalSpaceChanges;
        result.avgSpacesMoved = spaceChangesPerCattle.stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0.0);

        // Average Distance Moved
        double totalDistance = 0.0;
        List<Double> distancesPerCattle = new ArrayList<>();

        for (List<SimulatedLocation> cattleLocations : groupedByCattle.values()) {
            double cattleDistance = 0.0;
            SimulatedLocation prevLocation = null;

            for (SimulatedLocation loc : cattleLocations) {
                if (prevLocation != null) {
                    cattleDistance += haversine(
                            prevLocation.getLatitude(), prevLocation.getLongitude(),
                            loc.getLatitude(), loc.getLongitude()
                    );
                }
                prevLocation = loc;
            }

            totalDistance += cattleDistance;
            distancesPerCattle.add(cattleDistance);
        }

        result.avgDistanceMovedInKm = distancesPerCattle.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);

        return result;
    }

    // Haversine formula to calculate distance in kilometers
    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS = 6371; // Radius of Earth in kilometers

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }

    public Map<String, Object> evaluateSimulatedDataAgainstSanitizedData(double distanceThresholdKm, long timeThresholdMinutes) {

        ZonedDateTime start = ZonedDateTime.parse("2024-11-25T00:00:00Z");
        ZonedDateTime end = ZonedDateTime.parse("2024-11-26T23:59:59Z");

        // Fetch simulated locations and sanitized positions
        List<SimulatedLocation> simulatedLocations = simulatedLocationRepository.findAll();
        var simulatedLocationsMap = simulatedLocations.stream()
                .collect(Collectors.groupingBy(SimulatedLocation::getCattleID));

        List<PositionSanitized> sanitizedPositions = positionSanitizedRepository.findByDeviceTimeBetween(
                start.withZoneSameInstant(ZoneId.of("UTC-5")).toOffsetDateTime(),
                end.withZoneSameInstant(ZoneId.of("UTC-5")).toOffsetDateTime());

        var sanitizedPositionMap = sanitizedPositions.stream()
                .filter(positionSanitized -> positionSanitized.getBeaconMinor() != null)
                .collect(Collectors.groupingBy(PositionSanitized::getBeaconMinor));

        logger.info("Starting evaluation of simulated data against sanitized data.");
        logger.info("Total simulated locations: " + simulatedLocations.size());
        logger.info("Total sanitized positions: " + sanitizedPositions.size());

        // Overall statistics
        int totalMatched = 0;
        int totalSimulatedCount = 0;
        int totalSanitizedCount = sanitizedPositions.size();

        Map<String, Map<String, Object>> individualResults = new HashMap<>();

        // Process for each key in simulatedLocationsMap
        for (Map.Entry<String, List<SimulatedLocation>> entry : simulatedLocationsMap.entrySet()) {

            try {
                if (entry.getKey() != null && Integer.parseInt(entry.getKey()) > 20) {
                    continue;
                }
            } catch (NumberFormatException e) {
                logger.warn("Invalid key format: " + entry.getKey(), e);
                continue; // Skip invalid keys
            }

            Integer cattleID = Integer.valueOf(entry.getKey());
            List<SimulatedLocation> simulatedList = entry.getValue();
            List<PositionSanitized> sanitizedList = sanitizedPositionMap.getOrDefault(cattleID, List.of());

            int matched = 0;

            for (SimulatedLocation simulated : simulatedList) {
                for (PositionSanitized sanitized : sanitizedList) {


                    if (matches(simulated, sanitized, distanceThresholdKm, timeThresholdMinutes)) {
                        matched++;
                        break;
                    }
                }
            }

            totalMatched += matched;
            totalSimulatedCount += simulatedList.size();

            // Calculate individual precision, recall, and accuracy
            double precision = simulatedList.isEmpty() ? 0 : (double) matched / simulatedList.size();
            double recall = sanitizedList.isEmpty() ? 0 : (double) matched / sanitizedList.size();
            double accuracy = (precision + recall) / 2;

            // Store individual results
            Map<String, Object> individualStats = new HashMap<>();
            individualStats.put("matched", matched);
            individualStats.put("simulatedCount", simulatedList.size());
            individualStats.put("sanitizedCount", sanitizedList.size());
            individualStats.put("precision", precision);
            individualStats.put("recall", recall);
            individualStats.put("accuracy", accuracy);

            individualResults.put(String.valueOf(cattleID), individualStats);

            logger.info("Processed cattleID: " + cattleID + " - Matched: " + matched +
                    ", Precision: " + precision + ", Recall: " + recall + ", Accuracy: " + accuracy);
        }

        // Calculate overall precision, recall, and accuracy
        double overallPrecision = totalSimulatedCount == 0 ? 0 : (double) totalMatched / totalSimulatedCount;
        double overallRecall = totalSanitizedCount == 0 ? 0 : (double) totalMatched / totalSanitizedCount;
        double overallAccuracy = (overallPrecision + overallRecall) / 2;

        logger.info("Evaluation completed.");
        logger.info("Overall Matched: " + totalMatched);
        logger.info("Overall Precision: " + overallPrecision);
        logger.info("Overall Recall: " + overallRecall);
        logger.info("Overall Accuracy: " + overallAccuracy);

        // Prepare final result
        Map<String, Object> result = new HashMap<>();
        result.put("totalSimulated", totalSimulatedCount);
        result.put("totalSanitized", totalSanitizedCount);
        result.put("totalMatched", totalMatched);
        result.put("overallPrecision", overallPrecision);
        result.put("overallRecall", overallRecall);
        result.put("overallAccuracy", overallAccuracy);
        result.put("individualResults", individualResults);

        return result;
    }

    private boolean matches(SimulatedLocation simulated, PositionSanitized sanitized, double distanceThresholdKm, long timeThresholdMinutes) {
        double distance = calculateDistance(
                simulated.getLatitude(), simulated.getLongitude(),
                sanitized.getLatitude(), sanitized.getLongitude()
        );

        boolean isWithinDistance = distance <= distanceThresholdKm;

        // Check if sanitized.getFixTime() is null and skip time comparison if it is
        if (sanitized.getFixTime() == null) {
            logger.info("Skipping time comparison for sanitized position ID: " + sanitized.getId() + " due to null FixTime.");
            return isWithinDistance;
        }

        boolean isWithinTime = Math.abs(ChronoUnit.MINUTES.between(
                simulated.getStartTime().toInstant(),
                sanitized.getFixTime().toInstant())) <= timeThresholdMinutes;

//        return isWithinDistance && isWithinTime;
        return isWithinDistance;
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS = 6371; // Radius in kilometers

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }


}
