package com.example.taskapi.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends AppException {
    private static final long serialVersionUID = 1L;

    public UserNotFoundException() {
        super("User not found");
        getError().setStatus(HttpStatus.NOT_FOUND.value());
        getError().setErrorCode("USER_NOT_FOUND");
        log.warn("user not found");
    }
}
