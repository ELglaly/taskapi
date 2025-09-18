package com.example.taskapi.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * MODERN Custom Authentication Provider
 * Replaces deprecated DaoAuthenticationProvider
 */
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public CustomAuthenticationProvider(
            CustomUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String email = authentication.getName();
        String password = authentication.getCredentials().toString();

        // Load user details
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        // Verify password
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        // Check account status
        if (!userDetails.isEnabled()) {
            throw new BadCredentialsException("Account is disabled");
        }
        if (!userDetails.isAccountNonLocked()) {
            throw new BadCredentialsException("Account is locked");
        }
        if (!userDetails.isAccountNonExpired()) {
            throw new BadCredentialsException("Account has expired");
        }
        if (!userDetails.isCredentialsNonExpired()) {
            throw new BadCredentialsException("Credentials have expired");
        }

        // Return authenticated token with authorities so isAuthenticated()==true
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
