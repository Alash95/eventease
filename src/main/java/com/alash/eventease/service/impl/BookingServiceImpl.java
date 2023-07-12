package com.alash.eventease.service.impl;

import com.alash.eventease.dto.request.BookingRequestDto;
import com.alash.eventease.dto.response.CustomResponse;
import com.alash.eventease.model.domain.Booking;
import com.alash.eventease.model.domain.Event;
import com.alash.eventease.model.domain.UserEntity;
import com.alash.eventease.repository.BookingRepository;
import com.alash.eventease.repository.EventRepository;
import com.alash.eventease.repository.UserRepository;
import com.alash.eventease.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.sql.internal.ParameterRecognizerImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;




    @Override
    public ResponseEntity<CustomResponse> createBooking(BookingRequestDto bookingRequest) {
        if (bookingRequest.getUserId() == null) {
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "UserId is required"));

        }
        if (bookingRequest.getEventId() == null) {
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "EventId is required"));

        }
        if (bookingRequest.getNumberOfTickets() <= 0) {
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "Number of tickets must be greater than zero"));
        }

        Optional<UserEntity> userOpt = userRepository.findById(bookingRequest.getUserId());
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "User not found for this Id"));
        }
        Optional<Event> eventOpt = eventRepository.findById(bookingRequest.getEventId());
        if (eventOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "Event not found for this Id"));
        }
        Booking bookEvent = Booking.builder()
                .user(userOpt.get())
                .event(eventOpt.get())
                .firstName(userOpt.get().getFirstName())
                .lastName(userOpt.get().getLastName())
                .eventName(eventOpt.get().getEventName())
                .bookingDate(bookingRequest.getBookingDate())
                .numberOfTickets(bookingRequest.getNumberOfTickets())
                .build();
        bookingRepository.save(bookEvent);

        return ResponseEntity.ok().body(new CustomResponse(HttpStatus.CREATED, "Event booked successfully"));
    }
}
