package com.example.taskapi.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standardized error response following RFC 7807 Problem Details for HTTP APIs
 * Provides consistent error structure across the entire application
 */

@Setter
@Getter
public class AppErrorResponse {
                String message;
                int status;
                String errorCode;
                String path;
                LocalDateTime timestamp;
                Map<String, String> fieldErrors;
                Boolean isLoggable;

        private AppErrorResponse(String message, int status, String errorCode, String path, LocalDateTime timestamp, Map<String, String> fieldErrors, Boolean isLoggable) {
        }

        //builder pattern for easier instantiation
        public static class Builder {
                private String message;
                private int status;
                private String errorCode;
                private String path;
                private LocalDateTime timestamp;
                private Map<String, String> fieldErrors;
                private Boolean isLoggable;

                public Builder message(String message) {
                        this.message = message;
                        return this;
                }

                public Builder status(int status) {
                        this.status = status;
                        return this;
                }

                public Builder errorCode(String errorCode) {
                        this.errorCode = errorCode;
                        return this;
                }

                public Builder path(String path) {
                        this.path = path;
                        return this;
                }

                public Builder timestamp() {
                        this.timestamp = LocalDateTime.now();
                        return this;
                }

                public Builder fieldErrors(Map<String, String> fieldErrors) {
                        this.fieldErrors = fieldErrors;
                        return this;
                }
                public Builder isLoggable(Boolean isLoggable) {
                        return this;
                }

                public AppErrorResponse build() {
                        return new AppErrorResponse(message, status, errorCode, path, timestamp, fieldErrors,isLoggable);
                }

        }

}
