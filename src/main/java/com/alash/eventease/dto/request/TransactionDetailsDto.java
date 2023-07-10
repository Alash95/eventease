package com.alash.eventease.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionDetailsDto {
    private String amount;
    private String recipient;
    private String description;

    // Add getters and setters
}

