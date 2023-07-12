package com.alash.eventease.service.impl;

import com.alash.eventease.dto.request.CreateEventRequest;
import com.alash.eventease.dto.response.EventResponseDto;
import com.alash.eventease.dto.response.UserResponseDto;
import com.alash.eventease.dto.response.CustomResponse;
import com.alash.eventease.model.domain.Event;
import com.alash.eventease.model.domain.UserEntity;
import com.alash.eventease.repository.EventRepository;
import com.alash.eventease.repository.UserRepository;
import com.alash.eventease.service.EventService;
import com.alash.eventease.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

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

        return ResponseEntity.ok(successResponse);
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
