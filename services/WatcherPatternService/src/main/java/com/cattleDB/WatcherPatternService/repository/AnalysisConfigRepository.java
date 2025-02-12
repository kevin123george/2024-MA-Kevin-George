package com.cattleDB.WatcherPatternService.repository;

import com.cattleDB.WatcherPatternService.models.AnalysisConfig;
import com.cattleDB.WatcherPatternService.models.AnalysisType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnalysisConfigRepository extends JpaRepository<AnalysisConfig, Long> {
    Optional<AnalysisConfig> findByKey(String key);

    Optional<AnalysisConfig> findByKeyAndAnalysisType(String key, AnalysisType analysisType);

}
