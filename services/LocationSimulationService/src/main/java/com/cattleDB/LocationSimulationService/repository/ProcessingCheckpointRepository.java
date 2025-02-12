package com.cattleDB.LocationSimulationService.repository;


import com.cattleDB.LocationSimulationService.models.ProcessingCheckpoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;



@Repository
public interface ProcessingCheckpointRepository extends JpaRepository<ProcessingCheckpoint, Long> {

    @Query("SELECT c.lastProcessedId FROM ProcessingCheckpoint c ORDER BY c.id DESC LIMIT 1")
    Long findLastProcessedId();

    @Modifying
    @Query("UPDATE ProcessingCheckpoint c SET c.lastProcessedId = :lastProcessedId WHERE c.id = (SELECT MAX(id) FROM ProcessingCheckpoint)")
    void updateLastProcessedId(@Param("lastProcessedId") Long lastProcessedId);
}