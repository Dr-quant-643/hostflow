package com.hostflow.common.response;

import com.hostflow.common.exception.ErrorCode;

public class ApiError {

    private final String code;
    private final String message;

    public ApiError(ErrorCode errorCode, String message) {
        this.code = errorCode.getCode();
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
