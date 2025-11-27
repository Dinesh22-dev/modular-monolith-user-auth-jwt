package com.example.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.auth.entity.Otp;

public interface OtpRepostiory extends JpaRepository<Otp, Integer> {
    Optional<Otp> findByTransactionsAndUsedFalse(String id);

    Optional<Otp> findByTransactions(String id);
}
