package com.alash.eventease.service.impl;

import com.alash.eventease.dto.request.CreateEventRequest;
import com.alash.eventease.dto.response.EventResponseDto;
import com.alash.eventease.dto.response.UserResponseDto;
import com.alash.eventease.dto.response.CustomResponse;
import com.alash.eventease.exception.UserNotFoundException;
import com.alash.eventease.model.domain.Booking;
import com.alash.eventease.model.domain.Event;
import com.alash.eventease.model.domain.UserEntity;
import com.alash.eventease.repository.BookingRepository;
import com.alash.eventease.repository.EventRepository;
import com.alash.eventease.repository.UserRepository;
import com.alash.eventease.service.EventService;
import com.alash.eventease.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.awt.print.Book;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;


    @Override
    public ResponseEntity<CustomResponse> addEvent(CreateEventRequest request) {
        UserEntity foundUser = userRepository.findByEmail(request.getEmail()).get();
        if (!userRepository.existsByEmail(request.getEmail())){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, ResponseUtils.USER_NOT_FOUND_MESSAGE));

        }
        Event event = Event.builder()
                .eventName(request.getEventName())
                .description(request.getDescription())
                .location(request.getEventLocation())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();
        eventRepository.save(event);

        CustomResponse customResponse = new CustomResponse(HttpStatus.OK, "Event created successfully");
        customResponse.setData(event);
        return ResponseEntity.ok().body(customResponse);

    }

    @Override
    public ResponseEntity<CustomResponse> fetchAllEvents() {
        List<Event> events = eventRepository.findAll();
        List<EventResponseDto> eventResponseList = events.stream()
                .map(this::mapToUserResponse).collect(Collectors.toList());

        CustomResponse successResponse = CustomResponse.builder()
                .status(HttpStatus.OK.name())
                .message("Successful")
                .data(eventResponseList.isEmpty() ? null : eventResponseList)
                .build();

        CustomResponse customResponse = new CustomResponse(HttpStatus.OK, "Successful");
        customResponse.setData(successResponse);
        return ResponseEntity.ok().body(customResponse);

    }

    @Override
    public ResponseEntity<CustomResponse> getRegisteredEvents(Long userId) {
        Optional<UserEntity> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            // Handle the case when the user does not exist
            return ResponseEntity
                    .badRequest()
                    .body(new CustomResponse(HttpStatus.BAD_REQUEST, "No user found"));
        }

        // Retrieve the event registrations for the user
        List<Booking> bookings = bookingRepository.findByUserId(userId);

        // Retrieve the event details for each registration
        List<Event> registeredEvents = new ArrayList<>();
        for (Booking registration : bookings) {
            Optional<Event> event = eventRepository.findById(registration.getId());
            if (event.isPresent()) {
                registeredEvents.add(event.get());
            }
        }

        if (registeredEvents.isEmpty()) {
            // Handle the case when the user has no registered events
            return ResponseEntity
                    .ok()
                    .body(new CustomResponse(HttpStatus.NOT_FOUND, "No registered events found"));
        }

        CustomResponse customResponse = new CustomResponse(HttpStatus.OK, "Registered events found");
        customResponse.setData(registeredEvents);

        return ResponseEntity.ok().body(customResponse);
    }





    private EventResponseDto mapToUserResponse(Event event) {
        return EventResponseDto.builder()
                .id(event.getId())
                .eventName(event.getEventName())
                .location(event.getLocation())
                .description(event.getDescription())
                .price(event.getPrice())
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .build();
    }
}
