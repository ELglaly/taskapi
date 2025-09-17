package com.example.taskapi.exception;

import com.example.taskapi.response.AppErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import javax.security.auth.login.AccountLockedException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    /**
     * Handle Bean Validation errors (@Valid annotation failures)
     * This catches your email validation error
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<AppErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {

        log.error("Validation failed: {}", ex.getMessage());

        Map<String, String> fieldErrors = new HashMap<>();

        // Extract field-specific errors
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);

            log.debug("Field validation error - Field: {}, Value: {}, Message: {}",
                    fieldName, error.getRejectedValue(), errorMessage);
        });

        AppErrorResponse errorResponse = AppErrorResponse.builder()
                .message("Input validation failed. Please check your request data.")
                .status(HttpStatus.BAD_REQUEST.value())
                .errorCode("VALIDATION_FAILED")
                .path(extractPath(request))
                .timestamp()
                .fieldErrors(fieldErrors)
                .isLoggable(true)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle constraint validation exceptions (method-level validation)
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<AppErrorResponse> handleConstraintViolationException(
            ConstraintViolationException ex, WebRequest request) {

        log.error("Constraint violation: {}", ex.getMessage());

        Map<String, String> violations = new HashMap<>();

        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String propertyPath = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            violations.put(propertyPath, message);
        }

        AppErrorResponse errorResponse = AppErrorResponse.builder()
                .message("Request validation failed")
                .status(HttpStatus.BAD_REQUEST.value())
                .errorCode("CONSTRAINT_VIOLATION")
                .path(extractPath(request))
                .timestamp()
                .fieldErrors(violations)
                .isLoggable(true)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle custom business logic exceptions
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<AppErrorResponse> handleUserAlreadyExistsException(
            UserAlreadyExistsException ex, WebRequest request) {

        log.warn("User registration failed - user already exists: {}", ex.getMessage());

        AppErrorResponse errorResponse = AppErrorResponse.builder()
                .message(ex.getMessage() != null ? ex.getMessage() : "User with this email already exists")
                .status(HttpStatus.CONFLICT.value())
                .errorCode("USER_ALREADY_EXISTS")
                .path(extractPath(request))
                .timestamp()
                .isLoggable(false)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    /**
     * Handle authentication exceptions
     */
    @ExceptionHandler({AuthenticationException.class, BadCredentialsException.class})
    public ResponseEntity<AppErrorResponse> handleAuthenticationException(
            Exception ex, WebRequest request) {

        log.warn("Authentication failed: {}", ex.getMessage());

        AppErrorResponse errorResponse = AppErrorResponse.builder()
                .message("Invalid credentials")
                .status(HttpStatus.UNAUTHORIZED.value())
                .errorCode("AUTHENTICATION_FAILED")
                .path(extractPath(request))
                .timestamp()
                .isLoggable(false)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handle authorization exceptions
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<AppErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex, WebRequest request) {

        log.warn("Access denied: {}", ex.getMessage());

        AppErrorResponse errorResponse = AppErrorResponse.builder()
                .message("You don't have permission to access this resource")
                .status(HttpStatus.FORBIDDEN.value())
                .errorCode("ACCESS_DENIED")
                .path(extractPath(request))
                .timestamp()
                .isLoggable(false)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    /**
     * Handle security exceptions
     */
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<AppErrorResponse> handleSecurityException(
            SecurityException ex, WebRequest request) {

        log.error("Security violation: {}", ex.getMessage());

        AppErrorResponse errorResponse = AppErrorResponse.builder()
                .message("Security constraint violated")
                .status(HttpStatus.FORBIDDEN.value())
                .errorCode("SECURITY_VIOLATION")
                .path(extractPath(request))
                .timestamp()
                .isLoggable(true)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    /**
     * Handle invalid input exceptions
     */
    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<AppErrorResponse> handleInvalidInputException(
            InvalidInputException ex, WebRequest request) {

        log.warn("Invalid input: {}", ex.getMessage());

        AppErrorResponse errorResponse = AppErrorResponse.builder()
                .message(ex.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .errorCode("INVALID_INPUT")
                .path(extractPath(request))
                .timestamp()
                .isLoggable(false)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle illegal argument exceptions
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<AppErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {

        log.warn("Illegal argument: {}", ex.getMessage());

        AppErrorResponse errorResponse = AppErrorResponse.builder()
                .message(ex.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .errorCode("INVALID_REQUEST")
                .path(extractPath(request))
                .timestamp()
                .isLoggable(false)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle weak password exceptions
     */
    @ExceptionHandler(WeakPasswordException.class)
    public ResponseEntity<AppErrorResponse> handleWeakPasswordException(
            WeakPasswordException ex, WebRequest request) {

        log.warn("Weak password provided: {}", ex.getMessage());

        AppErrorResponse errorResponse = AppErrorResponse.builder()
                .message(ex.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .errorCode("WEAK_PASSWORD")
                .path(extractPath(request))
                .timestamp()
                .isLoggable(false)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle account locked exceptions
     */
    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<AppErrorResponse> handleAccountLockedException(
            AccountLockedException ex, WebRequest request) {

        log.warn("Account locked: {}", ex.getMessage());

        AppErrorResponse errorResponse = AppErrorResponse.builder()
                .message(ex.getMessage())
                .status(HttpStatus.LOCKED.value())
                .errorCode("ACCOUNT_LOCKED")
                .path(extractPath(request))
                .timestamp()
                .isLoggable(false)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.LOCKED);
    }

    /**
     * Handle all other exceptions (fallback)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<AppErrorResponse> handleGlobalException(
            Exception ex, WebRequest request) {

        log.error("Unexpected error occurred", ex);

        AppErrorResponse errorResponse = AppErrorResponse.builder()
                .message("An unexpected error occurred. Please try again later.")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .errorCode("INTERNAL_SERVER_ERROR")
                .path(extractPath(request))
                .timestamp()
                .isLoggable(true)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Extract clean path from WebRequest
     */
    private String extractPath(WebRequest request) {
        String description = request.getDescription(false);
        return description.startsWith("uri=") ? description.substring(4) : description;
    }
}
