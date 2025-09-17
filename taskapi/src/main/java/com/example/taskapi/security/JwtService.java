package com.example.taskapi.security;

import org.springframework.security.core.userdetails.UserDetails;
import java.util.Map;

/**
 * JWT Service interface for token operations
 *
 * Provides methods for JWT token generation, validation, and extraction
 * following modern security practices and standards.
 */
public interface JwtService {

    /**
     * Generate JWT token from UserDetails
     *
     * @param userDetails the authenticated user details
     * @return JWT token string
     */
    String generateToken(UserDetails userDetails);

    /**
     * Extract username from JWT token
     *
     * @param token JWT token
     * @return username/email from token
     */
    String extractUsername(String token);

    /**
     * Extract user ID from JWT token
     *
     * @param token JWT token
     * @return user ID from token
     */
    Long extractUserId(String token);

    /**
     * Validate JWT token against UserDetails
     *
     * @param token JWT token
     * @param userDetails the authenticated user details
     * @return true if token is valid, false otherwise
     */
    boolean isTokenValid(String token, UserDetails userDetails);

    /**
     * Check if JWT token is expired
     *
     * @param token JWT token
     * @return true if token is expired, false otherwise
     */
    boolean isTokenExpired(String token);

}
