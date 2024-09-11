package com.namikaze.hatake.repository;

import com.namikaze.hatake.models.CsvEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CsvRepository extends JpaRepository<CsvEntity, Long> {
}