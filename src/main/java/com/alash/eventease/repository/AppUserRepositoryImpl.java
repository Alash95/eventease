package com.alash.eventease.repository;

import com.alash.eventease.model.domain.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUserRepositoryImpl extends JpaRepository<AppUser, Long> {
}

