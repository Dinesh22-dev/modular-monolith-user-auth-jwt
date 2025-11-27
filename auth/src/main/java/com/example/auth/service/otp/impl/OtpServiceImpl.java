package com.example.auth.service.otp.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.example.auth.entity.Otp;
import com.example.auth.repository.OtpRepostiory;
import com.example.auth.service.otp.OtpService;
import com.example.user.entity.User;
import com.example.user.repository.UserRepository;
import com.example.user.util.PasswordUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final OtpRepostiory otpRepo;
    private final UserRepository userRepository;
    private final PasswordUtil passwordUtil;

    private static final Duration OTP_TTL = Duration.ofMinutes(5);
    private static final int MAX_ATTEMPTS = 5;

    @Override
    public String startChallenge(String userEmail, String channel, String sendTo) {
        String otp = generateOtp(6); // e.g., "123456"
        String hash = hash(otp);
        User user = userRepository.findByEmailIgnoreCase(userEmail).get();

        Otp c = new Otp();
        c.setUserId(user);
        c.setChannel(channel);
        c.setOtpHash(hash);
        c.setOtp(otp);
        c.setExpiresAt(Instant.now().plus(OTP_TTL));

        otpRepo.save(c);

        System.out.println("[OTP] to " + sendTo + " = " + otp + " (tx=" + c.getId() + ")");

        return c.getTransactions();
    }

    @Override
    public boolean verify(String transactionId, String otp) {
        var otps = otpRepo.findByTransactionsAndUsedFalse(transactionId)
                .orElse(null);
        if (otps == null)
            return false;

        if (otps.getExpiresAt().isBefore(Instant.now()))
            return false;

        if (otps.getAttempts() >= MAX_ATTEMPTS)
            return false;

        otps.setAttempts(otps.getAttempts() + 1);

        boolean ok = hash(otp).equals(otps.getOtpHash());
        if (ok) {
            otps.setUsed(true);
        }
        return ok;
    }

    private String generateOtp(int digits) {
        int bound = (int) Math.pow(10, digits);
        int min = (int) Math.pow(10, digits - 1);
        int n = new Random().nextInt(bound - min) + min;
        return String.valueOf(n);
    }

    private String hash(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] d = md.digest(s.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : d)
                sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public User checkUser(String email, String password) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'checkUser'");
    }

}
