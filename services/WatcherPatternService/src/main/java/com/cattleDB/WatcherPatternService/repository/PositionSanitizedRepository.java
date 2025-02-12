package com.cattleDB.WatcherPatternService.repository;

import com.cattleDB.WatcherPatternService.models.Position;
import com.cattleDB.WatcherPatternService.models.PositionSanitized;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

public interface PositionSanitizedRepository extends JpaRepository<PositionSanitized, Long> {

    @Query("SELECT p FROM PositionSanitized p WHERE p.deviceTime > :startDate AND p.deviceTime < :endDate")
    List<PositionSanitized> findPositionsByDeviceTimeBetween(@Param("startDate") OffsetDateTime startDate,
                                                             @Param("endDate") OffsetDateTime endDate);


    @Query("SELECT p FROM PositionSanitized p WHERE p.deviceTime >= :startTime")
    List<PositionSanitized> findRecentPositions(@Param("startTime") OffsetDateTime startTime);





}
