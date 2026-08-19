package com.hostflow.common.exception;

/**
 * Base unchecked exception for all HostFlow application errors.
 * Module-specific exceptions should extend this rather than RuntimeException
 * directly,
 * so the global exception handler (added in core-config) can catch one common
 * type.
 */
public class ApplicationException extends RuntimeException {

    private final ErrorCode errorCode;

    public ApplicationException(ErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
    }

    public ApplicationException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
    }

    public ApplicationException(ErrorCode errorCode, String customMessage, Throwable cause) {
        super(customMessage, cause);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
