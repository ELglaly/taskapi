package com.example.taskapi.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;
import java.util.Map;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidInputException extends AppException {
    @Serial
    private static final long serialVersionUID = 1L;

    public InvalidInputException(String message) {
        super(message);
        getError().setStatus(HttpStatus.BAD_REQUEST.value());
    }
}
