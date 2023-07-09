package com.alash.eventease.repository;

import com.alash.eventease.model.domain.PaymentPaystack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaystackPaymentRepositoryImpl extends JpaRepository<PaymentPaystack, Long> {
}

