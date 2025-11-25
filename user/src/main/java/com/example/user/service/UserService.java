package com.example.user.service;

import java.util.List;

import com.example.user.dto.CreateUserRequestDto;
import com.example.user.dto.UserResponseDto;

public interface UserService {

    String createUser(CreateUserRequestDto createUserRequestDto);

    UserResponseDto getUser(int id);

    List<UserResponseDto> getAllUsers();

}
