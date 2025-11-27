package com.example.auth.entity;

import java.time.Instant;
import java.util.UUID;

import com.example.user.entity.User;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Otp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 36)
    private String transactions = UUID.randomUUID().toString(); // transactionId

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // d;

    @Column(length = 100)
    private String channel; // "email", "sms"

    @Column(length = 255, nullable = false)
    private String otpHash;

    @Column(length = 255, nullable = false)
    private String otp;

    private Instant expiresAt;

    private Integer attempts = 0;

    private boolean used = false;
}
