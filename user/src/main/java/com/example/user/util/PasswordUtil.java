package com.example.user.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public final class PasswordUtil {
    private static final int COST = 12; // 10–12 is a good default
    private static final PasswordEncoder ENCODER = new BCryptPasswordEncoder(COST);

    // Hash (store the result in DB)
    public String hash(String rawPassword) {
        return ENCODER.encode(rawPassword);
    }

    // Verify (compare login password with stored hash)
    public boolean matches(String rawPassword, String storedHash) {
        return ENCODER.matches(rawPassword, storedHash);
    }
}
