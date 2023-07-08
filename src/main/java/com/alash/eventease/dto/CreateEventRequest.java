package com.alash.eventease.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateEventRequest {
    private String email;
    private String eventName;
    private String eventLocation;
    private String description;
    private LocalDate startDate;
    @UpdateTimestamp
    private LocalDate endDate;
}
