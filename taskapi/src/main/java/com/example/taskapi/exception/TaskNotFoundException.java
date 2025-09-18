package com.example.taskapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TaskNotFoundException extends AppException {
    public TaskNotFoundException() {
        super("Task not found");
        getError().setErrorCode("TASK_NOT_FOUND");
        getError().setStatus(HttpStatus.NOT_FOUND.value());
    }
}
