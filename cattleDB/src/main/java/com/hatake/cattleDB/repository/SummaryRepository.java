package com.hatake.cattleDB.repository;

import com.hatake.cattleDB.models.SummaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SummaryRepository extends JpaRepository<SummaryEntity, Long> {
}
