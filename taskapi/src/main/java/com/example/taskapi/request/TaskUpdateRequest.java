package com.example.taskapi.request;

import com.example.taskapi.entity.appenum.TaskStatus;

public record TaskUpdateRequest(
        String title,
        String description,
        String status
) {
}