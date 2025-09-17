package com.example.taskapi.exception;


import com.example.taskapi.response.ApiResponse;
import com.example.taskapi.response.AppErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @Value("${app.security.hide-error-details}")
    private boolean hideErrorDetails;


    @ExceptionHandler(AppException.class)
    public ResponseEntity<AppErrorResponse> handleGlobalException(AppException ex, HttpServletRequest request) {
        AppErrorResponse errorResponse = ex.getError();
        if (hideErrorDetails) {
            errorResponse.setMessage("An error occurred. Please contact support.");
        }

        errorResponse.setPath(request.getRequestURI());

        if (errorResponse.getIsLoggable() == null || errorResponse.getIsLoggable()) {
            log.error("AppException: {}", ex.getMessage(), ex);
        }

        return new ResponseEntity<>(errorResponse,
                ex.getError().getStatus() != 0 ? HttpStatus.valueOf(ex.getError().getStatus())
                        : HttpStatus.INTERNAL_SERVER_ERROR);

    }
}
