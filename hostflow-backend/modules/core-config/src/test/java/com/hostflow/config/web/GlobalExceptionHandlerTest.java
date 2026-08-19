package com.hostflow.config.web;

import com.hostflow.common.exception.ApplicationException;
import com.hostflow.common.exception.BusinessRuleException;
import com.hostflow.common.exception.ErrorCode;
import com.hostflow.common.exception.ResourceNotFoundException;
import com.hostflow.common.response.ApiResponse;
import com.hostflow.common.exception.TenantContextMissingException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void resourceNotFound_mapsTo404() {
        ResponseEntity<ApiResponse<Void>> response = handler
                .handleApplicationException((ApplicationException) new ResourceNotFoundException("Property", "prop-1"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getError().getCode()).isEqualTo("HF-404");
    }

    @Test
    void businessRuleViolation_mapsTo422() {
        ResponseEntity<ApiResponse<Void>> response = handler
                .handleApplicationException(new BusinessRuleException("Overlapping booking dates"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody().getError().getMessage()).isEqualTo("Overlapping booking dates");
    }

    @Test
    void tenantContextMissing_mapsTo401() {
        ResponseEntity<ApiResponse<Void>> response = handler
                .handleApplicationException(new TenantContextMissingException());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().getError().getCode()).isEqualTo("HF-440");
    }

    @Test
    void unexpectedException_mapsTo500AndHidesInternalDetails() {
        ResponseEntity<ApiResponse<Void>> response = handler
                .handleUnexpectedException(new NullPointerException("something exploded internally"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        // Internal exception message must never leak to the client
        assertThat(response.getBody().getError().getMessage())
                .doesNotContain("something exploded internally")
                .isEqualTo("An unexpected error occurred");
    }

    @Test
    void allErrorCodes_haveAnHttpStatusMapping_noExceptionThrown() {
        for (ErrorCode code : ErrorCode.values()) {
            ResponseEntity<ApiResponse<Void>> response = handler
                    .handleApplicationException(new com.hostflow.common.exception.ApplicationException(code));
            assertThat(response.getStatusCode()).isNotNull();
        }
    }
}
