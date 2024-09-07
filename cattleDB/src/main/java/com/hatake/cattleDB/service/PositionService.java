package com.hatake.cattleDB.service;

import com.hatake.cattleDB.dtos.PositionResponse;
import com.hatake.cattleDB.fegin.SafectoryClient;
import com.hatake.cattleDB.models.Position;
import com.hatake.cattleDB.repository.PositionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PositionService {

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private SafectoryClient safectoryClient;

    private final String authorization = "Basic sdfffffffffffffff";

    public List<PositionResponse> fetchPositions() {
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
    public void saveFetchedPositions() {
        List<PositionResponse> positionResponses = fetchPositions();
        List<Position> positions = positionResponses.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());

        // Batch save
        int batchSize = 50; // This should match the hibernate.jdbc.batch_size in your configuration
        for (int i = 0; i < positions.size(); i += batchSize) {
            int end = Math.min(i + batchSize, positions.size());
            List<Position> batchList = positions.subList(i, end);
            positionRepository.saveAll(batchList);

            // Flush and clear the EntityManager to avoid memory issues
            positionRepository.flush();
//            positionRepository.clear();
        }
    }
}
