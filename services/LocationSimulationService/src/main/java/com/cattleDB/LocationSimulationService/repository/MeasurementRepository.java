package com.cattleDB.LocationSimulationService.repository;

import com.cattleDB.LocationSimulationService.models.Measurement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeasurementRepository extends JpaRepository<Measurement, Long> {
}
