package com.cattleDB.FrontEndService.repository;

import com.cattleDB.FrontEndService.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepositroy extends JpaRepository<Role, Integer> {
    Optional<Role> findById(Integer id);

}
