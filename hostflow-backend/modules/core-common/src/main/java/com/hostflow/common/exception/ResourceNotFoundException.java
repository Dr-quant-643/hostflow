package com.hostflow.common.exception;

public class ResourceNotFoundException extends ApplicationException {

    public ResourceNotFoundException(String resourceName, Object identifier) {
        super(ErrorCode.RESOURCE_NOT_FOUND,
                String.format("%s with identifier '%s' was not found", resourceName, identifier));
    }
}
