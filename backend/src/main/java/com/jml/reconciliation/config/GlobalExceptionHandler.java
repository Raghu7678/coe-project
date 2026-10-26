package com.jml.reconciliation.config;

import com.jml.reconciliation.dto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Enterprise Global Exception Handler and API Error Boundary.
 * 
 * Intercepts uncaught exceptions across all REST controllers and translates them
 * into standardized {@link ApiErrorResponse} payloads with appropriate HTTP status codes.
 * 
 * Key Responsibilities:
 * - Prevents raw stack trace leakage to API consumers and attackers
 * - Enforces explicit HTTP status mapping for domain validation and dual-control policies
 * - Preserves structured error context for client-side diagnostic displays
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles illegal arguments, including dual-control self-approval violations.
     * Maps to HTTP 400 Bad Request.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        String msg = ex.getMessage();
        String errorCode = (msg != null && msg.contains("Self-approval")) 
                ? "DUAL_CONTROL_SELF_APPROVAL_VIOLATION" 
                : "INVALID_REQUEST_ARGUMENT";

        log.warn("Handled IllegalArgumentException on path [{}]: {} (code: {})", request.getRequestURI(), msg, errorCode);

        ApiErrorResponse response = new ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                msg,
                request.getRequestURI(),
                errorCode
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handles invalid lifecycle states (e.g. attempting to execute an unapproved remediation action).
     * Maps to HTTP 409 Conflict.
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalState(IllegalStateException ex, HttpServletRequest request) {
        String msg = ex.getMessage();
        log.warn("Handled IllegalStateException on path [{}]: {}", request.getRequestURI(), msg);

        ApiErrorResponse response = new ApiErrorResponse(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                msg,
                request.getRequestURI(),
                "INVALID_LIFECYCLE_STATE"
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    /**
     * Handles resource lookup failures (missing employee, remediation action, etc.).
     * Maps to HTTP 404 Not Found.
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(NoSuchElementException ex, HttpServletRequest request) {
        String msg = ex.getMessage();
        log.warn("Handled NoSuchElementException on path [{}]: {}", request.getRequestURI(), msg);

        ApiErrorResponse response = new ApiErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                msg != null ? msg : "Requested resource was not found.",
                request.getRequestURI(),
                "RESOURCE_NOT_FOUND"
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * Handles Bean Validation errors on @Valid request bodies.
     * Maps to HTTP 422 Unprocessable Entity with field-level error mappings.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        log.warn("Handled MethodArgumentNotValidException on path [{}]: {} validation errors", request.getRequestURI(), errors.size());

        ApiErrorResponse response = new ApiErrorResponse(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                HttpStatus.UNPROCESSABLE_ENTITY.getReasonPhrase(),
                "Validation failed for one or more request fields.",
                request.getRequestURI(),
                "VALIDATION_FAILED",
                errors
        );
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
    }

    /**
     * Global catch-all handler for unexpected runtime or internal exceptions.
     * Maps to HTTP 500 Internal Server Error, logging the complete stack trace internally
     * while returning a safe sanitized error message to the client.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Unhandled internal server exception on path [{}]:", request.getRequestURI(), ex);

        ApiErrorResponse response = new ApiErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "An unexpected internal server error occurred while processing the request. Reference path: " + request.getRequestURI(),
                request.getRequestURI(),
                "INTERNAL_SERVER_ERROR"
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
