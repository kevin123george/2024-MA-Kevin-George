package com.hatake.cattleDB.repository;

import com.hatake.cattleDB.models.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<EventEntity, Long> {
}
