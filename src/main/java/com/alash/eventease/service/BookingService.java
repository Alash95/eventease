package com.alash.eventease.service;

import com.alash.eventease.dto.request.BookingRequestDto;
import com.alash.eventease.dto.response.CustomResponse;
import com.alash.eventease.model.domain.Booking;
import org.springframework.http.ResponseEntity;

public interface BookingService {
    ResponseEntity<CustomResponse> createBooking(BookingRequestDto booking);

}
