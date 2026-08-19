package com.hostflow.identity.keycloak;

import com.hostflow.common.exception.ApplicationException;
import com.hostflow.common.exception.ErrorCode;

public class KeycloakProvisioningException extends ApplicationException {

    public KeycloakProvisioningException(String message, Throwable cause) {
        super(ErrorCode.INTERNAL_ERROR, message, cause);
    }
}
