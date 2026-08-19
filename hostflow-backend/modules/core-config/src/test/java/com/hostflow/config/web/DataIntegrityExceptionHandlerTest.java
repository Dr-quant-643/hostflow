package com.hostflow.config.web;

import com.hostflow.common.response.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class DataIntegrityExceptionHandlerTest {

    private final DataIntegrityExceptionHandler handler = new DataIntegrityExceptionHandler();

    @Test
    void bookingOverlapConstraintViolation_mapsToCleanConflictMessage() {
        RuntimeException rootCause = new RuntimeException(
                "duplicate key value violates exclusion constraint \"excl_bookings_no_overlap\"");
        DataIntegrityViolationException ex = new DataIntegrityViolationException("insert failed", rootCause);

        ResponseEntity<ApiResponse<Void>> response = handler.handleDataIntegrityViolation(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().getError().getMessage()).isEqualTo("Booking dates overlap an existing reservation");
    }

    @Test
    void genericConstraintViolation_getsGenericMessage_notRawSqlText() {
        RuntimeException rootCause = new RuntimeException("duplicate key value violates unique constraint \"uq_something\"");
        DataIntegrityViolationException ex = new DataIntegrityViolationException("insert failed", rootCause);

        ResponseEntity<ApiResponse<Void>> response = handler.handleDataIntegrityViolation(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().getError().getMessage()).doesNotContain("uq_something");
    }
}
