package com.cattleDB.LocationSimulationService.controller;

import com.cattleDB.LocationSimulationService.Service.SimulationService;
import com.cattleDB.LocationSimulationService.dto.TestCaseResult;
import com.cattleDB.LocationSimulationService.models.LoadHistory;
import com.cattleDB.LocationSimulationService.models.SimulatedLocation;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mysql")
@CrossOrigin("*")
public class SimulationControllerV2 {

    private final SimulationService simulationService;

    public SimulationControllerV2(SimulationService simulationService) {
        this.simulationService = simulationService;
    }

    @GetMapping("/tables")
    public List<Map<String, Object>> getAllTables() {
        return simulationService.getAllTables();
    }

    @GetMapping("/tables/{tableName}/details")
    public Map<String, Object> getTableDetails(@PathVariable String tableName) {
        return simulationService.getTableDetails(tableName);
    }

    @GetMapping("/cattle-positions")
    public List<SimulatedLocation> simulateCattlePositions(
            @RequestParam(value = "startTimeFrom", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime startTimeFrom,
            @RequestParam(value = "startTimeTo", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime startTimeTo,
            @RequestParam(value = "cattleID", required = false) List<Long> cattleIDs) {
        return simulationService.simulateCattlePositions(startTimeFrom, startTimeTo, cattleIDs);
    }

    @GetMapping("/cattle-ids")
    public List<String> getDistinctCattleIDs() {
        return simulationService.getDistinctCattleIDs();
    }

    @GetMapping("/sync-simulated-location")
    public String syncSimulatedLocation() {
        return simulationService.syncSimulatedLocation();
    }

    @GetMapping("/tables/init-measure")
    public List<Map<String, Object>> initializeMeasurements(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate) {
        return simulationService.initializeMeasurements(startDate, endDate);
    }


    @GetMapping("/tables/load-history")
    public List<LoadHistory> getLoadHistory() {
        return simulationService.loadHistroy();
    }


    @GetMapping("/sanitize")
    public String sanitizeLoadHistory() {
         simulationService.sanitizeAndSavePositions();
         return "ss";
    }

    @GetMapping("/cattle-positions-asc")
    public List<SimulatedLocation> simulateCattlePositionsAsc(){

        return simulationService.simulateCattlePositionsAsc();

    }


    @GetMapping("/simulation-eval")
    public TestCaseResult simulationEval() {
        return simulationService.calculateTestCase(simulationService.getAllSimulatedLocations());
    }

    @GetMapping("/analysis-eval")
    public Map<String, Object> evaluateAnalysis() {
        Map<String, Object> evaluationResults = simulationService.evaluateSimulatedDataAgainstSanitizedData(0.1, 5);

        return evaluationResults;
    }

}

