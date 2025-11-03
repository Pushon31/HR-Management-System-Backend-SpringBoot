package com.garmentmanagement.garmentmanagement.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        Map<String, Object> errorResponse = new HashMap<>();

        if (ex.getMessage().contains("PAYROLL_ALREADY_EXISTS")) {
            errorResponse.put("error", "DUPLICATE_PAYROLL");
            errorResponse.put("message", ex.getMessage().replace("PAYROLL_ALREADY_EXISTS: ", ""));
            errorResponse.put("status", HttpStatus.CONFLICT.value());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        } else if (ex.getMessage().contains("Employee not found") ||
                ex.getMessage().contains("Salary structure not found")) {
            errorResponse.put("error", "RESOURCE_NOT_FOUND");
            errorResponse.put("message", ex.getMessage());
            errorResponse.put("status", HttpStatus.NOT_FOUND.value());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } else {
            errorResponse.put("error", "PROCESSING_ERROR");
            errorResponse.put("message", ex.getMessage());
            errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
}