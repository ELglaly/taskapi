package com.example.taskapi.exception;

import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UserAlreadyExistsException extends AppException {
    public UserAlreadyExistsException() {
        super("User already exists");
        getError().setStatus(HttpStatus.CONFLICT.value());
    }
}
