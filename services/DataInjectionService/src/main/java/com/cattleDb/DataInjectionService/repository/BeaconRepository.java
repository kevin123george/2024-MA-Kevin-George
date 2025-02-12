package com.cattleDb.DataInjectionService.repository;

import com.cattleDb.DataInjectionService.models.BeaconEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BeaconRepository extends JpaRepository<BeaconEntity, Long> {
}