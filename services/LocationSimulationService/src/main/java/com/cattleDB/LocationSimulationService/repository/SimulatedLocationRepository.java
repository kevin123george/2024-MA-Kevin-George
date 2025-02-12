package com.cattleDB.LocationSimulationService.repository;

import com.cattleDB.LocationSimulationService.models.SimulatedLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface SimulatedLocationRepository extends JpaRepository<SimulatedLocation, Long> {
    @Query("SELECT DISTINCT s.cattleID FROM SimulatedLocation s")
    List<String> findDistinctCattleIds();


    // Get all locations ordered by startTime ascending
    List<SimulatedLocation> findAllByOrderByStartTimeAsc();

    // Filter by startTime range and order by startTime ascending
    List<SimulatedLocation> findByStartTimeBetweenOrderByStartTimeAsc(ZonedDateTime startTimeFrom, ZonedDateTime startTimeTo);

    // Filter by cattle IDs and order by startTime ascending
    List<SimulatedLocation> findByCattleIDInOrderByStartTimeAsc(List<Long> cattleIDs);

    // Filter by startTime range and cattle IDs, ordered by startTime ascending
    List<SimulatedLocation> findByStartTimeBetweenAndCattleIDInOrderByStartTimeAsc(ZonedDateTime startTimeFrom, ZonedDateTime startTimeTo, List<Long> cattleIDs);

    List<SimulatedLocation> findByStartTimeBetween(ZonedDateTime start, ZonedDateTime end);


}