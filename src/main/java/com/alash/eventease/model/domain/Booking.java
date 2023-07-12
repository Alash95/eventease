package com.alash.eventease.model.domain;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "booking")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String status;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
    private double totalAmount;
    private String reference;
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    private String bookingDate;

    private Integer numberOfTickets;

    private String eventName;

    private String firstName;

    private String lastName;


}

