package com.cattleDB.LocationSimulationService.repository;

import com.cattleDB.LocationSimulationService.models.LearningMeasurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface LearningMeasurementRepository extends JpaRepository<LearningMeasurement,Long> {




    @Transactional
    @Modifying
    @Query(value = "INSERT INTO learning_measurement (wifi_ap, cnx_time, client_id) " +
            "SELECT SUBSTRING(beacon, 1, 20), time, device FROM measurement", nativeQuery = true)
    void insertFromMeasurement();
}
