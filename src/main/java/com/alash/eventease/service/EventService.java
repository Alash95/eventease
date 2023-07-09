package com.alash.eventease.service;

import com.alash.eventease.dto.CreateEventRequest;
import com.alash.eventease.dto.CustomResponse;
import org.springframework.http.ResponseEntity;

public interface EventService {
//    void saveEvent(CreateEventRequest event);
    ResponseEntity<CustomResponse> addEvent(CreateEventRequest request);

}
