package com.cattleDB.LocationSimulationService.repository;

import com.cattleDB.LocationSimulationService.models.ProcessedPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeduplicationRepository extends JpaRepository<ProcessedPosition, String> {
    boolean existsByUniqueKey(String uniqueKey);
}
