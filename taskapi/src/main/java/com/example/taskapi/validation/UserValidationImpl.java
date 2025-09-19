package com.example.taskapi.validation;

import com.example.taskapi.exception.InvalidInputException;
import com.example.taskapi.exception.WeakPasswordException;
import com.example.taskapi.request.LoginRequest;
import com.example.taskapi.request.RegistrationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.Objects;
import java.util.regex.Pattern;

@Component
@Slf4j
public class UserValidationImpl implements UserValidation {

    // Common password patterns to reject
    private static final String[] COMMON_PASSWORDS = {
            "password", "123456", "password123", "admin", "qwerty", "letmein",
            "welcome", "monkey", "1234567890", "abc123", "password1"
    };

    // XSS patterns for security validation
    private static final String[] XSS_PATTERNS = {
            "<script", "</script>", "javascript:", "onload=", "onclick=", "onerror=",
            "onmouseover=", "<iframe", "eval(", "alert(", "document.cookie",
            "window.location", "<object", "<embed"
    };

    // SQL injection patterns
    private static final String[] SQL_INJECTION_PATTERNS = {
            "union", "select", "insert", "update", "delete", "drop", "create",
            "alter", "exec", "execute", "--", "/*", "*/", "xp_", "sp_"
    };

    // Email regex pattern (RFC 5322 compliant)
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    // Name pattern (letters, spaces, hyphens, apostrophes)
    private static final Pattern NAME_PATTERN = Pattern.compile(
            "^[\\p{L}\\s'-]{2,100}$"
    );

    @Override
    public void registrationRequestValidation(RegistrationRequest request) {

        log.debug("Validating registration request for email: {}",
                request != null ? maskEmail(request.email()) : "null");

        // NULL VALIDATION
        Objects.requireNonNull(request, "Registration request cannot be null");
        Objects.requireNonNull(request.email(), "Email is required");
        Objects.requireNonNull(request.password(), "Password is required");
        Objects.requireNonNull(request.name(), "Name is required");

        // TRIM AND NORMALIZE
        String email = request.email().trim().toLowerCase();
        String password = request.password();
        String name = request.name().trim();

        // EMPTY CHECK AFTER TRIMMING
        if (email.isEmpty()) {
            throw new InvalidInputException("Email cannot be empty");
        }
        if (password.isEmpty()) {
            throw new InvalidInputException("Password cannot be empty");
        }
        if (name.isEmpty()) {
            throw new InvalidInputException("Name cannot be empty");
        }

        // EMAIL VALIDATION
        validateEmail(email);

        // PASSWORD VALIDATION
        validatePasswordStrength(password);

        // NAME VALIDATION
        validateName(name);

        // SECURITY VALIDATION
        validateForXSS(email, "email");
        validateForXSS(name, "name");
        validateForSQLInjection(email, "email");
        validateForSQLInjection(name, "name");


        log.debug("Registration request validation passed for: {}", maskEmail(email));
    }

    @Override
    public void loginRequestValidation(LoginRequest request) {

        log.debug("Validating login request");

        // NULL VALIDATION
        Objects.requireNonNull(request, "Login request cannot be null");
        Objects.requireNonNull(request.email(), "Email is required");
        Objects.requireNonNull(request.password(), "Password is required");

        // TRIM AND NORMALIZE
        String email = request.email().trim().toLowerCase();
        String password = request.password();

        // EMPTY CHECK AFTER TRIMMING
        if (email.isEmpty()) {
            throw new InvalidInputException("Email cannot be empty");
        }
        if (password.isEmpty()) {
            throw new InvalidInputException("Password cannot be empty");
        }

        // EMAIL FORMAT VALIDATION
        if (!isValidEmail(email)) {
            throw new InvalidInputException("Invalid email format");
        }

        // LENGTH VALIDATION
        if (email.length() > 255) {
            throw new InvalidInputException("Email is too long");
        }
        if (password.length() > 128) {
            throw new InvalidInputException("Password is too long");
        }

        // SECURITY VALIDATION
        validateForXSS(email, "email");
        validateForSQLInjection(email, "email");

        log.debug("Login request validation passed");
    }

