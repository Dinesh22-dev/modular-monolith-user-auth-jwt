package com.example.auth.config;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

import com.example.auth.service.CustomUserDetailService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EmailOnlyAuthenticationProvider implements AuthenticationProvider {

    private final CustomUserDetailService customUserDetailService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();

        if (email == null || email.isBlank()) {
            throw new BadCredentialsException("Email is empty");
        }

        UserDetails user = customUserDetailService.loadUserByUsername(email);

        if (user == null) {
            throw new BadCredentialsException("Email not found");
        }

        UsernamePasswordAuthenticationToken authenticated = new UsernamePasswordAuthenticationToken(user, null,
                user.getAuthorities());
        authenticated.setDetails(authentication.getDetails());
        return authenticated;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(clazz)
                || PreAuthenticatedAuthenticationToken.class.isAssignableFrom(clazz);
    }

}
