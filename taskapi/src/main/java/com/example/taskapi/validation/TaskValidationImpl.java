package com.example.taskapi.validation;

import com.example.taskapi.entity.appenum.TaskStatus;
import com.example.taskapi.exception.InvalidInputException;
import com.example.taskapi.request.TaskCreateRequest;
import com.example.taskapi.request.TaskUpdateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Arrays;
import java.util.regex.Pattern;

@Component
@Slf4j
public class TaskValidationImpl implements TaskValidation {

    // XSS patterns to detect potentially malicious content
    private static final String[] XSS_PATTERNS = {
            "<script", "</script>", "javascript:", "onload=", "onclick=", "onerror=",
            "onmouseover=", "<iframe", "eval(", "alert(", "document.cookie",
            "window.location", "<object", "<embed", "<link", "<meta"
    };

    // SQL injection patterns
    private static final String[] SQL_INJECTION_PATTERNS = {
            "union", "select", "insert", "update", "delete", "drop", "create",
            "alter", "exec", "execute", "--", "/*", "*/", "xp_", "sp_"
    };

    @Override
    public void validateTaskCreateRequest(TaskCreateRequest request) {

        log.debug("Validating task creation request: {}", request);

        // NULL VALIDATION
        Objects.requireNonNull(request, "Task creation request cannot be null");
        Objects.requireNonNull(request.getTitle(), "Task title cannot be null");
        Objects.requireNonNull(request.getDescription(), "description title cannot be null");
        Objects.requireNonNull(request.getStatus(), "Task status cannot be null");

        // TITLE VALIDATION
        validateTitle(request.getTitle());

        // DESCRIPTION VALIDATION (optional field)
        if (!request.getDescription().trim().isEmpty()) {
            validateDescription(request.getDescription());
        }

        // STATUS VALIDATION
        validateStatusString(request.getStatus());

        // SECURITY VALIDATION
        validateForXSS(request.getTitle(), "title");
        validateForXSS(request.getDescription(), "description");

        // SQL INJECTION VALIDATION
        validateForSQLInjection(request.getTitle(), "title");
        validateForSQLInjection(request.getDescription(), "description");

        log.debug("Task creation request validation passed");
    }

    @Override
    public void validateTaskUpdateRequest(TaskUpdateRequest request) {

        log.debug("Validating task update request: {}", request);

        // NULL VALIDATION
        Objects.requireNonNull(request, "Task update request cannot be null");


        // STATUS VALIDATION (if provided)
        if (request.status() != null) {
            validateStatusString(request.status());
        }

        log.debug("Task update request validation passed");
    }

    /**
     * Validate task title
     */
    private void validateTitle(String title) {

        if (title == null || title.trim().isEmpty()) {
            throw new InvalidInputException("Title is required and cannot be empty");
        }

        String trimmedTitle = title.trim();

        if (trimmedTitle.length() < 3) {
            throw new InvalidInputException("Title must be at least 3 characters long");
        }

        if (trimmedTitle.length() > 100) {
            throw new InvalidInputException("Title cannot exceed 100 characters");
        }

        // Check for valid characters (letters, numbers, punctuation, spaces)
        if (!Pattern.matches("^[\\p{L}\\p{N}\\p{P}\\p{Z}]{3,100}$", trimmedTitle)) {
            throw new InvalidInputException("Title contains invalid characters. Only letters, numbers, punctuation, and spaces are allowed");
        }

        // Check for excessive special characters
        long specialCharCount = trimmedTitle.chars()
                .filter(ch -> !Character.isLetterOrDigit(ch) && !Character.isWhitespace(ch))
                .count();

        if (specialCharCount > trimmedTitle.length() / 2) {
            throw new InvalidInputException("Title contains too many special characters");
        }
    }

    /**
     * Validate task description
     */
    private void validateDescription(String description) {

        if (description != null && description.length() > 500) {
            throw new InvalidInputException("Description cannot exceed 500 characters");
        }

        // Allow empty description (optional field)
        if (description != null && !description.trim().isEmpty()) {

            // Check for excessive special characters
            String trimmedDesc = description.trim();
            long specialCharCount = trimmedDesc.chars()
                    .filter(ch -> !Character.isLetterOrDigit(ch) && !Character.isWhitespace(ch))
                    .count();

            if (specialCharCount > trimmedDesc.length() / 2) {
                throw new InvalidInputException("Description contains too many special characters");
            }
        }
    }

    /**
     * Validate task status enum
     */
    private void validateStatus(TaskStatus status) {

        if (status == null) {
            throw new InvalidInputException("Task status is required");
        }

        // Ensure status is one of the valid enum values
        boolean isValidStatus = Arrays.stream(TaskStatus.values())
                .anyMatch(validStatus -> validStatus == status);

        if (!isValidStatus) {
            throw new InvalidInputException("Invalid task status: " + status);
        }
    }

    /**
     * Validate task status string (for update requests)
     */
    private void validateStatusString(String statusString) {

        if (statusString == null || statusString.trim().isEmpty()) {
            throw new InvalidInputException("Status cannot be empty if provided");
        }

        try {
            TaskStatus.valueOf(statusString.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException("Invalid task status: " + statusString +
                    ". Valid statuses are: " + Arrays.toString(TaskStatus.values()));
        }
    }

    /**
     * SECURITY: Validate for XSS patterns
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
     * SECURITY: Validate for SQL injection patterns
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
}
