package com.cattleDb.DataInjectionService.repository;

import com.cattleDb.DataInjectionService.models.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

public interface PositionRepository extends JpaRepository<Position, Long> {

    //    List<Position> findByDeviceId(Long deviceId);
    @Query(value = """
            SELECT p.id, p.protocol, p.device_time AS deviceTime, p.latitude, p.longitude, 
                   p.device_name AS deviceName, p.device_id AS deviceId
            FROM Position p 
            WHERE p.device_id = :deviceId       
            """, nativeQuery = true)
    List<Map<String, Object>> findPositionsByDeviceId(@Param("deviceId") Long deviceId);


    @Query(value = """
            SELECT DISTINCT ON (p.device_id) 
            p.id, p.protocol, p.device_time AS deviceTime, 
            p.latitude, p.longitude, 
            p.device_name AS deviceName, p.device_id AS deviceId
            FROM Position p
            WHERE valid = true
            ORDER BY p.device_id, p.device_time DESC;
            """, nativeQuery = true)
    List<Map<String, Object>> getLatestPositions();


    @Query("SELECT p.deviceId as deviceId, COUNT(p.id) as entryCount FROM Position p GROUP BY p.deviceId")
    List<Map<String, Object>> countEntriesByDeviceId();


    Position findTopByDeviceIdOrderByDeviceTimeDesc(Long deviceId);


    // Custom query to fetch positions from the last two weeks
    @Query("SELECT p FROM Position p WHERE p.deviceTime >= :startTime")
    List<Position> findRecentPositions(OffsetDateTime startTime);


    @Query(value = """
            SELECT DISTINCT ON (p.device_id) p.id, p.protocol, p.device_time AS deviceTime, 
                   p.latitude, p.longitude, p.device_name AS deviceName, p.device_id AS deviceId
            FROM Position p
            ORDER BY p.device_id, p.id DESC
            """, nativeQuery = true)
    List<Map<String, Object>> findLatestPositionsForAllDevices();


    @Query("SELECT p FROM Position p WHERE p.deviceTime BETWEEN :startDate AND :endDate")
    List<Position> findPositionsByDeviceTimeBetween(@Param("startDate") OffsetDateTime startDate,
                                                    @Param("endDate") OffsetDateTime endDate);

}
