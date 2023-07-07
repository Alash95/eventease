package com.alash.eventease.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ApiResponse {
    private boolean isSuccessful;
    private Object data;
}
