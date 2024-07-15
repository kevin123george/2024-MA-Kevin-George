package com.hatake.cattleDB.controller;

import com.hatake.cattleDB.models.SummaryEntity;
import com.hatake.cattleDB.service.RouteService;
import com.hatake.cattleDB.service.SummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin("http://localhost:3000/")
public class RouteController {

    @Autowired
    private RouteService routeService;

    @Autowired
    private SummaryService summaryService;

    @PostMapping("/route")
    public String getRoutes(
    ) {

        routeService.fetchRoutes();
        return "Route data processed";
    }

    @PostMapping("/summary")
    public String getSummary(
    ) {

        summaryService.fetchSummary();
        return "Route data processed";
    }

    @GetMapping("/summary")
    public List<SummaryEntity> getAllSummary(){
        return summaryService.getAllSummaryEntities();
    }
}