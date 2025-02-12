package com.cattleDb.DataInjectionService;

import com.cattleDb.DataInjectionService.dtos.BeaconResponse;
import com.cattleDb.DataInjectionService.fegin.SafectoryClient;
import com.cattleDb.DataInjectionService.models.BeaconEntity;
import com.cattleDb.DataInjectionService.repository.BeaconRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BeaconController {

    @Autowired
    private BeaconRepository beaconRepository;

    @Autowired
    private SafectoryClient safectoryClient;

    @GetMapping("/beacons")
    public List<BeaconEntity> fetchPositions() {
        // For demonstration, the authorization header is hardcoded
        // but typically you'd get it from the request (e.g. @RequestHeader)
        String authorization = "Basic a2V2aW4uZ2VvcmdlQHN0dWQudW5pLWJhbWJlcmcuZGU6QmxhY2tiaXJkMTIzIQ==";

        // Step 1: Fetch the responses
        List<BeaconResponse> beaconResponses = safectoryClient.getBeacons(authorization, "json", ",");

        // Step 2: Map each BeaconResponse to BeaconEntity
        List<BeaconEntity> beaconEntities = beaconResponses.stream()
                .map(this::mapToEntity)
                .toList();

        // Step 3: Save the list of entities to the database
        beaconRepository.saveAll(beaconEntities);

        // Step 4: Return the responses (or you can return saved entities,
        // depending on your requirement)
        return beaconEntities;
    }

    private BeaconEntity mapToEntity(BeaconResponse response) {
        BeaconEntity entity = new BeaconEntity();
        entity.setId(response.getId());
        entity.setName(response.getName());
        entity.setDeviceId(response.getDeviceId());
        entity.setUuid(response.getUuid());
        entity.setMajor(response.getMajor());
        entity.setMinor(response.getMinor());
        entity.setLatitude(response.getLatitude());
        entity.setLongitude(response.getLongitude());
        entity.setLastSeen(response.getLastSeen());
        entity.setBatteryLevel(response.getBatteryLevel());
        entity.setRelative(response.isRelative());
        entity.setRoleGroupId(response.getRoleGroupId());
        return entity;
    }
}
