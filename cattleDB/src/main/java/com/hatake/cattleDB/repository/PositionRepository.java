package com.hatake.cattleDB.repository;

import com.hatake.cattleDB.models.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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



@Query("SELECT p.deviceId as deviceId, COUNT(p.id) as entryCount FROM Position p GROUP BY p.deviceId")
    List<Map<String, Object>> countEntriesByDeviceId();
}
