package com.alash.eventease.repository;

import com.alash.eventease.model.User;
import com.alash.eventease.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<UserRole, Long> {
    Optional<UserRole> findByName(String name);

    List<UserRole> findByUsers(User user);
}
