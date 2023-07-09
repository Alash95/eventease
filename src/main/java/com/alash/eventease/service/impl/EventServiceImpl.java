package com.alash.eventease.service.impl;

import com.alash.eventease.dto.CreateEventRequest;
import com.alash.eventease.dto.CustomResponse;
import com.alash.eventease.model.Event;
import com.alash.eventease.model.UserEntity;
import com.alash.eventease.repository.EventRepository;
import com.alash.eventease.repository.UserRepository;
import com.alash.eventease.service.EventService;
import com.alash.eventease.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;


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


        return ResponseEntity.ok().body(new CustomResponse(HttpStatus.CREATED, "Event created successfully"));
    }
}
