package com.alash.eventease.service;

import com.alash.eventease.dto.request.CreateEventRequest;
import com.alash.eventease.dto.response.CustomResponse;
import com.alash.eventease.model.domain.Event;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface EventService {
//    void saveEvent(CreateEventRequest event);
    ResponseEntity<CustomResponse> addEvent(CreateEventRequest request);
    ResponseEntity<CustomResponse> fetchAllEvents();
    ResponseEntity<CustomResponse> getRegisteredEvents(Long userId);

}
