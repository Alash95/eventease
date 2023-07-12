package com.alash.eventease.service;

import com.alash.eventease.dto.request.CreateEventRequest;
import com.alash.eventease.dto.response.CustomResponse;
import org.springframework.http.ResponseEntity;

public interface EventService {
//    void saveEvent(CreateEventRequest event);
    ResponseEntity<CustomResponse> addEvent(CreateEventRequest request);
    ResponseEntity<CustomResponse> fetchAllEvents();

}
