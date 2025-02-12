package com.cattleDb.DataInjectionService.service;

import com.cattleDb.DataInjectionService.dtos.PositionResponse;
import com.cattleDb.DataInjectionService.fegin.SafectoryClient;
import com.cattleDb.DataInjectionService.models.Position;
import com.cattleDb.DataInjectionService.repository.PositionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PositionService {

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private SafectoryClient safectoryClient;

    public List<PositionResponse> fetchPositions(String authorizatiosn) {
        String authorization = "Basic a2V2aW4uZ2VvcmdlQHN0dWQudW5pLWJhbWJlcmcuZGU6QmxhY2tiaXJkMTIzIQ==";
        return safectoryClient.getPositions(authorization);
    }


    public List<Position> getPositions() {
        return positionRepository.findAll();
    }

    public List<Map<String, Object>> getLatestPositions() {
        return positionRepository.getLatestPositions();
    }

    public List<Map<String, Object>> fetchPositionsByDevice(Long deviceId) {
        return positionRepository.findPositionsByDeviceId(deviceId);
    }

    public List<Map<String, Object>> getEntriesCountByDeviceId() {
        return positionRepository.countEntriesByDeviceId();
    }

    public Position toEntity(PositionResponse dto) {
        if (dto == null) {
            return null;
        }

        Position position = new Position();
        position.setId(dto.getId());
        position.setName(dto.getName());
        position.setAttributes(dto.getAttributes());
        position.setProtocol(dto.getProtocol());
        position.setServerTime(dto.getServerTime());
        position.setDeviceTime(dto.getDeviceTime());
        position.setFixTime(dto.getFixTime());
        position.setOutdated(dto.isOutdated());
        position.setValid(dto.isValid());
        position.setLatitude(dto.getLatitude());
        position.setLongitude(dto.getLongitude());
        position.setEventId(dto.getEventId() != null ? dto.getEventId().longValue() : null);
        position.setDeviceId(dto.getDeviceId());
        position.setScanRssi(dto.getScanRssi());
        position.setRssi(dto.getRssi());
        position.setBeaconId(dto.getBeaconId());
        position.setBeaconName(dto.getBeaconName());
        position.setBeaconMajor(dto.getBeaconMajor());
        position.setBeaconMinor(dto.getBeaconMinor());
        position.setBeaconUuid(dto.getBeaconUuid());
        position.setSourceDeviceId(dto.getSourceDeviceId());
        position.setDeviceName(dto.getDeviceName());

        return position;
    }

    @Transactional
    public void saveFetchedPositions(String authorization) {
        List<PositionResponse> positionResponses = fetchPositions(authorization);
        var latestPositions = positionRepository.findLatestPositionsForAllDevices();

        // Create a HashMap to store deviceName as key and the rest of the data as value
        var positionMap = new HashMap<String, Map<String, Object>>();

        for (Map<String, Object> row : latestPositions) {
            // Extract deviceName to be the key
            String deviceName = (String) row.get("deviceName");

            // Put the entire row (the map representing the row) into the positionMap with deviceName as key
            positionMap.put(deviceName, row);
        }

        List<Position> positions = positionResponses.stream()
                .map(response -> {
                    Position position = toEntity(response);

                    // Fetch the latest position for this device if it exists
                    Map<String, Object> latestPosition = positionMap.get(position.getDeviceName());

                    if (latestPosition != null) {
                        // Calculate distance between latest and current positions
                        double lastLatitude = (Double) latestPosition.get("latitude");
                        double lastLongitude = (Double) latestPosition.get("longitude");
                        double newLatitude = position.getLatitude();
                        double newLongitude = position.getLongitude();

                        double distance = calculateDistance(lastLatitude, lastLongitude, newLatitude, newLongitude);

                        // Convert deviceTime from Instant to OffsetDateTime
                        Instant lastDeviceTimeInstant = (Instant) latestPosition.get("deviceTime");
                        OffsetDateTime lastDeviceTime = OffsetDateTime.ofInstant(lastDeviceTimeInstant, ZoneOffset.UTC);

                        OffsetDateTime newDeviceTime = position.getDeviceTime();

                        // Calculate time difference in seconds
                        long timeDifferenceInSeconds = Duration.between(lastDeviceTime, newDeviceTime).getSeconds();

                        if (timeDifferenceInSeconds > 0) {
                            // Calculate speed (meters per second)
                            double speed = distance / timeDifferenceInSeconds;
                            position.setSpeed(speed); // Assuming your Position entity has a setSpeed method
                        }
                    }

                    return position;
                })
                .collect(Collectors.toList());

        // Batch save
        int batchSize = 50; // This should match the hibernate.jdbc.batch_size in your configuration
        for (int i = 0; i < positions.size(); i += batchSize) {
            int end = Math.min(i + batchSize, positions.size());
            List<Position> batchList = positions.subList(i, end);
            positionRepository.saveAll(batchList);

            // Flush and clear the EntityManager to avoid memory issues
            positionRepository.flush();
            // positionRepository.clear();
        }
    }


    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS = 6371000; // radius in meters

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c; // distance in meters
    }
}
