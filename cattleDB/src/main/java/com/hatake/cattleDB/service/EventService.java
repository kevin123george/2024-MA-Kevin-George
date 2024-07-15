package com.hatake.cattleDB.service;


import com.hatake.cattleDB.dtos.EventRequest;
import com.hatake.cattleDB.dtos.EventResponse;
import com.hatake.cattleDB.fegin.SafectoryClient;
import com.hatake.cattleDB.models.EventEntity;
import com.hatake.cattleDB.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private SafectoryClient safectoryClient;



    public List<EventResponse> fetchEvents() {
        String authorization = "Basic ==";
        EventRequest request = new EventRequest();
        request.setDeviceId(Collections.singletonList(0));
        request.setTrackableId(Collections.emptyList());
        request.setType(Collections.singletonList("allEvents"));
        request.setFrom("2024-06-21T22:00:00.000Z");
        request.setTo("2024-06-22T22:00:00.000Z");
        request.setFilterSameLocation(false);
        request.setFilterFlapping(false);
        request.setFilterNonBeacon(false);
        request.setGapTime(90);
        request.setFilterDurationMin(300);
        request.setQueryId(null);

        var eventResponses =safectoryClient.getEvents(authorization, request);
        // Convert EventResponse to EventEntity
        List<EventEntity> eventEntities = eventResponses.stream()
                .map(this::convertToEntity)
                .collect(Collectors.toList());

        // Save all entities
        eventRepository.saveAll(eventEntities);

        // Return the responses
        return eventResponses;
    }



    private EventEntity convertToEntity(EventResponse eventResponse) {
        EventEntity eventEntity = new EventEntity();
        eventEntity.setId(eventResponse.getId());
        eventEntity.setName(eventResponse.getName());
//        eventEntity.setAttributes(eventResponse.getAttributes() != null ? eventResponse.getAttributes().toString() : null);
        eventEntity.setType(eventResponse.getType());
        eventEntity.setDeviceId(eventResponse.getDeviceId());
        eventEntity.setTrackableId(eventResponse.getTrackableId());
        eventEntity.setServerTime(eventResponse.getServerTime());
        eventEntity.setDeviceTime(eventResponse.getDeviceTime());
        eventEntity.setPositionId(eventResponse.getPositionId());
        eventEntity.setGeofenceId(eventResponse.getGeofenceId());
        eventEntity.setMessage(eventResponse.getMessage());
        eventEntity.setUserId(eventResponse.getUserId());
        eventEntity.setBeaconId(eventResponse.getBeaconId());
        eventEntity.setBeaconName(eventResponse.getBeaconName());
        eventEntity.setBeaconMajor(eventResponse.getBeaconMajor());
        eventEntity.setBeaconMinor(eventResponse.getBeaconMinor());
        eventEntity.setBeaconUuid(eventResponse.getBeaconUuid());
        return eventEntity;
    }
}
