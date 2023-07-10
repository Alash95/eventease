package com.alash.eventease.service;

import com.alash.eventease.dto.request.CreatePlanDto;
import com.alash.eventease.dto.request.InitializePaymentDto;
import com.alash.eventease.dto.response.CreatePlanResponse;
import com.alash.eventease.dto.response.InitializePaymentResponse;
import com.alash.eventease.dto.response.PaymentVerificationResponse;
import org.springframework.stereotype.Service;

public interface PaystackService {
    CreatePlanResponse createPlan(CreatePlanDto createPlanDto) throws Exception;
    InitializePaymentResponse initializePayment(InitializePaymentDto initializePaymentDto);
    PaymentVerificationResponse paymentVerification(String reference, String plan, Long id) throws Exception;
}

