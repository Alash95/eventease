package com.alash.eventease.payment.service;

import com.alash.eventease.dto.response.CustomResponse;
import org.springframework.http.ResponseEntity;

public interface IPaymentService {
    ResponseEntity<CustomResponse> initiatePayment(String orderReference);

    ResponseEntity<CustomResponse> verifyPayment(String orderReference);
}
