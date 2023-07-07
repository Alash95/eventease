package com.alash.eventease.dto.response;

import com.alash.eventease.dto.request.Data;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@lombok.Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Response {
    private String responseCode;
    private String responseMessage;
    private Data data;

}
