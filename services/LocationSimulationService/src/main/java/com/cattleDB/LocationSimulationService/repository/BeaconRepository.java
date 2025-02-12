package com.cattleDB.LocationSimulationService.repository;

import com.cattleDB.LocationSimulationService.models.BeaconEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BeaconRepository extends JpaRepository<BeaconEntity, Long> {

    Optional<BeaconEntity> findByName(String name);

    // If you want to find multiple names
    List<BeaconEntity> findByNameIn(List<String> names);
}