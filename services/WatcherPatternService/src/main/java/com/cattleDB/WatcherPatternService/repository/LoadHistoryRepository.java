package com.cattleDB.WatcherPatternService.repository;

import com.cattleDB.WatcherPatternService.models.LoadHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoadHistoryRepository extends JpaRepository<LoadHistory, Long> {
}
