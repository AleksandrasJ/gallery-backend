package com.example.controller;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.NoSuchElementException;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class GalleryExceptionHandler{

    private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(NoSuchElementException.class)
    protected ResponseEntity<Object> handleResourceNotFound(NoSuchElementException ex) {
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, "Resource not found", ex);
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    protected ResponseEntity<Object> handleMissingRequestPart(MissingServletRequestPartException ex) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, "Missing request part", ex);
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<Object> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        ApiError apiError = new ApiError(HttpStatus.METHOD_NOT_ALLOWED, "Method not allowed", ex);
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleMethodNotSupported(Exception ex) {
        ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Something happened inside our service", ex);
        return buildResponseEntity(apiError);
    }
}
