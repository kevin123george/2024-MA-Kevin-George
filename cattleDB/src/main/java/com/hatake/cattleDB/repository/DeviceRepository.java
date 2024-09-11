package com.hatake.cattleDB.repository;

import com.hatake.cattleDB.models.Device;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceRepository extends JpaRepository<Device, Long> {
}