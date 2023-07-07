package com.alash.eventease.service.impl;

import com.alash.eventease.dto.request.EventDto;
import com.alash.eventease.model.Event;
import com.alash.eventease.repository.EventRepository;
import com.alash.eventease.service.EventService;
import org.springframework.stereotype.Service;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    public EventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public void saveEvent(EventDto event) {
        Event newEvent = Event.builder()
                .eventName(event.getEventName())
                .description(event.getDescription())
                .location(event.getLocation())
                .build();


        eventRepository.save(newEvent);
    }
}
