package com.alash.eventease.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EventDto {
    private String email;
    private String eventName;
    private String description;
    private String location;
}
