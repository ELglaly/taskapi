package com.example.taskapi.request;

import com.example.taskapi.validation.NoXSS;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record LoginRequest
        (
                @NotBlank(message = "Email is required")
                @Email(message = "Please provide a valid email address")
                @Size(max = 255, message = "Email cannot exceed 255 characters")
                @NoXSS
                String email,

                @NotBlank(message = "Password is required")
                @NoXSS
                String password
        ) {
}
