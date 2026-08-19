package com.hostflow.common.exception;

public class TenantContextMissingException extends ApplicationException {

    private static final String DEFAULT_MESSAGE = "No tenant context found. Ensure TenantContext.set() is called before accessing tenant-scoped data.";

    public TenantContextMissingException() {
        super(ErrorCode.TENANT_CONTEXT_MISSING, DEFAULT_MESSAGE);
    }

    public TenantContextMissingException(String message) {
        super(ErrorCode.TENANT_CONTEXT_MISSING, message != null ? message : DEFAULT_MESSAGE);
    }

    public TenantContextMissingException(String message, Throwable cause) {
        super(ErrorCode.TENANT_CONTEXT_MISSING, message != null ? message : DEFAULT_MESSAGE, cause);
    }

    public TenantContextMissingException(Throwable cause) {
        super(ErrorCode.TENANT_CONTEXT_MISSING, DEFAULT_MESSAGE, cause);
    }
}
