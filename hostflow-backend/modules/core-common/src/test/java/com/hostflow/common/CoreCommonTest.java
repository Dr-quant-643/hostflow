package com.hostflow.common;

import com.hostflow.common.exception.BusinessRuleException;
import com.hostflow.common.exception.ErrorCode;
import com.hostflow.common.exception.ResourceNotFoundException;
import com.hostflow.common.response.ApiError;
import com.hostflow.common.response.ApiResponse;
import com.hostflow.common.response.PageResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CoreCommonTest {

    @Test
    void resourceNotFoundException_carriesCorrectErrorCodeAndMessage() {
        assertThatThrownBy(() -> {
            throw new ResourceNotFoundException("Property", "prop-123");
        })
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Property")
                .hasMessageContaining("prop-123");
    }

    @Test
    void businessRuleException_usesProvidedMessage() {
        BusinessRuleException ex = new BusinessRuleException("Booking dates overlap an existing reservation");
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.BUSINESS_RULE_VIOLATION);
        assertThat(ex.getMessage()).isEqualTo("Booking dates overlap an existing reservation");
    }

    @Test
    void apiResponse_success_wrapsDataCorrectly() {
        ApiResponse<String> response = ApiResponse.success("hello");

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData()).isEqualTo("hello");
        assertThat(response.getError()).isNull();
        assertThat(response.getTimestamp()).isNotNull();
    }

    @Test
    void apiResponse_failure_wrapsErrorCorrectly() {
        ApiError error = new ApiError(ErrorCode.VALIDATION_FAILED, "Email is required");
        ApiResponse<Object> response = ApiResponse.failure(error);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getData()).isNull();
        assertThat(response.getError().getCode()).isEqualTo("HF-400");
        assertThat(response.getError().getMessage()).isEqualTo("Email is required");
    }

    @Test
    void pageResponse_calculatesTotalPagesCorrectly() {
        PageResponse<String> page = new PageResponse<>(List.of("a", "b"), 0, 10, 25);

        assertThat(page.getContent()).hasSize(2);
        assertThat(page.getTotalElements()).isEqualTo(25);
        assertThat(page.getTotalPages()).isEqualTo(3);
    }

    @Test
    void pageResponse_handlesZeroPageSizeWithoutDivisionError() {
        PageResponse<String> page = new PageResponse<>(List.of(), 0, 0, 0);
        assertThat(page.getTotalPages()).isEqualTo(0);
    }
}
