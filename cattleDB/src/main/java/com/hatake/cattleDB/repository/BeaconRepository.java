package com.hatake.cattleDB.repository;

import com.hatake.cattleDB.models.Beacon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BeaconRepository extends JpaRepository<Beacon, Long> {
}