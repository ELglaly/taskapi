package com.example.taskapi.exception;

import com.example.taskapi.response.AppErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
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
     * Handle TaskNotFoundException - SPECIFIC HANDLER FIRST
     */
    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<AppErrorResponse> handleTaskNotFoundException(
            TaskNotFoundException ex, WebRequest request) {

        log.warn("Task not found: {}", ex.getMessage());

        AppErrorResponse errorResponse = AppErrorResponse.builder()
                .message(ex.getMessage() != null ? ex.getMessage() : "Task not found")
                .status(HttpStatus.NOT_FOUND.value())
                .errorCode("TASK_NOT_FOUND")
                .path(extractPath(request))
                .timestamp()
                .isLoggable(false)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Handle constraint validation exceptions
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
     * Handle specific BadCredentialsException - FIXED: More specific than AuthenticationException
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<AppErrorResponse> handleBadCredentialsException(
            BadCredentialsException ex, WebRequest request) {

        log.warn("Bad credentials: {}", ex.getMessage());

        AppErrorResponse errorResponse = AppErrorResponse.builder()
                .message("Invalid email or password")
                .status(HttpStatus.UNAUTHORIZED.value())
                .errorCode("INVALID_CREDENTIALS")
                .path(extractPath(request))
                .timestamp()
                .isLoggable(false)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handle general authentication exceptions - FIXED: Exclude BadCredentialsException
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<AppErrorResponse> handleAuthenticationException(
            AuthenticationException ex, WebRequest request) {

        log.warn("Authentication failed: {}", ex.getMessage());

        AppErrorResponse errorResponse = AppErrorResponse.builder()
                .message("Authentication failed")
                .status(HttpStatus.UNAUTHORIZED.value())
                .errorCode("AUTHENTICATION_FAILED")
                .path(extractPath(request))
                .timestamp()
                .isLoggable(false)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handle UserNotFoundException
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<AppErrorResponse> handleUserNotFoundException(
            UserNotFoundException ex, WebRequest request) {

        log.warn("User not found: {}", ex.getMessage());

        AppErrorResponse errorResponse = AppErrorResponse.builder()
                .message("User not found")
                .status(HttpStatus.NOT_FOUND.value())
                .errorCode("USER_NOT_FOUND")
                .path(extractPath(request))
                .timestamp()
                .isLoggable(false)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Handle UserAlreadyExistsException
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<AppErrorResponse> handleUserAlreadyExistsException(
            UserAlreadyExistsException ex, WebRequest request) {

        log.warn("User already exists: {}", ex.getMessage());

        AppErrorResponse errorResponse = AppErrorResponse.builder()
                .message(ex.getMessage() != null ? ex.getMessage() : "User already exists")
                .status(HttpStatus.CONFLICT.value())
                .errorCode("USER_ALREADY_EXISTS")
                .path(extractPath(request))
                .timestamp()
                .isLoggable(false)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    /**
     * Handle account locked exceptions
     */
    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<AppErrorResponse> handleAccountLockedException(

            AccountLockedException ex, WebRequest request) {
        log.warn("Account locked: {}", ex.getMessage());

        AppErrorResponse errorResponse = AppErrorResponse.builder()
                .message(ex.getMessage() != null ? ex.getMessage() : "Account is temporarily locked")
                .status(HttpStatus.LOCKED.value())
                .errorCode("ACCOUNT_LOCKED")
                .path(extractPath(request))
                .timestamp()
                .isLoggable(false)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.LOCKED);
    }

    /**
     * Handle account disabled exceptions
     */
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<AppErrorResponse> handleAccountDisabledException(
            DisabledException ex, WebRequest request) {

        log.warn("Account disabled: {}", ex.getMessage());

        AppErrorResponse errorResponse = AppErrorResponse.builder()
                .message("Account is disabled")
                .status(HttpStatus.UNAUTHORIZED.value())
                .errorCode("ACCOUNT_DISABLED")
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
     * Handle data integrity violations
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<AppErrorResponse> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex, WebRequest request) {

        log.error("Data integrity violation: {}", ex.getMessage());

        String message = "Data integrity constraint violated";
        String errorCode = "DATA_INTEGRITY_VIOLATION";

        // Check for specific constraint violations
        if (ex.getMessage() != null) {
            if (ex.getMessage().contains("email")) {
                message = "Email already exists";
                errorCode = "EMAIL_ALREADY_EXISTS";
            } else if (ex.getMessage().contains("username")) {
                message = "Username already exists";
                errorCode = "USERNAME_ALREADY_EXISTS";
            }
        }

        AppErrorResponse errorResponse = AppErrorResponse.builder()
                .message(message)
                .status(HttpStatus.CONFLICT.value())
                .errorCode(errorCode)
                .path(extractPath(request))
                .timestamp()
                .isLoggable(true)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    /**
     * Handle illegal argument exceptions
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<AppErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {

        log.warn("Illegal argument: {}", ex.getMessage());

        AppErrorResponse errorResponse = AppErrorResponse.builder()
                .message(ex.getMessage() != null ? ex.getMessage() : "Invalid request parameters")
                .status(HttpStatus.BAD_REQUEST.value())
                .errorCode("INVALID_REQUEST")
                .path(extractPath(request))
                .timestamp()
                .isLoggable(false)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle all other exceptions (fallback) - FIXED: More specific type to avoid conflicts
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<AppErrorResponse> handleRuntimeException(
            RuntimeException ex, WebRequest request) {

        log.error("Unexpected runtime error occurred", ex);

        AppErrorResponse errorResponse = AppErrorResponse.builder()
                .message("An unexpected error occurred. Please try again later.")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .errorCode("RUNTIME_ERROR")
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
