package com.hostflow.config.web;

import com.hostflow.common.exception.ErrorCode;
import com.hostflow.common.response.ApiError;
import com.hostflow.common.response.ApiResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Catches DataIntegrityViolationException specifically (Spring's wrapper around
 * any DB constraint violation — unique, check, exclusion). This is the backstop
 * translation layer: if a race condition slips past application-level checks
 * (e.g. the booking overlap EXCLUDE constraint from V23, or any future unique/
 * check constraint anywhere else in the schema) and Postgres rejects the write,
 * the caller still gets a clean 409 Conflict with a readable message instead of
 * a raw 500 with a leaked SQL exception message.
 *
 * Registered as a SEPARATE @RestControllerAdvice from GlobalExceptionHandler
 * (not merged into it) since this handles a different exception FAMILY
 * (Spring's DAO layer) than ApplicationException (this codebase's own hierarchy)
 * — Spring dispatches to whichever @ExceptionHandler most specifically matches
 * the thrown type, so both coexist correctly without conflict.
 */
@RestControllerAdvice
public class DataIntegrityExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String message = "This request conflicts with existing data";
        String rootMessage = ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : "";

        if (rootMessage != null && rootMessage.contains("excl_bookings_no_overlap")) {
            message = "Booking dates overlap an existing reservation";
        }

        ApiError error = new ApiError(ErrorCode.CONFLICT, message);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.failure(error));
    }
}