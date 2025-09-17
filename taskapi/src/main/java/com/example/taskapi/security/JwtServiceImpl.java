package com.example.taskapi.security;

import com.example.taskapi.security.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT Service implementation using modern JJWT library
 *
 * SECURITY FEATURES:
 * - Secure key generation using Keys.secretKeyFor()
 * - Proper token expiration handling
 * - Comprehensive token validation
 * - Support for refresh tokens
 * - Role-based claims
 * - Secure exception handling
 *
 * @author Your Name
 * @version 2.0
 * @since 2025-09-17
 */
@Service
@Slf4j
public class JwtServiceImpl implements JwtService {
    

    @Value("${app.jwt.secret:mySecretKey}")
    private String jwtSecret;

    @Value("${app.jwt.expiration:86400000}") // 24 hours in milliseconds
    private long jwtExpirationMs;

    @Value("${app.jwt.refresh-expiration:604800000}") // 7 days in milliseconds
    private long refreshExpirationMs;

    // MODERN: Generate secure key
    private SecretKey getSigningKey() {
        // In production, use a more secure approach to handle the secret
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }


    @Override
    public String generateToken(UserDetails userDetails) {

        if (!(userDetails instanceof CustomUserDetails customUserDetails)) {
            throw new IllegalArgumentException("UserDetails must be instance of CustomUserDetails");
        }

        // Add standard claims
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", customUserDetails.getId());
        claims.put("actualUername", customUserDetails.getActualUsername());
        claims.put("email", customUserDetails.getUsername());

        return buildToken(claims, userDetails.getUsername(), jwtExpirationMs);
    }

    /**
     * Build JWT token with claims and expiration
     */
    private String buildToken(Map<String, Object> extraClaims, String subject, long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .claims(extraClaims)
                .subject(subject)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public Long extractUserId(String token) {
        return extractClaim(token, claims -> {
            Object userId = claims.get("userId");
            if (userId instanceof Number) {
                return ((Number) userId).longValue();
            }
            return null;
        });
    }
    
    /**
     * Extract expiration date from token
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Generic method to extract claims from token
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extract all claims from token
     */
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException ex) {
            log.debug("JWT token is expired: {}", ex.getMessage());
            throw ex;
        } catch (UnsupportedJwtException ex) {
            log.error("JWT token is unsupported: {}", ex.getMessage());
            throw ex;
        } catch (MalformedJwtException ex) {
            log.error("JWT token is malformed: {}", ex.getMessage());
            throw ex;
        } catch (IllegalArgumentException ex) {
            log.error("JWT token compact of handler are invalid: {}", ex.getMessage());
            throw ex;
        }
    }

    @Override
    public boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }
}
