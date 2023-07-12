package com.alash.eventease.payment.repository;


import com.alash.eventease.model.domain.Booking;
import com.alash.eventease.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.print.Book;
import java.util.Optional;

public interface IPaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrder(Booking order);
}
