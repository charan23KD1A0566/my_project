package com.project.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.fasterxml.jackson.databind.JsonMappingException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        e.printStackTrace();
        System.out.println("💥 Exception caught: " + e.getClass().getName() + " - " + e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            "Error: " + e.getClass().getSimpleName() + " - " + e.getMessage()
        );
    }

    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadable(org.springframework.http.converter.HttpMessageNotReadableException e) {
        System.out.println("💥 JSON Parse Error: " + e.getMessage());
        Throwable cause = e.getRootCause();
        if (cause != null) {
            System.out.println("   Root cause: " + cause.getClass().getName() + " - " + cause.getMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            "Invalid JSON: " + (cause != null ? cause.getMessage() : e.getMessage())
        );
    }
}
