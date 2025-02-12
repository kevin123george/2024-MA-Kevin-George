package com.cattleDB.LocationSimulationService.Service;

import com.cattleDB.LocationSimulationService.dto.TestCaseResult;
import com.cattleDB.LocationSimulationService.mappers.PositionSanitizedMapper;
import com.cattleDB.LocationSimulationService.models.*;
import com.cattleDB.LocationSimulationService.repository.*;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;


import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class SimulationService {
    private final PositionSanitizedMapper mapper = PositionSanitizedMapper.INSTANCE;


    private final MeasurementRepository measurementRepository;
    @Value("${is.docker}")
    private Boolean isDocker;

    private final PositionRepository positionRepository;
    private final LoadHistoryRepository loadHistoryRepository;
    private final SimulatedLocationRepository simulatedLocationRepository;
    private final PositionSanitizedRepository positionSanitizedRepository;;
    private final ProcessingCheckpointRepository processingCheckpointRepository;
    private final DeduplicationRepository deduplicationRepository;


    private static final Logger logger = LoggerFactory.getLogger(SimulationService.class);

    public SimulationService(PositionRepository positionRepository, LoadHistoryRepository loadHistoryRepository, SimulatedLocationRepository simulatedLocationRepository, PositionSanitizedRepository positionSanitizedRepository, ProcessingCheckpointRepository processingCheckpointRepository, DeduplicationRepository deduplicationRepository, MeasurementRepository measurementRepository) {
        this.positionRepository = positionRepository;
        this.loadHistoryRepository = loadHistoryRepository;
        this.simulatedLocationRepository = simulatedLocationRepository;
        this.positionSanitizedRepository = positionSanitizedRepository;
        this.processingCheckpointRepository = processingCheckpointRepository;
        this.deduplicationRepository = deduplicationRepository;
        this.measurementRepository = measurementRepository;
    }


    public List<Map<String, Object>> getAllTables() {
        var jdbcTemplate = getJdbcTemplateForMySQL();

        String sql = "SELECT table_name FROM information_schema.tables WHERE table_schema = DATABASE()";
        return jdbcTemplate.queryForList(sql);
    }

    public Map<String, Object> getTableDetails(String tableName) {
        Map<String, Object> response = new HashMap<>();
        var jdbcTemplate = getJdbcTemplateForMySQL();
        String schemaSql = "SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE, COLUMN_KEY, COLUMN_DEFAULT, EXTRA " +
                "FROM information_schema.columns WHERE table_name = ? AND table_schema = DATABASE()";
        List<Map<String, Object>> schema = jdbcTemplate.queryForList(schemaSql, tableName);

        String dataSql = "SELECT * FROM " + tableName;
        List<Map<String, Object>> data = jdbcTemplate.queryForList(dataSql);

        response.put("schema", schema);
        response.put("data", data);
        return response;
    }

    public List<SimulatedLocation> simulateCattlePositions(ZonedDateTime startTimeFrom,
                                                           ZonedDateTime startTimeTo,
                                                           List<Long> cattleIDs) {
        if (startTimeFrom != null && startTimeTo != null && cattleIDs != null && !cattleIDs.isEmpty()) {
            return simulatedLocationRepository.findByStartTimeBetweenAndCattleIDInOrderByStartTimeAsc(startTimeFrom, startTimeTo, cattleIDs);
        } else if (startTimeFrom != null && startTimeTo != null) {
            return simulatedLocationRepository.findByStartTimeBetweenOrderByStartTimeAsc(startTimeFrom, startTimeTo);
        } else if (cattleIDs != null && !cattleIDs.isEmpty()) {
            return simulatedLocationRepository.findByCattleIDInOrderByStartTimeAsc(cattleIDs);
        } else {
            return simulatedLocationRepository.findAllByOrderByStartTimeAsc();
        }
    }

    public List<String> getDistinctCattleIDs() {
        return simulatedLocationRepository.findDistinctCattleIds();
    }

    public String syncSimulatedLocation() {
        simulatedLocationRepository.deleteAll();

        String fetchDataSql = "SELECT * FROM location";
        var jdbcTemplate = getJdbcTemplateForMySQL();
        var locations = jdbcTemplate.queryForList(fetchDataSql);
        locations.forEach(location -> {
            SimulatedLocation simulatedLocation = new SimulatedLocation();
            simulatedLocation.setCattleID((String) location.get("cattle_id"));
            simulatedLocation.setSpaceID((String) location.get("space_id"));
            simulatedLocation.setLatitude((Double) location.get("latitude"));
            simulatedLocation.setLongitude((Double) location.get("longitude"));

            LocalDateTime startTime = (LocalDateTime) location.get("start_time");
            LocalDateTime endTime = (LocalDateTime) location.get("end_time");

            simulatedLocation.setStartTime(startTime.atZone(ZoneId.systemDefault()));
            simulatedLocation.setEndTime(endTime.atZone(ZoneId.systemDefault()));

            simulatedLocationRepository.save(simulatedLocation);
        });

        return "Sync completed successfully";
    }

    public List<SimulatedLocation> getAllSimulatedLocations(){
        return simulatedLocationRepository.findAll();
    }

    @Transactional
    public List<Map<String, Object>> initializeMeasurements(String startDate, String endDate) {
        LocalDate startLocalDate = LocalDate.parse(startDate);
        LocalDate endLocalDate = LocalDate.parse(endDate);
        var jdbcTemplate = getJdbcTemplateForMySQL();


        OffsetDateTime startDateTime = startLocalDate.atStartOfDay(ZoneOffset.UTC).toOffsetDateTime();
        OffsetDateTime endDateTime = endLocalDate.atTime(LocalTime.MAX).atOffset(ZoneOffset.UTC);

        long startTime = System.currentTimeMillis();
        logger.info("Start processing for date range: {} to {}", startDateTime, endDateTime);

        int rowsLoaded = 0;
        String status = "Success";

        try {
            jdbcTemplate.update("DELETE FROM measurement");
            jdbcTemplate.update("DELETE FROM learning_measurement");

            List<PositionSanitized> positions = positionSanitizedRepository.findPositionsByDeviceTimeBetween(startDateTime, endDateTime);

            Set<String> uniqueEntries = new HashSet<>();
            List<Object[]> batchArgs = new ArrayList<>();
            for (PositionSanitized position : positions) {
                String uniqueKey = position.getLatitude() + "|" + position.getLongitude() + "|" +
                        position.getBeaconName() + "|" + position.getDeviceTime();

                if (uniqueEntries.add(uniqueKey)) {
                    if (position.getBeaconName() == null) continue;

                    Object[] args = new Object[]{
                            position.isValid(),
                            position.getDeviceName(),
                            position.getDeviceTime(),
                            "AMS",
                            position.getLatitude(),
                            position.getLongitude(),
                            position.getAttributes().getOrDefault("altitude", "0"),
                            position.getBeaconName(),
                            position.getBeaconMajor(),
                            position.getBeaconMinor(),
                            position.getId(),
                            position.getSpeed(),
                            convertAttributesToString(position.getAttributes())
                    };
                    batchArgs.add(args);
                }
            }

            String insertSql = "INSERT INTO measurement (valid, device, time, geofences, latitude, longitude, altitude, beacon, major, minor, uuid, speed, attributes) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            rowsLoaded = jdbcTemplate.batchUpdate(insertSql, batchArgs).length;

            jdbcTemplate.update("INSERT INTO learning_measurement (wifi_ap, cnx_time, client_id) " +
                    "SELECT minor, time, device FROM measurement");

            long endTime = System.currentTimeMillis();
            long timeTaken = endTime - startTime;

            LoadHistory history = new LoadHistory();
            history.setStartDate(startDateTime.toLocalDateTime());
            history.setEndDate(endDateTime.toLocalDateTime());
            history.setRowsLoaded(rowsLoaded);
            history.setTimeTakenMs(timeTaken);
            history.setStatus(status);
            history.setLoadTimestamp(LocalDateTime.now());
            loadHistoryRepository.save(history);




            // update meassuremnts tables


            List<Measurement> newMeasurements = new ArrayList<>();

            var meassurements = processPositionSanitizedData(positions);

            measurementRepository.deleteAll();


            logger.info("saving new measurements");
            measurementRepository.saveAll(meassurements);




            return jdbcTemplate.queryForList("SELECT * FROM measurement");

        } catch (Exception e) {
            logger.error("Error occurred during data processing: {}", e.getMessage(), e);
            throw e;
        }
    }

    private String convertAttributesToString(Map<String, String> attributes) {
        return attributes.toString();
    }


        // Create a JdbcTemplate instance for MySQL directly in the controller
    private JdbcTemplate getJdbcTemplateForMySQL() {
        DataSource dataSource = getMySQLDataSource();
        return new JdbcTemplate(dataSource);
    }

    // Create MySQL DataSource directly in the controller
    private DataSource getMySQLDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        String url;
//        if (isDocker) {
        if (true) {
            url = "jdbc:mysql://host.docker.internal:3306/simcattle?useSSL=false&serverTimezone=UTC";
        } else {
            url = "jdbc:mysql://localhost:3306/simcattle?useSSL=false&serverTimezone=UTC";
        }

        dataSource.setUrl(url);
//        dataSource.setUrl("jdbc:mysql://host.docker.internal:3306/simcattle?useSSL=false&serverTimezone=UTC");
        dataSource.setUsername("user");
        dataSource.setPassword("password");
        return dataSource;
    }


    public List<LoadHistory> loadHistroy(){
        return loadHistoryRepository.findAll();
    }





