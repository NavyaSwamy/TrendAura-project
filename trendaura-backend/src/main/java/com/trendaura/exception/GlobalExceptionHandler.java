package com.trendaura.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // =====================================================
    // ⭐ STANDARD ERROR RESPONSE STRUCTURE
    // =====================================================
    private Map<String, Object> buildError(String message, Object details) {

        Map<String, Object> response = new HashMap<>();

        response.put("success", false);
        response.put("message", message);
        response.put("timestamp", Instant.now());
        response.put("traceId", UUID.randomUUID().toString());
        response.put("details", details);

        return response;
    }

    // =====================================================
    // ⭐ RESOURCE NOT FOUND
    // =====================================================
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleNotFound(ResourceNotFoundException e) {

        log.warn("Resource not found -> {}", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(buildError(e.getMessage(), null));
    }

    // =====================================================
    // ⭐ AUTHENTICATION ERROR
    // =====================================================
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleAuth(BadCredentialsException e) {

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(buildError("Invalid credentials", null));
    }

    // =====================================================
    // ⭐ ACCESS DENIED
    // =====================================================
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDenied(AccessDeniedException e) {

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(buildError("Access denied", null));
    }

    // =====================================================
    // ⭐ VALIDATION ERROR HANDLING (PRODUCTION LEVEL)
    // =====================================================
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException e) {

        Map<String, String> errors = new HashMap<>();

        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        return ResponseEntity
                .badRequest()
                .body(buildError("Validation failed", errors));
    }

    // =====================================================
    // ⭐ RUNTIME ERRORS
    // =====================================================
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntime(RuntimeException e) {

        log.error("Runtime Exception", e);

        return ResponseEntity
                .badRequest()
                .body(buildError(
                        e.getMessage() != null ? e.getMessage() : "Unexpected error",
                        null
                ));
    }

    // =====================================================
    // ⭐ GLOBAL FALLBACK ERROR
    // =====================================================
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception e) {

        log.error("Unknown System Error", e);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildError(
                        "Internal server error",
                        null
                ));
    }
}