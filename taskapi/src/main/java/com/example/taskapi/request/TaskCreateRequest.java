package com.example.taskapi.request;

import com.example.taskapi.entity.appenum.TaskStatus;
import com.example.taskapi.validation.NoXSS;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TaskCreateRequest {

    @NotBlank(message = "Title is required and cannot be empty")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    @Pattern(regexp = "^[\\p{L}\\p{N}\\p{P}\\p{Z}]{3,100}$", message = "Title contains invalid characters")
    @NoXSS
    private String title;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    @NoXSS
    private String description;

    @NotBlank(message = "Status is required")
    private String status;
}