//    @Scheduled(cron = "0 0 * * * *") // Every hour
    @Transactional
    public long sanitizeAndSavePositions() {
        Logger logger = LoggerFactory.getLogger(this.getClass());
        logger.info("Starting the sanitization process...");

        Long lastProcessedId = processingCheckpointRepository.findLastProcessedId();
        if (lastProcessedId == null) {
            lastProcessedId = 0L;
        }
        logger.info("Last processed ID: {}", lastProcessedId);

        int pageSize = 1000; // Define chunk size
        long totalSanitized = 0;
        int batchCount = 0;

        // Create a fixed thread pool for async operations
        ExecutorService executor = Executors.newFixedThreadPool(14);

        // Get total count for progress calculation
        long totalPositions = positionRepository.countByIdGreaterThan(lastProcessedId);
        logger.info("Total positions to process: {}", totalPositions);

        // HashMap for deduplication
        Map<String, Boolean> uniqueKeyMap = new HashMap<>();

        try (Stream<Position> positionsStream = positionRepository.streamByIdGreaterThanOrderByIdAsc(lastProcessedId)) {
            List<PositionSanitized> batch = new ArrayList<>();
            long processedCount = 0;

            for (Position position : (Iterable<Position>) positionsStream::iterator) {
                String uniqueKey = position.getLatitude() + "|" + position.getLongitude() + "|" +
                        position.getBeaconName() + "|" + position.getDeviceTime() + "|" +
                        position.getDeviceId() + "|" + position.getName() + "|" +
                        position.getProtocol() + "|" + position.getEventId();
                // Use HashMap for deduplication
                if (!uniqueKeyMap.containsKey(uniqueKey)) { // Only process if unique
                    uniqueKeyMap.put(uniqueKey, true); // Mark as processed

                    // Process and sanitize the position
                    PositionSanitized sanitized = mapToSanitized(position);

                    batch.add(sanitized);
                }

                processedCount++;

                // Save in batches
                if (batch.size() >= pageSize) {
                    List<PositionSanitized> batchToSave = new ArrayList<>(batch); // Copy the batch
                    executor.submit(() -> {
                        try {
                            positionSanitizedRepository.saveAllAndFlush(batchToSave);
                            logger.info("Async batch saved to database. Batch size: {}", batchToSave.size());
                        } catch (Exception e) {
                            logger.error("Error saving batch asynchronously", e);
                        }
                    });
                    batch.clear(); // Clear the batch
                    batchCount++;
                    logger.info("Submitted batch {} for async saving.", batchCount);
                }

                // Update last processed ID
                lastProcessedId = position.getId();

                // Report progress every 10%
                if (totalPositions >= 10 && processedCount % (totalPositions / 10) == 0) {
                    int progress = (int) ((processedCount * 100) / totalPositions);
                    logger.info("Processing progress: {}% ({}/{} positions processed)", progress, processedCount, totalPositions);
                }
            }

            // Save remaining batch
            if (!batch.isEmpty()) {
                List<PositionSanitized> batchToSave = new ArrayList<>(batch); // Copy the remaining batch
                executor.submit(() -> {
                    try {
                        positionSanitizedRepository.saveAllAndFlush(batchToSave);
                        logger.info("Async final batch saved to database. Batch size: {}", batchToSave.size());
                    } catch (Exception e) {
                        logger.error("Error saving final batch asynchronously", e);
                    }
                });
                batchCount++;
                logger.info("Submitted final batch {} for async saving.", batchCount);
            }
        } catch (Exception e) {
            logger.error("Error during sanitization process", e);
        } finally {
            executor.shutdown(); // Shutdown executor service
            try {
                if (!executor.awaitTermination(1, TimeUnit.HOURS)) {
                    executor.shutdownNow();
                    if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
                        logger.error("Executor did not terminate");
                    }
                }
            } catch (InterruptedException ie) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        // Update the checkpoint
//        processingCheckpointRepository.updateLastProcessedId(lastProcessedId);
        ProcessingCheckpoint lastCheckpoitn  = new ProcessingCheckpoint();
        lastCheckpoitn.setLastProcessedId(lastProcessedId);
        processingCheckpointRepository.save(lastCheckpoitn);
        logger.info("Updated last processed ID to {}", lastProcessedId);
        logger.info("Sanitization process completed successfully. Total sanitized entries: {}", totalSanitized);
        return totalSanitized;
    }



    public List<SimulatedLocation> simulateCattlePositionsAsc(){

        return simulatedLocationRepository.findAllByOrderByStartTimeAsc();

    }




    public static PositionSanitized mapToSanitized(Position position) {
        if (position == null) {
            return null; // Handle null input gracefully
        }

        return new PositionSanitized(
                position.getId(),
                position.getName(),
                position.getAttributes() != null ? Map.copyOf(position.getAttributes()) : null, // Copy map to ensure immutability
                position.getProtocol(),
                position.getServerTime(),
                position.getDeviceTime(),
                position.getFixTime(),
                position.isOutdated(),
                position.isValid(),
                position.getLatitude(),
                position.getLongitude(),
                position.getEventId(),
                position.getDeviceId(),
                position.getScanRssi(),
                position.getRssi(),
                position.getBeaconId(),
                position.getBeaconName(),
                position.getBeaconMajor(),
                position.getBeaconMinor(),
                position.getBeaconUuid(),
                position.getSourceDeviceId(),
                position.getDeviceName(),
                position.getSpeed()
        );
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


    public List<Measurement> processPositionSanitizedData(List<PositionSanitized> positionSanitizedList) {
        return mapper.mapToMeasurements(positionSanitizedList);
    }
}
