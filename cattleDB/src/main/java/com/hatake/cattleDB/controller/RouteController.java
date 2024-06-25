package com.hatake.cattleDB.controller;

import com.hatake.cattleDB.service.RouteService;
import com.hatake.cattleDB.service.SummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class RouteController {

    @Autowired
    private RouteService routeService;

    @Autowired
    private SummaryService summaryService;

    @PostMapping("/route")
    public String getRoutes(
    ) {

        routeService.fetchRoutes();
        // Implement your logic here
        return "Route data processed";
    }

    @PostMapping("/summary")
    public String getSummary(
    ) {

        summaryService.fetchSummary();
        // Implement your logic here
        return "Route data processed";
    }
}