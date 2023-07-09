package com.alash.eventease.repository;

import com.alash.eventease.model.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}
