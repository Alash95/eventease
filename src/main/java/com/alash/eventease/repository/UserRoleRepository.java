package com.alash.eventease.repository;

import com.alash.eventease.model.domain.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    boolean existsByName(String name);

    UserRole findByName(String roleUser);
}
