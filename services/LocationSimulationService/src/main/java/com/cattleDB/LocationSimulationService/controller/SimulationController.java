//package com.cattleDB.LocationSimulationService.controller;
//
//import com.cattleDB.LocationSimulationService.models.LoadHistory;
//import com.cattleDB.LocationSimulationService.models.Measurement;
//import com.cattleDB.LocationSimulationService.models.Position;
//import com.cattleDB.LocationSimulationService.models.SimulatedLocation;
//import com.cattleDB.LocationSimulationService.repository.*;
//import jakarta.transaction.Transactional;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.format.annotation.DateTimeFormat;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.jdbc.datasource.DriverManagerDataSource;
//import org.springframework.web.bind.annotation.*;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import javax.sql.DataSource;
//import java.time.*;
//import java.util.*;
//
//@RestController
//@RequestMapping("/mysqlback")
//@CrossOrigin("*")
//public class SimulationController {
//
//    private final PositionRepository positionRepository;
//    private final LoadHistoryRepository loadHistoryRepository;
//    private final MeasurementRepository measurementRepository;
//    private final SimulatedLocationRepository simulatedLocationRepository;
//
//
//    @Value("${is.docker}")
//    private Boolean isDocker;
//
//    private static final Logger logger = LoggerFactory.getLogger(SimulationController.class);
//
//    // Updated constructor to include SimulatedLocationRepository
//    @Autowired
//    public SimulationController(PositionRepository positionRepository,
//                                LoadHistoryRepository loadHistoryRepository,
//                                MeasurementRepository measurementRepository,
//                                SimulatedLocationRepository simulatedLocationRepository) {
//        this.positionRepository = positionRepository;
//        this.loadHistoryRepository = loadHistoryRepository;
//        this.measurementRepository = measurementRepository;
//        this.simulatedLocationRepository = simulatedLocationRepository;
//    }
//    // Create a JdbcTemplate instance for MySQL directly in the controller
//    private JdbcTemplate getJdbcTemplateForMySQL() {
//        DataSource dataSource = getMySQLDataSource();
//        return new JdbcTemplate(dataSource);
//    }
//
//    // Create MySQL DataSource directly in the controller
//    private DataSource getMySQLDataSource() {
//        DriverManagerDataSource dataSource = new DriverManagerDataSource();
//        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
//        String url;
//        if (isDocker) {
//            url = "jdbc:mysql://host.docker.internal:3306/simcattle?useSSL=false&serverTimezone=UTC";
//        } else {
//            url = "jdbc:mysql://localhost:3306/simcattle?useSSL=false&serverTimezone=UTC";
//        }
//
//        dataSource.setUrl(url);
////        dataSource.setUrl("jdbc:mysql://host.docker.internal:3306/simcattle?useSSL=false&serverTimezone=UTC");
//        dataSource.setUsername("user");
//        dataSource.setPassword("password");
//        return dataSource;
//    }
//
//    // Combined endpoint to fetch the schema (columns) and data of a specific table
//    @GetMapping("/tables/{tableName}/details")
//    public Map<String, Object> getTableDetails(@PathVariable String tableName) {
//        Map<String, Object> response = new HashMap<>();
//
//        // Fetch the schema (column details)
//        String schemaSql = "SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE, COLUMN_KEY, COLUMN_DEFAULT, EXTRA " +
//                "FROM information_schema.columns " +
//                "WHERE table_name = ? AND table_schema = DATABASE()";
//        JdbcTemplate jdbcTemplate = getJdbcTemplateForMySQL();
//        List<Map<String, Object>> schema = jdbcTemplate.queryForList(schemaSql, new Object[]{tableName});
//
//        // Fetch the actual data in the table
//        String dataSql = "SELECT * FROM " + tableName;
//        List<Map<String, Object>> data = jdbcTemplate.queryForList(dataSql);
//
//        // Add both schema and data to the response
//        response.put("schema", schema);
//        response.put("data", data);
//
//        return response;
//    }
//
//    // Endpoint to fetch all tables from the MySQL database
//    @GetMapping("/tables")
//    public List<Map<String, Object>> getAllTablesFromMySQL() {
//        String sql = "SELECT table_name FROM information_schema.tables WHERE table_schema = DATABASE()";
//        JdbcTemplate jdbcTemplate = getJdbcTemplateForMySQL();
//        return jdbcTemplate.queryForList(sql);
//    }
//
//    // Endpoint to fetch the schema (columns) of a specific table
//    @GetMapping("/tables/{tableName}/schema")
//    public List<Map<String, Object>> getTableSchema(@PathVariable String tableName) {
//        // SQL query to get schema information for a specific table
//        String sql = "SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE, COLUMN_KEY, COLUMN_DEFAULT, EXTRA " +
//                "FROM information_schema.columns " +
//                "WHERE table_name = ? AND table_schema = DATABASE()";
//
//        JdbcTemplate jdbcTemplate = getJdbcTemplateForMySQL();
//
//        // Execute the query with the specified table name
//        return jdbcTemplate.queryForList(sql, new Object[]{tableName});
//    }
//
//
//    @GetMapping("/tables/{tableName}")
//    public List<Map<String, Object>> getTable(@PathVariable String tableName) {
//        // SQL query to get schema information for a specific table
//        String sql = "SELECT * " +
//                "FROM " + tableName ;
//
//        JdbcTemplate jdbcTemplate = getJdbcTemplateForMySQL();
//
//        // Execute the query with the specified table name
//        return jdbcTemplate.queryForList(sql);
//    }
//
//    @GetMapping("/tables/init-measure")
//    @Transactional
//    public List<Map<String, Object>> getPositionsByDateRange(@RequestParam("startDate") String startDate,
//                                                             @RequestParam("endDate") String endDate) {
//        Logger logger = LoggerFactory.getLogger(this.getClass());
//
//        LocalDate startLocalDate = LocalDate.parse(startDate);
//        LocalDate endLocalDate = LocalDate.parse(endDate);
//
//        OffsetDateTime startDateTime = startLocalDate.atStartOfDay(ZoneOffset.UTC).toOffsetDateTime();
//        OffsetDateTime endDateTime = endLocalDate.atTime(LocalTime.MAX).atOffset(ZoneOffset.UTC);
//
//        long startTime = System.currentTimeMillis();
//        logger.info("Start processing for date range: {} to {}", startDateTime, endDateTime);
//
//        int rowsLoaded = 0;
//        String status = "Success";
//        var jdbcTemplate = getJdbcTemplateForMySQL();
//
//        try {
//            // Step 1: Delete existing records
//            logger.info("Deleting existing records from 'measurement' and 'learning_measurement' tables...");
//            jdbcTemplate.update("DELETE FROM measurement");
//            jdbcTemplate.update("DELETE FROM learning_measurement");
//
//            // Step 2: Fetch data from Position
//            logger.info("Fetching positions from the repository...");
//            List<Position> positions = positionRepository.findPositionsByDeviceTimeBetween(startDateTime, endDateTime);
//            logger.debug("Total positions fetched: {}", positions.size());
//
//            // Filter unique entries
//            Set<String> uniqueEntries = new HashSet<>();
//            List<Position> newPositions = new ArrayList<>();
//            for (Position position : positions) {
//                String uniqueKey = position.getLatitude() + "|" +
//                        position.getLongitude() + "|" +
//                        position.getBeaconName() + "|" +
//                        position.getDeviceTime();
//
//                if (uniqueEntries.add(uniqueKey)) {
//                    newPositions.add(position);
//                }
//            }
//            logger.debug("Filtered positions to retain unique entries: {}", newPositions.size());
//
//            // Step 3: Batch insert into measurement table
//            logger.info("Inserting filtered positions into 'measurement' table...");
//            String insertSql = "INSERT INTO measurement (valid, device, time, geofences, latitude, longitude, altitude, beacon, major, minor, uuid, speed, attributes) " +
//                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
//            List<Object[]> batchArgs = new ArrayList<>();
//            for (Position position : newPositions) {
//                if (position.getBeaconName() == null) continue;
//
//                Object[] args = new Object[]{
//                        position.isValid(),
//                        position.getDeviceName(),
//                        position.getDeviceTime(),
//                        "AMS",
//                        position.getLatitude(),
//                        position.getLongitude(),
//                        position.getAttributes().getOrDefault("altitude", "0"),
//                        position.getBeaconName(),
//                        position.getBeaconMajor(),
//                        position.getBeaconMinor(),
//                        position.getId(),
//                        position.getSpeed(),
//                        convertAttributesToString(position.getAttributes())
//                };
//                batchArgs.add(args);
//            }
//            int[] updateCounts = jdbcTemplate.batchUpdate(insertSql, batchArgs);
//            rowsLoaded = updateCounts.length;
//            logger.info("Inserted {} rows into 'measurement' table.", rowsLoaded);
//
//            // Step 4: Insert into learning_measurement
//            logger.info("Inserting data into 'learning_measurement' table...");
//            String insertLearningMeasurementSql = "INSERT INTO simcattle.learning_measurement (wifi_ap, cnx_time, client_id) " +
//                    "SELECT minor, time, device FROM simcattle.measurement";
//            jdbcTemplate.update(insertLearningMeasurementSql);
//
//            logger.info("Successfully updated 'learning_measurement' table.");
//
//            // Step 5: Save Load History
//            long endTime = System.currentTimeMillis();
//            long timeTaken = endTime - startTime;
//            logger.info("Operation completed successfully in {} ms.", timeTaken);
//
//            LoadHistory history = new LoadHistory();
//            history.setStartDate(startDateTime.toLocalDateTime());
//            history.setEndDate(endDateTime.toLocalDateTime());
//            history.setRowsLoaded(rowsLoaded);
//            history.setTimeTakenMs(timeTaken);
//            history.setStatus(status);
//            history.setLoadTimestamp(LocalDateTime.now());
//            loadHistoryRepository.save(history);
//
//            // Fetch and return the newly inserted data
//            logger.info("Fetching data from 'measurement' table...");
//            String fetchDataSql = "SELECT * FROM measurement";
//            return jdbcTemplate.queryForList(fetchDataSql);
//
//        } catch (Exception e) {
//            logger.error("Error occurred during data processing: {}", e.getMessage(), e);
//
//            // Record failed load into history
//            long endTime = System.currentTimeMillis();
//            long timeTaken = endTime - startTime;
//
//            LoadHistory history = new LoadHistory();
//            history.setStartDate(startDateTime.toLocalDateTime());
//            history.setEndDate(endDateTime.toLocalDateTime());
//            history.setRowsLoaded(rowsLoaded);
//            history.setTimeTakenMs(timeTaken);
//            history.setStatus("Failed");
//            history.setLoadTimestamp(LocalDateTime.now());
//            loadHistoryRepository.save(history);
//
//            throw e; // Re-throw exception after logging
//        }
//    }
//    @GetMapping("/sync-simulated-location")
//    public String syncSimulatedLocation() {
//        var jdbcTemplate = getJdbcTemplateForMySQL();
//        String fetchDataSql = "SELECT * FROM location";
//        var locations = jdbcTemplate.queryForList(fetchDataSql);
//
//        // Iterate over the fetched locations and save them into the simulatedLocation table
//        locations.forEach(location -> {
//            SimulatedLocation simulatedLocation = new SimulatedLocation();
//
//            // Map data from location to SimulatedLocation object
//            simulatedLocation.setCattleID((String) location.get("cattle_id"));
//            simulatedLocation.setSpaceID((String) location.get("space_id"));
//            simulatedLocation.setLatitude((Double) location.get("latitude"));
//            simulatedLocation.setLongitude((Double) location.get("longitude"));
//
//            // Handle LocalDateTime for start_time and end_time
//            LocalDateTime startTime = (LocalDateTime) location.get("start_time");
//            LocalDateTime endTime = (LocalDateTime) location.get("end_time");
//
//            simulatedLocation.setStartTime(startTime.atZone(ZoneId.systemDefault()));
//            simulatedLocation.setEndTime(endTime.atZone(ZoneId.systemDefault()));
//
//            // Save the entity using the repository
//            simulatedLocationRepository.save(simulatedLocation);
//        });
//
//        return "Sync completed successfully";
//    }
//
//
//    // Utility method to convert attributes map to a string representation (for simplicity)
//    private String convertAttributesToString(Map<String, String> attributes) {
//        // Convert the Map<String, String> to a JSON string or a simple text representation
//        // You can use a JSON library like Jackson if necessary
//        return attributes.toString(); // Simplified version (can replace with actual JSON conversion)
//    }
//
//
//    @GetMapping("/tables/load-history")
//    public List<LoadHistory> getLoadHistory() {
//        return loadHistoryRepository.findAll();
//    }
//
//
//    @GetMapping("/measurement/hist")
//    public Map<String, Integer> getCheckdata() {
//        var messuremetns = measurementRepository.findAll();
//
//
//        HashMap<String, Integer> map = new HashMap<String, Integer>();
//        for(Measurement measurement: messuremetns){
//        var key = measurement.getBeacon() +measurement.getTime().toString();
//        if (map.containsKey(key)){
//            map.put(key, map.getOrDefault(key, 0)+1);
//        }else {
//            map.put(key, 0);
//        }
//
//        }
//        return map;
//    }
//
//    @GetMapping("/cattle-positions-asc")
//    public List<SimulatedLocation> simulateCattlePositionsAsc(){
//
//        return simulatedLocationRepository.findAllByOrderByStartTimeAsc();
//
//    }
//
//    @GetMapping("/cattle-positions")
//    public List<SimulatedLocation> simulateCattlePositions(
//            @RequestParam(value = "startTimeFrom", required = false)
//            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime startTimeFrom,
//            @RequestParam(value = "startTimeTo", required = false)
//            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime startTimeTo,
//            @RequestParam(value = "cattleID", required = false) List<Long> cattleIDs) {
//
//        List<SimulatedLocation> result;
//
//        // Check for filters and fetch data accordingly
//        if (startTimeFrom != null && startTimeTo != null && cattleIDs != null && !cattleIDs.isEmpty()) {
//            result = simulatedLocationRepository.findByStartTimeBetweenAndCattleIDInOrderByStartTimeAsc(startTimeFrom, startTimeTo, cattleIDs);
//        } else if (startTimeFrom != null && startTimeTo != null) {
//            result = simulatedLocationRepository.findByStartTimeBetweenOrderByStartTimeAsc(startTimeFrom, startTimeTo);
//        } else if (cattleIDs != null && !cattleIDs.isEmpty()) {
//            result = simulatedLocationRepository.findByCattleIDInOrderByStartTimeAsc(cattleIDs);
//        } else {
//            result = simulatedLocationRepository.findAllByOrderByStartTimeAsc();
//        }
//
//        return result;
//    }
//
//
//
//
//    @GetMapping("/cattle-ids")
//    public List<String> generateDefaultCattleIds() {
//        return simulatedLocationRepository.findDistinctCattleIds();
//    }
//
//
//}
//
//
