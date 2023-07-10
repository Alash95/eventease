package com.alash.eventease.dto.response;

import com.alash.eventease.dto.request.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@lombok.Data
public class CreatePlanResponse {

    private Boolean status;
    private String message;
    private Data data;


}

