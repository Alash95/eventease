package com.alash.eventease.dto.request;

import com.alash.eventease.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.Set;

@lombok.Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Data {
    private String email;
    private String fullName;
    private String eventName;
    private String eventLocation;
    private Set<UserRole> role;
}
