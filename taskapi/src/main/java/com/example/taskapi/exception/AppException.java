package com.example.taskapi.exception;

import com.example.taskapi.response.AppErrorResponse;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class AppException extends RuntimeException implements Serializable {
    private static final long serialVersionUID = 1L;

    protected AppErrorResponse Error;

    public AppException(String message) {
        Error = new AppErrorResponse.Builder()
                .message(message)
                .errorCode("INTERNAL_SERVER_ERROR")
                .timestamp()
                .isLoggable(true)
                .build();
    }
}
