package com.example.taskapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InValidCredientailException extends AppException {
    public InValidCredientailException() {
        super("Password or email is incorrect");
        getError().setStatus(HttpStatus.BAD_REQUEST.value());
        getError().setErrorCode("INVALID_CREDENTIALS");
    }
}
