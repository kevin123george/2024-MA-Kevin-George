package com.hatake.cattleDB.repository;

import com.hatake.cattleDB.models.Position;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PositionRepository extends JpaRepository<Position, Long> {

    List<Position> findByDeviceId(Long deviceId);
}
