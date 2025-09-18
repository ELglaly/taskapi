package com.example.taskapi.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standardized error response following RFC 7807 Problem Details for HTTP APIs
 * Provides consistent error structure across the entire application
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppErrorResponse {

        private String message;
        private int status;
        private String errorCode;
        private String path;

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
        private LocalDateTime timestamp;

        private Map<String, String> fieldErrors;

        @JsonIgnore
        private Boolean isLoggable;

        // Private constructor for builder pattern
        private AppErrorResponse(String message, int status, String errorCode, String path,
                                 LocalDateTime timestamp, Map<String, String> fieldErrors, Boolean isLoggable) {
                this.message = message;
                this.status = status;
                this.errorCode = errorCode;
                this.path = path;
                this.timestamp = timestamp;
                this.fieldErrors = fieldErrors;
                this.isLoggable = isLoggable;
        }

        // Static method to create builder
        public static Builder builder() {
                return new Builder();
        }

        // Builder pattern for easier instantiation
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

                public Builder timestamp(LocalDateTime timestamp) {
                        this.timestamp = timestamp;
                        return this;
                }

                public Builder fieldErrors(Map<String, String> fieldErrors) {
                        this.fieldErrors = fieldErrors;
                        return this;
                }

                public Builder isLoggable(Boolean isLoggable) {
                        this.isLoggable = isLoggable;
                        return this;
                }

                public AppErrorResponse build() {
                        return new AppErrorResponse(message, status, errorCode, path, timestamp, fieldErrors, isLoggable);
                }
        }
}
