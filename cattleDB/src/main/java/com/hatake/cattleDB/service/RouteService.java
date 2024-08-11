package com.hatake.cattleDB.service;

import com.hatake.cattleDB.dtos.RouteRequest;
import com.hatake.cattleDB.dtos.RouteResponse;
import com.hatake.cattleDB.models.RouteEntity;
import com.hatake.cattleDB.repository.RouteRepository;
import com.hatake.cattleDB.fegin.SafectoryClient;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

@Service
public class RouteService {

    private static final Logger logger = LoggerFactory.getLogger(RouteService.class);

    @Autowired
    private SafectoryClient safectoryClient;

    @Autowired
    private RouteRepository routeRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void fetchRoutes() {
        String authorization = "Basic xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx==";
        RouteRequest request = buildRouteRequest();

        logger.info("Fetching routes from external service...");
        List<RouteResponse> routes = safectoryClient.getRoutes(authorization, request);
        var entities = mapToEntityList(routes);

        logger.info("Fetched {} routes. Starting batch insert...", entities.size());
        saveEntitiesInBatch(entities);
        logger.info("Batch insert completed.");
    }

    public RouteRequest buildRouteRequest() {
        RouteRequest routeRequest = new RouteRequest();
        routeRequest.setDeviceIds(Collections.singletonList(0));
        routeRequest.setTrackableIds(Collections.emptyList());
        routeRequest.setTypes(Collections.singletonList("allEvents"));
        routeRequest.setFrom("2024-06-23T22:00:00.000Z");
        routeRequest.setTo("2024-06-24T22:00:00.000Z");
        routeRequest.setFilterSameLocation(false);
        routeRequest.setFilterFlapping(false);
        routeRequest.setFilterNonBeacon(false);
        routeRequest.setGapTime(90);
        routeRequest.setFilterDurationMin(300);
        routeRequest.setQueryId(null);

        return routeRequest;
    }

    private void saveEntitiesInBatch(List<RouteEntity> entities) {
        int batchSize = 50;  // Set the batch size
        int totalEntities = entities.size();

        for (int i = 0; i < totalEntities; i++) {
            entityManager.persist(entities.get(i));

            // Flush and clear the persistence context every batch
            if (i % batchSize == 0 && i > 0) {
                entityManager.flush();
                entityManager.clear();
                logger.info("Processed {} entities out of {}", i, totalEntities);
            }
        }

        // Flush the remaining entities
        entityManager.flush();
        entityManager.clear();
        logger.info("All entities have been processed and saved.");
    }

    public static List<RouteEntity> mapToEntityList(List<RouteResponse> dtos) {
        List<RouteEntity> routeEntities = new java.util.ArrayList<>(List.of());
        dtos.forEach(dto -> {
            RouteEntity entity = new RouteEntity();
            entity.setId(dto.getId());
            entity.setName(dto.getName());
            entity.setAttributes(dto.getAttributes());
            entity.setProtocol(dto.getProtocol());
            entity.setServerTime(dto.getServerTime());
            entity.setDeviceTime(dto.getDeviceTime());
            entity.setFixTime(dto.getFixTime());
            entity.setOutdated(dto.isOutdated());
            entity.setValid(dto.isValid());
            entity.setLatitude(dto.getLatitude());
            entity.setLongitude(dto.getLongitude());
            entity.setEventId(dto.getEventId());
            entity.setDeviceId(dto.getDeviceId());
            entity.setScanRssi(dto.getScanRssi());
            entity.setRssi(dto.getRssi());
            entity.setBeaconId(dto.getBeaconId());
            entity.setBeaconName(dto.getBeaconName());
            entity.setBeaconMajor(dto.getBeaconMajor());
            entity.setBeaconMinor(dto.getBeaconMinor());
            entity.setBeaconUuid(dto.getBeaconUuid());
            entity.setSourceDeviceId(dto.getSourceDeviceId());
            entity.setDeviceName(dto.getDeviceName());

            routeEntities.add(entity);
        });

        return routeEntities;
    }
}
