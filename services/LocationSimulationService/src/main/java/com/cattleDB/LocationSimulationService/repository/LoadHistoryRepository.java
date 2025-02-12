package com.cattleDB.LocationSimulationService.repository;

import com.cattleDB.LocationSimulationService.models.LoadHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoadHistoryRepository extends JpaRepository<LoadHistory, Long> {
}
