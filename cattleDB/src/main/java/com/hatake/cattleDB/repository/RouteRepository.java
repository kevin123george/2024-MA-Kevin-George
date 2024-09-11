package com.hatake.cattleDB.repository;

import com.hatake.cattleDB.models.RouteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RouteRepository extends JpaRepository<RouteEntity, Long> {
    // Count valid routes
    @Query("SELECT COUNT(r) FROM RouteEntity r WHERE r.valid = true")
    long countValidRoutes();

    // Count invalid routes
    @Query("SELECT COUNT(r) FROM RouteEntity r WHERE r.valid = false")
    long countInvalidRoutes();

    // Average RSSI
    @Query("SELECT AVG(r.rssi) FROM RouteEntity r WHERE r.rssi IS NOT NULL")
    Double findAverageRssi();

    // Find the latest routes by fix time
    List<RouteEntity> findTop5ByOrderByFixTimeDesc();
}
