package com.example.taskapi.security;

import com.example.taskapi.entity.user.AppUser;
import com.example.taskapi.exception.UserNotFoundException;
import com.example.taskapi.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Custom UserDetailsService implementation for Spring Security
 *
 * This service loads user details from the database for authentication
 * and authorization purposes. It integrates with our custom UserDetails
 * implementation and provides secure user data access.
 */
@Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * SECURITY: Load user by email (used as username in our system)
     *
     * @param email The user's email address (our login username)
     * @return UserDetails for the authenticated user
     * @throws UsernameNotFoundException if user not found
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        log.info("Loading user by email: {}", email);
        if (email == null || email.trim().isEmpty()) {
            throw new UsernameNotFoundException("Email cannot be empty");
        }

        // Find user by email
        AppUser user = Optional.ofNullable(userRepository.findByAppUserContactEmail(email.toLowerCase().trim(),AppUser.class))
                .orElseThrow(UserNotFoundException::new);

        log.info("User found: {}", user.getAppUserContact().getEmail());
        // Check if user data is complete
        if (user.getAppUserSecurity() == null) {
            log.error("User security data missing for user: {}", user.getUsername());
            throw new IllegalStateException(
                    String.format("User security data missing for user: %s", user.getUsername())
            );
        }

        if (user.getAppUserContact() == null) {
            log.error("User contact data missing for user: {}", user.getUsername());
            throw new IllegalStateException(
                    String.format("User contact data missing for user: %s", user.getUsername())
            );
        }

        // Create and return secure UserDetails
        return CustomUserDetails.from(user);
    }
}
