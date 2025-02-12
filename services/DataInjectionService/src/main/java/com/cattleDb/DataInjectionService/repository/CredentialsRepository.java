package com.cattleDb.DataInjectionService.repository;

import com.cattleDb.DataInjectionService.models.Credentials;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CredentialsRepository extends JpaRepository<Credentials, Long> {
}
