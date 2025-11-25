package com.example.auth.dto;

import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private String name;
    private String email;

}
