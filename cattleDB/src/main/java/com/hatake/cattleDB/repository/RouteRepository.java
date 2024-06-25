package com.hatake.cattleDB.repository;

import com.hatake.cattleDB.models.RouteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RouteRepository extends JpaRepository<RouteEntity, Long> {
}
