package com.example.taskapi.request;

import com.example.taskapi.entity.appenum.TaskStatus;
import lombok.Builder;

@Builder
public record TaskUpdateRequest(
        String status
) {
}