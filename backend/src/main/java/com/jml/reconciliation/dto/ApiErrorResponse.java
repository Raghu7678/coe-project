package com.jml.reconciliation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standardized API Error Envelope for REST endpoints.
 * 
 * Complies with RFC 7807 (Problem Details for HTTP APIs) principles,
 * ensuring consistent error telemetry across all backend services,
 * dual-control approval gate violations, and input validation failures.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiErrorResponse {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private String errorCode;
    private Map<String, String> validationErrors;

    public ApiErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ApiErrorResponse(int status, String error, String message, String path, String errorCode) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.errorCode = errorCode;
    }

    public ApiErrorResponse(int status, String error, String message, String path, String errorCode, Map<String, String> validationErrors) {
        this(status, error, message, path, errorCode);
        this.validationErrors = validationErrors;
    }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }

    public Map<String, String> getValidationErrors() { return validationErrors; }
    public void setValidationErrors(Map<String, String> validationErrors) { this.validationErrors = validationErrors; }
}
