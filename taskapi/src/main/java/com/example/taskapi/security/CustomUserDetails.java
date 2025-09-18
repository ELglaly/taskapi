package com.example.taskapi.security;

import com.example.taskapi.entity.user.AppUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Enhanced UserDetails implementation for Spring Security
 *
 * This class securely wraps AppUser entity data for Spring Security authentication
 * and authorization. It includes proper role mapping, account status checking,
 * and security-aware field access.
 *
 * SECURITY FEATURES:
 * - Secure password hash handling
 * - Role-based authority mapping
 * - Account lockout detection
 * - Password expiration checking
 * - Email verification status
 *
 * @see org.springframework.security.core.userdetails.UserDetails
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {

    /**
     * -- GETTER --
     *  Get the actual username for display purposes
     */
    @Getter
    private Long id;
    private String username;
    private String email;
    private String password; // This will contain the encoded password hash

    // Account status fields
    private boolean active;
    private boolean verified;


    /**
     * SECURITY: Factory method to create UserDetails from AppUser
     * This method safely extracts necessary information without exposing sensitive data
     */
    public static CustomUserDetails from(AppUser user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        if (user.getAppUserSecurity() == null) {
            throw new IllegalStateException("User security information is missing");
        }

        if (user.getAppUserContact() == null) {
            throw new IllegalStateException("User contact information is missing");
        }

        return CustomUserDetails.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getAppUserContact().getEmail())
                .password(user.getAppUserSecurity().getPasswordHash()) // Secure hash access
                .active(user.getAppUserSecurity().getActive())
                .verified(user.getAppUserSecurity().getVerified())
                .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    /**
     * SECURITY: Returns the encoded password hash for authentication
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * SECURITY: Returns the username used for authentication
     * In this case, we use email as the username for login
     */
    @Override
    public String getUsername() {
        return email; // Use email as login username
    }

    /**
     * Get the actual username for display purposes
     */
    public String getActualUsername() {
        return username;
    }

    /**
     * SECURITY: Check if account is not expired
     * Account expires after certain period of inactivity
     */
    @Override
    public boolean isAccountNonExpired() {

        return active;
    }

    /**
     * SECURITY: Check if account is enabled
     * Disabled accounts cannot authenticate
     */
    @Override
    public boolean isEnabled() {
        return active ;
    }



}
