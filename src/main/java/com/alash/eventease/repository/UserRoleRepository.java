package com.alash.eventease.repository;

import com.alash.eventease.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    boolean existsByName(String name);

    UserRole findByName(String roleUser);
}
