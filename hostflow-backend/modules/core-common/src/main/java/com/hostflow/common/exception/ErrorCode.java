package com.hostflow.common.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "HF-400", "Bad request"),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "HF-400", "Validation failed"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "HF-401", "Unauthorized"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "HF-403", "Forbidden"),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "HF-404", "Resource not found"),
    CONFLICT(HttpStatus.CONFLICT, "HF-409", "Conflict"),
    PRECONDITION_FAILED(HttpStatus.PRECONDITION_FAILED, "HF-412", "Precondition failed"),
    BUSINESS_RULE_VIOLATION(HttpStatus.UNPROCESSABLE_ENTITY, "HF-422", "Business rule violation"),
    UNPROCESSABLE_ENTITY(HttpStatus.UNPROCESSABLE_ENTITY, "HF-422", "Unprocessable entity"),
    TENANT_CONTEXT_MISSING(HttpStatus.UNAUTHORIZED, "HF-440", "No tenant context found"),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "HF-500", "Internal server error");

    private final HttpStatus status;
    private final String code;
    private final String defaultMessage;

    ErrorCode(HttpStatus status, String code, String defaultMessage) {
        this.status = status;
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}
