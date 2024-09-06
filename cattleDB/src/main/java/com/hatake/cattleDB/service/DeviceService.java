package com.hatake.cattleDB.service;


import com.hatake.cattleDB.dtos.DeviceResponse;
import com.hatake.cattleDB.models.Device;
import com.hatake.cattleDB.repository.DeviceRepository;
import com.hatake.cattleDB.fegin.SafectoryClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final SafectoryClient safectoryClient;
    private final DeviceRepository deviceRepository;

    @Transactional
    public void fetchAndStoreDevices() {
        String authorization = "Basic sdfffffffffffffff";

        // Fetch the devices from the external API
        List<DeviceResponse> deviceResponses = safectoryClient.getDevices(
                authorization, "json", ","
        );

        // Map DeviceResponse to Device and save them to the database
        List<Device> devices = deviceResponses.stream()
                .map(this::convertToDeviceEntity)
                .collect(Collectors.toList());

        // Save all devices in a single transaction
        deviceRepository.saveAll(devices);
    }

    public Device convertToDeviceEntity(DeviceResponse response) {
        Device device = new Device();
        device.setId(response.getId());
        device.setName(response.getName());
//        device.setAttributes(response.getAttributes()); // JSONB field
        device.setUniqueId(response.getUniqueId());
        device.setStatus(response.getStatus());
        device.setLastUpdate(response.getLastUpdate());
        device.setGeofences(response.getGeofences());
        device.setActiveGeofences(response.getActiveGeofences());
        device.setNotifications(response.getNotifications());
        device.setPositionId(response.getPositionId());
        device.setPhone(response.getPhone());
        device.setModel(response.getModel());
        device.setContact(response.getContact());
        device.setCategory(response.getCategory());
        device.setDisabled(response.isDisabled());
        device.setAllGeofencesEnabled(response.isAllGeofencesEnabled());
        device.setLastSuccessfulAsnJob(response.getLastSuccessfulAsnJob());
        device.setLastOneTimeJob(response.getLastOneTimeJob());
        device.setLastIncompleteAsnJob(response.getLastIncompleteAsnJob());
        device.setFirstDataTime(response.getFirstDataTime());
        device.setLastAttempt(response.getLastAttempt());
        device.setLastAsnFail(response.getLastAsnFail());
        device.setLastAsnCookie(response.getLastAsnCookie());
        device.setAsnConfigs(response.getAsnConfigs());
        device.setFirmwareVersion(response.getFirmwareVersion());
        device.setBatteryLevel(response.getBatteryLevel());
        device.setLeafGroupName(response.getLeafGroupName());

        return device;
    }

}