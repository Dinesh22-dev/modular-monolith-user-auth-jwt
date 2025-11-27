package com.example.auth.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.auth.dto.AuthRequest;
import com.example.auth.dto.AuthResponse;
import com.example.auth.service.CustomUserDetailService;
import com.example.auth.service.otp.OtpService;
import com.example.auth.util.JWTUtil;
import com.example.user.entity.User;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    @Qualifier("authenticationManager")
    private final AuthenticationManager passwordAuthManager;

    @Qualifier("onlyEmailAuthenticationManager")
    private final AuthenticationManager emailOnlyAuthManager;

    private final CustomUserDetailService userDetailService;

    private final OtpService otpService;

    private final JWTUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        try {
            passwordAuthManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUserName(), authRequest.getPassword()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid Credentials"));
        }

        final UserDetails userDetails = userDetailService.loadUserByUsername(authRequest.getUserName());
        final String jwt = jwtUtil.generateToken(userDetails);

        User user = userDetailService.getUserByLogin(authRequest.getUserName());

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid Credentials"));
        }

        if (!user.isActive()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Your account is deactivated"));
        }

        AuthResponse authResponse = new AuthResponse();
        authResponse.setToken(jwt);
        authResponse.setName(user.getName());
        authResponse.setEmail(user.getEmail());

        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/login-email-only")
    public ResponseEntity<?> loginEmailOnly(@RequestParam String email) {
        try {
            emailOnlyAuthManager.authenticate(new UsernamePasswordAuthenticationToken(email, ""));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid Credentials"));
        }

        final UserDetails userDetails = userDetailService.loadUserByUsername(email);
        final String jwt = jwtUtil.generateToken(userDetails);

        User user = userDetailService.getUserByLogin(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid Credentials"));
        }
        if (!user.isActive()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Your account is deactivated"));
        }

        AuthResponse authResponse = new AuthResponse();
        authResponse.setToken(jwt);
        authResponse.setName(user.getName());
        authResponse.setEmail(user.getEmail());

        return ResponseEntity.ok(authResponse);

    }

    @PostMapping("/login-generate-otp")
    public ResponseEntity<?> loginGenerateOtp(@RequestBody AuthRequest authRequest) {
        User user = otpService.checkUser(authRequest.getUserName(), authRequest.getPassword());

        if (user == null) {
            throw new RuntimeException("Invalid Credentials");
        }
        String otpTransactionId = otpService.startChallenge(user.getEmail(), null, user.getEmail());

        return ResponseEntity.ok(Map.of("otpTransactionId", otpTransactionId));
    }

    @PostMapping("/login-generate-otp-only-email")
    public ResponseEntity<?> loginGenerateOtpOnlyEmail(@RequestParam String email) {
        User user = otpService.checkUserOnlyEmail(email);

        if (user == null) {
            throw new RuntimeException("Invalid Credentials");
        }
        String otpTransactionId = otpService.startChallenge(user.getEmail(), null, user.getEmail());

        return ResponseEntity.ok(Map.of("otpTransactionId", otpTransactionId));
    }

    @PostMapping("/login-resend-otp")
    public ResponseEntity<?> loginResendOtp(@RequestParam String transactionId) {
        String otpTransactionId = otpService.resendOtp(transactionId);
        return ResponseEntity.ok(Map.of("otpTransactionId", otpTransactionId));
    }

    @PostMapping("/login-verify-otp")
    public ResponseEntity<?> loginVerifyOtp(@RequestParam String transactionId, @RequestParam String otp) {
        User user = otpService.verifyy(transactionId, otp);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid OTP or expired"));
        }

        // load UserDetails and generate token
        final UserDetails userDetails = userDetailService.loadUserByUsername(user.getEmail());
        final String jwt = jwtUtil.generateToken(userDetails);

        // you already use AuthResponse in other endpoints
        AuthResponse authResponse = new AuthResponse();
        authResponse.setToken(jwt);
        authResponse.setName(user.getName());
        authResponse.setEmail(user.getEmail());

        return ResponseEntity.ok(authResponse);
    }

}
