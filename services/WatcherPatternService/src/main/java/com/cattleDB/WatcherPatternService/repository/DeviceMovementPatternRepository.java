package com.cattleDB.WatcherPatternService.repository;

import com.cattleDB.WatcherPatternService.models.DeviceMovementPattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DeviceMovementPatternRepository extends JpaRepository<DeviceMovementPattern, Long> {
    List<DeviceMovementPattern> findByDeviceId(Long deviceId);



    @Query(value = "SELECT * " +
            "FROM device_movement_pattern ORDER BY id DESC LIMIT :limit", nativeQuery = true)
    List<DeviceMovementPattern> findLatestMovementsProjection(@Param("limit") int limit);

}
