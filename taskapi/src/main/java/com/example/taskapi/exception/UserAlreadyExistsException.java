package com.example.taskapi.exception;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
@Slf4j
public class UserAlreadyExistsException extends AppException {
    public UserAlreadyExistsException() {
        super("User already exists");
        getError().setStatus(HttpStatus.CONFLICT.value());
        getError().setErrorCode("USER_ALREADY_EXISTS");
        log.warn("user already exists");
    }
}
