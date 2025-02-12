package com.cattleDB.WatcherPatternService.repository;


import com.cattleDB.WatcherPatternService.models.DeviceAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceAnalysisRepository extends JpaRepository<DeviceAnalysis, Long> {
}