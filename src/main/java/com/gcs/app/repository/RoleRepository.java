package com.gcs.app.repository;

import com.gcs.app.model.Role;
import com.gcs.app.model.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleType(RoleType roleType);
}