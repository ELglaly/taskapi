package com.example.taskapi.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends AppException {
    private static final long serialVersionUID = 1L;

    public UserNotFoundException() {
        super("User not found");
        getError().setStatus(HttpStatus.NOT_FOUND.value());
    }
}
