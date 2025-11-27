package com.example.auth.service;

import java.util.ArrayList;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.user.entity.User;
import com.example.user.repository.UserRepository;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Getter
public class CustomUserDetailService implements UserDetailsService {

        private final UserRepository userRepository;

        // CustomUserDetailService
        @Override
        public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
                User user = userRepository.findByUsername(login)
                                .orElseGet(() -> userRepository.findByEmailIgnoreCase(login)
                                                .orElseThrow(() -> new UsernameNotFoundException(
                                                                "User not found: " + login)));

                boolean enabled = Boolean.TRUE.equals(user.isActive());
                String pwd = (user.getPassword() == null || user.getPassword().isBlank())
                                ? "PASSWORD_NOT_USED"
                                : user.getPassword();

                return new org.springframework.security.core.userdetails.User(
                                user.getUsername() != null ? user.getUsername() : user.getEmail(),
                                pwd,
                                enabled, true, true, true,
                                new ArrayList<>());
        }

        public User getUserByLogin(String login) {
                return userRepository.findByUsername(login)
                                .orElseGet(() -> userRepository.findByEmailIgnoreCase(login)
                                                .orElseThrow(() -> new UsernameNotFoundException(
                                                                "User not found: " + login)));
        }

}
