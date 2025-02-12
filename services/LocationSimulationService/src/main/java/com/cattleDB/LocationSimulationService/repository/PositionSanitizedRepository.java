package com.cattleDB.LocationSimulationService.repository;

import com.cattleDB.LocationSimulationService.models.Position;
import com.cattleDB.LocationSimulationService.models.PositionSanitized;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.List;

public interface PositionSanitizedRepository extends JpaRepository<PositionSanitized, Long> {

    @Query("SELECT p FROM PositionSanitized p WHERE p.deviceTime > :startDate AND p.deviceTime < :endDate")
    List<PositionSanitized> findPositionsByDeviceTimeBetween(@Param("startDate") OffsetDateTime startDate,
                                                             @Param("endDate") OffsetDateTime endDate);



    List<PositionSanitized> findByDeviceTimeBetween(OffsetDateTime start, OffsetDateTime end);


    List<PositionSanitized> findAllByDeviceIdAndDeviceTimeBetween(
            Long deviceId, OffsetDateTime start, OffsetDateTime end
    );


    @Query(value = "SELECT * FROM position_sanitized LIMIT :limit", nativeQuery = true)
    List<PositionSanitized> findTopPositions(@Param("limit") int limit);
}
