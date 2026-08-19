package com.hostflow.security.product;

import com.hostflow.common.exception.ApplicationException;
import com.hostflow.common.exception.ErrorCode;

import java.util.Set;

public class ProductAccessDeniedException extends ApplicationException {

    private static final String DEFAULT_MESSAGE = "Access denied due to missing or insufficient product scope";

    public ProductAccessDeniedException(ProductScope required) {
        super(ErrorCode.FORBIDDEN,
                String.format("This action requires access to %s, which was not found in the current token", required));
    }

    public ProductAccessDeniedException(Set<ProductScope> requiredScopes) {
        super(ErrorCode.FORBIDDEN,
                String.format("This action requires access to one of: %s, which was not found in the current token",
                        String.join(", ", requiredScopes.stream()
                                .map(Enum::name)
                                .sorted()
                                .toArray(String[]::new))));
    }

    public ProductAccessDeniedException(ProductScope required, Throwable cause) {
        super(ErrorCode.FORBIDDEN,
                String.format("This action requires access to %s, which was not found in the current token", required),
                cause);
    }

    public ProductAccessDeniedException(String message) {
        super(ErrorCode.FORBIDDEN, message);
    }

    public ProductAccessDeniedException(String message, Throwable cause) {
        super(ErrorCode.FORBIDDEN, message, cause);
    }

    public ProductAccessDeniedException() {
        super(ErrorCode.FORBIDDEN, DEFAULT_MESSAGE);
    }

    public ProductAccessDeniedException(Throwable cause) {
        super(ErrorCode.FORBIDDEN, DEFAULT_MESSAGE, cause);
    }
}
