package com.hatake.cattleDB.service;

import com.hatake.cattleDB.dtos.SummaryRequest;
import com.hatake.cattleDB.dtos.SummaryResponse;
import com.hatake.cattleDB.fegin.SafectoryClient;
import com.hatake.cattleDB.models.SummaryEntity;
import com.hatake.cattleDB.repository.SummaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SummaryService {

    @Autowired
    private SafectoryClient safectoryClient;

    @Autowired
    private SummaryRepository summaryRepository;

    private static final int BATCH_SIZE = 500; // You can adjust the batch size as needed

    public void fetchSummary() {
        String authorization = "Basic hahahahahahahahahahaha";
        SummaryRequest request = buildSummaryRequest();

        List<SummaryResponse> routes = safectoryClient.getSummary(authorization, request);
        var entities = mapToEntityList(routes);

        saveEntitiesInBatch(entities, BATCH_SIZE);

        System.out.println(routes);
    }

    public List<SummaryEntity> getAllSummaryEntities() {
        return summaryRepository.findAll();
    }

    public static List<SummaryEntity> mapToEntityList(List<SummaryResponse> responses) {
        return responses.stream()
                .map(SummaryService::mapToEntity)
                .collect(Collectors.toList());
    }

    public static SummaryEntity mapToEntity(SummaryResponse response) {
        SummaryEntity entity = new SummaryEntity();

        entity.setDeviceId(response.getDeviceId());
        entity.setDeviceName(response.getDeviceName());
        entity.setAverageSpeed(response.getAverageSpeed());
        entity.setBeaconCount(response.getBeaconCount());
        entity.setDistance(response.getDistance());
        entity.setEndOdometer(response.getEndOdometer());
        entity.setEngineHours(response.getEngineHours());
        entity.setMaxSpeed(response.getMaxSpeed());
        entity.setPositionCount(response.getPositionCount());
        entity.setSpentFuel(response.getSpentFuel());
        entity.setStartOdometer(response.getStartOdometer());

        return entity;
    }

    public SummaryRequest buildSummaryRequest() {
        SummaryRequest request = new SummaryRequest();
        request.setDeviceId(Collections.singletonList(0));
        request.setTrackableId(Collections.emptyList());
        request.setType(Collections.singletonList("allEvents"));
        request.setFrom("2024-06-23T22:00:00.000Z");
        request.setTo("2024-06-24T22:00:00.000Z");
        request.setFilterSameLocation(false);
        request.setFilterFlapping(false);
        request.setFilterNonBeacon(false);
        request.setGapTime(90);
        request.setFilterDurationMin(300);
        request.setQueryId(null);

        return request;
    }

    private void saveEntitiesInBatch(List<SummaryEntity> entities, int batchSize) {
        int size = entities.size();
        for (int i = 0; i < size; i += batchSize) {
            int end = Math.min(size, i + batchSize);
            List<SummaryEntity> batch = entities.subList(i, end);
            summaryRepository.saveAll(batch);
        }
    }
}
