package com.example.taskapi.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Custom validation annotation to prevent XSS attacks in string fields.
 *
 * This annotation validates that the input doesn't contain potentially
 * dangerous HTML/JavaScript content that could lead to XSS vulnerabilities.
 */
@Documented
@Constraint(validatedBy = NoXSSValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface NoXSS {
    String message() default "Input contains potentially dangerous content";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}