package com.alash.eventease.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RegisterUserRequest {
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String password;
}
