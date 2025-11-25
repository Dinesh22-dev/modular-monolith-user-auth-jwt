package com.example.user.dto;

import lombok.Data;

@Data
public class CreateUserRequestDto {

    private String name;

    private String email;

    private String password;

}
