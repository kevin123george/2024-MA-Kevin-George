package com.cattleDb.DataInjectionService.repository;

import com.cattleDb.DataInjectionService.models.LoadHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoadHistoryRepository extends JpaRepository<LoadHistory, Long> {
}
