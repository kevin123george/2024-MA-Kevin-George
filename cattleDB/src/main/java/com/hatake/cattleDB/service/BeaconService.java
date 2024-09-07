package com.hatake.cattleDB.service;

import com.hatake.cattleDB.dtos.BeaconResponse;
import com.hatake.cattleDB.fegin.SafectoryClient;
import com.hatake.cattleDB.models.Beacon;
import com.hatake.cattleDB.repository.BeaconRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BeaconService {

    @Autowired
    private SafectoryClient safectoryClient;

    @Autowired
    private BeaconRepository beaconRepository;
    String authorization = "Basic sdfffffffffffffff";

    // Method to fetch and save beacons
    @Transactional
    public List<Beacon> fetchAndSaveBeacons() {

        // Fetch beacons from the external service via Feign
        List<BeaconResponse> beaconResponseDTOs = safectoryClient.getBeacons(authorization, "json", ",");

        // Map DTOs to Entities and save them
        List<Beacon> beacons = beaconResponseDTOs.stream()
                .map(this::mapToEntity)
                .collect(Collectors.toList());

        // Save all beacons to the database
        return beaconRepository.saveAll(beacons);
    }

    // Helper method to map DTO to Entity
    private Beacon mapToEntity(BeaconResponse dto) {
        Beacon beacon = new Beacon();
        beacon.setName(dto.getName());
        beacon.setAttributes(dto.getAttributes());
        beacon.setDeviceId(dto.getDeviceId());
        beacon.setUuid(dto.getUuid());
        beacon.setMajor(dto.getMajor());
        beacon.setMinor(dto.getMinor());
        beacon.setLatitude(dto.getLatitude());
        beacon.setLongitude(dto.getLongitude());
        beacon.setLastSeen(dto.getLastSeen());
        beacon.setBatteryLevel(dto.getBatteryLevel());
        beacon.setRelative(dto.isRelative());
        beacon.setRoleGroupId(dto.getRoleGroupId());
        return beacon;
    }

    public List<Beacon> getBeacons() {
        return beaconRepository.findAll();
    }
}