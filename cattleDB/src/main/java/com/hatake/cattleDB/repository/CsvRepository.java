package com.hatake.cattleDB.repository;

import com.hatake.cattleDB.models.CsvEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CsvRepository extends JpaRepository<CsvEntity, Long> {
}