    /**
     *  EMAIL VALIDATION
     */
    private void validateEmail(String email) {

        // Length check
        if (email.length() > 255) {
            throw new InvalidInputException("Email cannot exceed 255 characters");
        }

        // Format validation
        if (!isValidEmail(email)) {
            throw new InvalidInputException("Invalid email format");
        }
    }

    /**
     *  PASSWORD STRENGTH VALIDATION
     */
    private void validatePasswordStrength(String password) {

        // Length validation
        if (password.length() < 8) {
            throw new WeakPasswordException("Password must be at least 8 characters long");
        }

        if (password.length() > 128) {
            throw new WeakPasswordException("Password cannot exceed 128 characters");
        }
    }

    /**
     * NAME VALIDATION
     */
    private void validateName(String name) {

        // Length validation
        if (name.length() < 2) {
            throw new InvalidInputException("Name must be at least 2 characters long");
        }

        if (name.length() > 100) {
            throw new InvalidInputException("Name cannot exceed 100 characters");
        }

        // Pattern validation (letters, spaces, hyphens, apostrophes)
        if (!NAME_PATTERN.matcher(name).matches()) {
            throw new InvalidInputException("Name can only contain letters, spaces, hyphens, and apostrophes");
        }

        // Additional business rules
        if (name.trim().split("\\s+").length > 10) {
            throw new InvalidInputException("Name cannot contain more than 10 words");
        }

        // Check for excessive special characters
        long specialCharCount = name.chars()
                .filter(ch -> ch == '-' || ch == '\'')
                .count();

        if (specialCharCount > name.length() / 3) {
            throw new InvalidInputException("Name contains too many special characters");
        }
    }

    /**
     * SECURITY: XSS validation
     */
    private void validateForXSS(String input, String fieldName) {

        if (input == null) return;

        String lowerInput = input.toLowerCase();

        for (String pattern : XSS_PATTERNS) {
            if (lowerInput.contains(pattern.toLowerCase())) {
                log.warn("XSS pattern detected in {}: {}", fieldName, pattern);
                throw new InvalidInputException(String.format("Field '%s' contains potentially dangerous content", fieldName));
            }
        }

        // Check for HTML/XML tags
        if (Pattern.compile("<[^>]+>").matcher(input).find()) {
            log.warn("HTML tags detected in {}", fieldName);
            throw new InvalidInputException(String.format("Field '%s' cannot contain HTML tags", fieldName));
        }
    }

    /**
     * SECURITY: SQL injection validation
     */
    private void validateForSQLInjection(String input, String fieldName) {

        if (input == null) return;

        String lowerInput = input.toLowerCase();

        for (String pattern : SQL_INJECTION_PATTERNS) {
            if (lowerInput.contains(pattern)) {
                log.warn("SQL injection pattern detected in {}: {}", fieldName, pattern);
                throw new InvalidInputException(String.format("Field '%s' contains potentially dangerous content", fieldName));
            }
        }

        // Check for suspicious patterns
        if (lowerInput.matches(".*['\";].*")) {
            if (lowerInput.contains("' or ") || lowerInput.contains("\" or ") ||
                    lowerInput.contains("; drop") || lowerInput.contains("; delete")) {
                log.warn("Suspicious SQL pattern detected in {}", fieldName);
                throw new InvalidInputException(String.format("Field '%s' contains potentially dangerous content", fieldName));
            }
        }
    }

    /**
     * EMAIL FORMAT VALIDATION
     */
    private boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Email masking for logs
     */
    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "***@***.***";
        }

        String[] parts = email.split("@");
        String localPart = parts[0];
        String domain = parts[1];

        String maskedLocal = localPart.length() > 2 ?
                localPart.charAt(0) + "***" + localPart.charAt(localPart.length() - 1) :
                "***";

        return maskedLocal + "@" + domain;
    }
}
