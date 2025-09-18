package com.example.taskapi.request;

import com.example.taskapi.validation.NoXSS;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record RegistrationRequest
        (
                @NotBlank(message = "Email is required")
                @Email(message = "Please provide a valid email address")
                @Size(max = 255, message = "Email cannot exceed 255 characters")
                @NoXSS
                String email,

                @NotBlank(message = "Password is required")
                @NoXSS
                String password,

                @NotBlank(message = "Name is required")
                @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
                @Pattern(regexp = "^[\\p{L}\\s'-]{2,100}$", message = "Name contains invalid characters")
                @NoXSS
                String name
        ) {
}
