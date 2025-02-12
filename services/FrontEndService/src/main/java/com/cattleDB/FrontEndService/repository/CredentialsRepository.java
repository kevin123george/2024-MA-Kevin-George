package com.cattleDB.FrontEndService.repository;

import com.cattleDB.FrontEndService.models.Credentials;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CredentialsRepository extends JpaRepository<Credentials, Long> {
}
