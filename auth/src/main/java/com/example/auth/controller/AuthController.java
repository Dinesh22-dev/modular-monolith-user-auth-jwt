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

}
