package com.hatake.cattleDB.controller;

import com.hatake.cattleDB.models.RouteEntity;
import com.hatake.cattleDB.repository.RouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@CrossOrigin("http://localhost:5173/")

@RestController
public class RouteAnalyticsController {

    @Autowired
    private RouteRepository routeRepository;

    @GetMapping("/api/analytics/routes")
    public ResponseEntity<?> getRouteAnalytics() {
        long totalRoutes = routeRepository.count();
        long validRoutes = routeRepository.countValidRoutes();
        long invalidRoutes = routeRepository.countInvalidRoutes();
        Double averageRssi = routeRepository.findAverageRssi();
        List<RouteEntity> latestRoutes = routeRepository.findTop5ByOrderByFixTimeDesc();

        Map<String, Object> result = new HashMap<>();
        result.put("totalRoutes", totalRoutes);
        result.put("validRoutes", validRoutes);
        result.put("invalidRoutes", invalidRoutes);
        result.put("averageRssi", averageRssi != null ? averageRssi : 0);
        result.put("latestRoutes", latestRoutes);

        return ResponseEntity.ok(result);
    }
}
