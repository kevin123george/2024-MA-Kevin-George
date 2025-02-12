package com.cattleDB.WatcherPatternService.Controller;

import com.cattleDB.WatcherPatternService.models.Alert;
import com.cattleDB.WatcherPatternService.models.DeviceMovementPattern;
import com.cattleDB.WatcherPatternService.repository.AlertRepository;
import com.cattleDB.WatcherPatternService.repository.DeviceMovementPatternRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alerts")
@CrossOrigin("*")
public class AlertsController {

    private final AlertRepository alertRepository;
    private final DeviceMovementPatternRepository deviceMovementPatternRepository;

    public AlertsController(AlertRepository alertRepository, DeviceMovementPatternRepository deviceMovementPatternRepository) {
        this.alertRepository = alertRepository;
        this.deviceMovementPatternRepository = deviceMovementPatternRepository;
    }
    @GetMapping
    List<Alert> getAlerts() {
        return alertRepository.findAllByOrderByIdDesc();
    }


    @GetMapping(value = "/movement-pattern")
    public ResponseEntity<List<DeviceMovementPattern>> getLatestMovements(
            @RequestParam(defaultValue = "10") int limit) {
        List<DeviceMovementPattern> movements = deviceMovementPatternRepository.findLatestMovementsProjection(limit);
        return ResponseEntity.ok(movements);
    }

}
