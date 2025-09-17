package com.example.taskapi.validation;

import com.example.taskapi.request.TaskCreateRequest;
import com.example.taskapi.request.TaskUpdateRequest;

public interface TaskValidation {
    void validateTaskCreateRequest(TaskCreateRequest taskCreateRequest);
    void validateTaskUpdateRequest(TaskUpdateRequest taskUpdateRequest);
}
