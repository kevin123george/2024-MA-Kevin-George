package com.cattleDB.WatcherPatternService.repository;

import com.cattleDB.WatcherPatternService.models.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
    // You can add custom queries here if needed


    List<Alert> findAllByOrderByIdDesc();

}
