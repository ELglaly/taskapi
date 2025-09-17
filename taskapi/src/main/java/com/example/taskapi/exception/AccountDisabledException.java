package com.example.taskapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AccountDisabledException extends AppException {
    public AccountDisabledException(String message) {
        super(message);
        getError().setStatus(HttpStatus.BAD_REQUEST.value());
    }
}
