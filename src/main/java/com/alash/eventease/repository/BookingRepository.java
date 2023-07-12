package com.alash.eventease.repository;

import com.alash.eventease.model.domain.Booking;
import com.alash.eventease.model.domain.Event;
import com.alash.eventease.model.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUser(UserEntity user);
    List<Booking> findByEvent(Event event);
    List<Booking> findByBookingDateBetween(LocalDate startDate, LocalDate endDate);

    // Example: Find all bookings for a specific user
    List<Booking> findAllByUser(UserEntity user);

    // Example: Find all bookings for a specific event
    List<Booking> findAllByEvent(Event event);

    // Example: Find all bookings by user and event
    List<Booking> findAllByUserAndEvent(UserEntity user, Event event);

    // Example: Find the latest bookings (sorted by booking date in descending order)
    List<Booking> findTop10ByOrderByBookingDateDesc();

    // Example: Find the total count of bookings for a specific user
    Long countByUser(UserEntity user);


    // Example: Delete all bookings for a specific event
    void deleteByEvent(Event event);

    // Example: Check if a specific user has any bookings
    boolean existsByUser(UserEntity user);

    // Example: Check if a booking exists for a specific user and event
    boolean existsByUserAndEvent(UserEntity user, Event event);

    // Additional methods can be added as needed for your use case
}


