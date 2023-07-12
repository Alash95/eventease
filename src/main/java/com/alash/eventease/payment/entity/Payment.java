package com.alash.eventease.payment.entity;

import com.alash.eventease.model.domain.Booking;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.print.Book;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double amount;
    private String status;
    private String paymentChannel;
    private String paymentReference;
    private String paymentAccessCode;
    @OneToOne
    @JoinColumn(name = "booking_id")
    private Booking order;
    private String transactionDate;
}


