package com.example.controller;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ApiError {
    private HttpStatus status;
    private LocalDateTime timestamp = LocalDateTime.now();
    private String message;
    private String debugMessage;

    ApiError(HttpStatus status, String message, Throwable exception) {
        this.status = status;
        this.message = message;
        this.debugMessage = exception.getLocalizedMessage();
    }

}
