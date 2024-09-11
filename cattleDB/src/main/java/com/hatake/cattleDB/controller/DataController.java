package com.hatake.cattleDB.controller;

import com.hatake.cattleDB.dtos.EventResponse;
import com.hatake.cattleDB.dtos.PositionResponse;
import com.hatake.cattleDB.models.Beacon;
import com.hatake.cattleDB.models.Device;
import com.hatake.cattleDB.models.Position;
import com.hatake.cattleDB.models.SummaryEntity;
import com.hatake.cattleDB.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DataController {

    @Autowired
    private EventService eventService;

    @Autowired
    private RouteService routeService;

    @Autowired
    private SummaryService summaryService;

    @Autowired
    private PositionService positionService;

    @Autowired
    private BeaconService beaconService;

    private final DeviceService deviceService;

    String authorization = "Basic a2V2aW4uZ2VvcmdlQHN0dWQudW5pLWJhbWJlcmcuZGU6QmxhY2tiaXJkMTIzIQ==";

    public DataController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    // Endpoint for fetching events
//    @GetMapping("/events/fetch")
//    public List<EventResponse> fetchEvents() {
//        return eventService.fetchEvents();
//    }
//
//    // Endpoint for fetching routes and processing them
//    @PostMapping("/reports/route")
//    public String processRoutes() {
//        routeService.fetchRoutes();
//        return "Route data processed";
//    }
//
//    // Endpoint for fetching and processing summary data
//    @PostMapping("/reports/summary")
//    public String processSummary() {
//        summaryService.fetchSummary();
//        return "Summary data processed";
//    }

    // Endpoint for fetching all summary data
    @GetMapping("/reports/summary")
    public List<SummaryEntity> getAllSummary() {
        return summaryService.getAllSummaryEntities();
    }


    @GetMapping("/fetch-devices")
    public String fetchAndStoreDevices() {
        deviceService.fetchAndStoreDevices(authorization);
        return "Devices fetched and stored successfully!";
    }

    @GetMapping("/devices")
    public List<Device> getDevices() {
        return deviceService.getDevices();
    }



    @GetMapping("/positions")
    public List<Map<String, Object>> getPositions() {
        return positionService.getLatestPositions();
    }
    @GetMapping("/positions/{deviceId}")
    public List<Map<String, Object>> getPositionsByDeviceId(@PathVariable Long deviceId) {
        return positionService.fetchPositionsByDevice(deviceId);
    }
    @GetMapping("/positions/summary")
    public List<Map<String, Object>> getEntriesCountByDeviceId() {
        return positionService.getEntriesCountByDeviceId();
    }
    @GetMapping("/beacons")
    public List<Beacon> getBeacons() {
        return beaconService.getBeacons();
    }

}
