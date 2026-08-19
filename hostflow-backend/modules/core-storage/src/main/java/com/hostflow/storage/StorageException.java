package com.hostflow.storage;

import com.hostflow.common.exception.ApplicationException;
import com.hostflow.common.exception.ErrorCode;

public class StorageException extends ApplicationException {

    public StorageException(String message, Throwable cause) {
        super(ErrorCode.INTERNAL_ERROR, message, cause);
    }
}
