package com.hatake.cattleDB.controller;

import com.hatake.cattleDB.dtos.EventResponse;
import com.hatake.cattleDB.models.SummaryEntity;
import com.hatake.cattleDB.service.DeviceService;
import com.hatake.cattleDB.service.EventService;
import com.hatake.cattleDB.service.RouteService;
import com.hatake.cattleDB.service.SummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin("http://localhost:3000/")
public class DataController {

    @Autowired
    private EventService eventService;

    @Autowired
    private RouteService routeService;

    @Autowired
    private SummaryService summaryService;
    private final DeviceService deviceService;


    public DataController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    // Endpoint for fetching events
    @GetMapping("/events/fetch")
    public List<EventResponse> fetchEvents() {
        return eventService.fetchEvents();
    }

    // Endpoint for fetching routes and processing them
    @PostMapping("/reports/route")
    public String processRoutes() {
        routeService.fetchRoutes();
        return "Route data processed";
    }

    // Endpoint for fetching and processing summary data
    @PostMapping("/reports/summary")
    public String processSummary() {
        summaryService.fetchSummary();
        return "Summary data processed";
    }

    // Endpoint for fetching all summary data
    @GetMapping("/reports/summary")
    public List<SummaryEntity> getAllSummary() {
        return summaryService.getAllSummaryEntities();
    }


    @GetMapping("/fetch-devices")
    public String fetchAndStoreDevices() {
        deviceService.fetchAndStoreDevices();
        return "Devices fetched and stored successfully!";
    }
}
