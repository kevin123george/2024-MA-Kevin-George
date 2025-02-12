package com.cattleDB.WatcherPatternService.repository;

import com.cattleDB.WatcherPatternService.models.AnalysisType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnalysisTypeRepository extends JpaRepository<AnalysisType, Long> {
    Optional<AnalysisType> findByName(String name);
}
