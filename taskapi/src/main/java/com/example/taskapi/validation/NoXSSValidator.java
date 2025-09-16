package com.example.taskapi.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * Validator implementation for @NoXSS annotation.
 *
 * This validator checks for common XSS attack patterns and prevents
 * potentially dangerous content from being stored in the database.
 *
 * Security patterns detected:
 * - Script tags
 * - Event handlers (onclick, onload, etc.)
 * - JavaScript protocol URLs
 * - Data URLs with JavaScript
 * - HTML comments that might hide malicious code
 * - Style attributes with expression()
 */
@Component
public class NoXSSValidator implements ConstraintValidator<NoXSS, String> {

    // Pattern to detect potential XSS attacks
    private static final Pattern XSS_PATTERN = Pattern.compile(
            "(?i)" + // Case insensitive
                    "(" +
                    "<script[^>]*>.*?</script>" + // Script tags
                    "|<.*?javascript:.*?>" + // JavaScript protocol
                    "|<.*?on\\w+\\s*=.*?>" + // Event handlers
                    "|<.*?style\\s*=.*?expression\\s*\\(.*?\\).*?>" + // CSS expressions
                    "|<.*?src\\s*=\\s*[\"']?data:.*?base64.*?[\"']?.*?>" + // Data URLs
                    "|<!--.*?-->" + // HTML comments
                    "|<iframe[^>]*>.*?</iframe>" + // Iframes
                    "|<object[^>]*>.*?</object>" + // Objects
                    "|<embed[^>]*>" + // Embeds
                    "|<link[^>]*>" + // Links
                    "|<meta[^>]*>" + // Meta tags
                    ")",
            Pattern.DOTALL
    );

    // Additional pattern for common XSS payloads
    private static final Pattern DANGEROUS_FUNCTIONS = Pattern.compile(
            "(?i)" +
                    "(" +
                    "alert\\s*\\(" + // Alert functions
                    "|confirm\\s*\\(" + // Confirm functions
                    "|prompt\\s*\\(" + // Prompt functions
                    "|eval\\s*\\(" + // Eval functions
                    "|setTimeout\\s*\\(" + // SetTimeout
                    "|setInterval\\s*\\(" + // SetInterval
                    "|document\\.write" + // Document.write
                    "|document\\.cookie" + // Cookie access
                    "|window\\.location" + // Location manipulation
                    ")"
    );

    @Override
    public void initialize(NoXSS constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Null values are considered valid (use @NotNull for null checks)
        if (value == null || value.trim().isEmpty()) {
            return true;
        }

        // Check for XSS patterns
        if (containsXSSPattern(value)) {
            addViolationMessage(context, "Input contains potentially dangerous HTML/JavaScript content");
            return false;
        }

        // Check for dangerous JavaScript functions
        if (containsDangerousFunctions(value)) {
            addViolationMessage(context, "Input contains potentially dangerous JavaScript functions");
            return false;
        }

        // Check for encoded XSS attempts
        if (containsEncodedXSS(value)) {
            addViolationMessage(context, "Input contains encoded potentially dangerous content");
            return false;
        }

        return true;
    }

    /**
     * Checks if the input contains XSS patterns
     */
    private boolean containsXSSPattern(String value) {
        return XSS_PATTERN.matcher(value).find();
    }

    /**
     * Checks if the input contains dangerous JavaScript functions
     */
    private boolean containsDangerousFunctions(String value) {
        return DANGEROUS_FUNCTIONS.matcher(value).find();
    }

    /**
     * Checks for common encoding attempts to bypass XSS filters
     */
    private boolean containsEncodedXSS(String value) {
        // Decode common HTML entities and URL encoding
        String decoded = decodeCommonEncodings(value);

        // Check decoded content for XSS patterns
        return XSS_PATTERN.matcher(decoded).find() ||
                DANGEROUS_FUNCTIONS.matcher(decoded).find();
    }

    /**
     * Decodes common HTML entities and URL encodings that might be used to bypass filters
     */
    private String decodeCommonEncodings(String value) {
        String decoded = value;

        // Decode HTML entities
        decoded = decoded.replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"")
                .replace("&#x27;", "'")
                .replace("&#x2F;", "/")
                .replace("&amp;", "&");

        // Decode URL encoding for common XSS characters
        decoded = decoded.replace("%3C", "<")
                .replace("%3E", ">")
                .replace("%22", "\"")
                .replace("%27", "'")
                .replace("%2F", "/")
                .replace("%3D", "=");

        // Decode Unicode escapes
        decoded = decoded.replace("\\\\u003c", "<")
                .replace("\\\\u003e", ">")
                .replace("\\\\u0022", "\"")
                .replace("\\\\u0027", "'");

        return decoded;
    }

    /**
     * Adds a custom violation message to the context
     */
    private void addViolationMessage(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }
}