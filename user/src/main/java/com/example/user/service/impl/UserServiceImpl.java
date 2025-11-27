package com.example.user.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.user.dto.CreateUserRequestDto;
import com.example.user.dto.UserResponseDto;
import com.example.user.entity.User;
import com.example.user.repository.UserRepository;
import com.example.user.service.UserService;
import com.example.user.util.PasswordUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordUtil passwordUtil;

    @Override
    public String createUser(CreateUserRequestDto createUserRequestDto) {

        String hashedPassword = passwordUtil.hash(createUserRequestDto.getPassword());

        User user = new User();
        user.setName(createUserRequestDto.getName());
        user.setEmail(createUserRequestDto.getEmail());
        user.setPassword(hashedPassword);
        userRepository.save(user);
        return "User created successfully";
    }

    @Override
    public UserResponseDto getUser(int id) {
        User user = userRepository.findById(id).get();
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setName(user.getName());
        userResponseDto.setEmail(user.getEmail());
        return userResponseDto;
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(user -> {
            UserResponseDto userResponseDto = new UserResponseDto();
            userResponseDto.setName(user.getName());
            userResponseDto.setEmail(user.getEmail());
            return userResponseDto;
        }).toList();
    }

}
