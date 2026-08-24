package com.hostflow.app.publicapi;

import com.hostflow.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Same auth boundary as GuestBookingController: an inquiry, like a booking,
 * comes from a signed-in NazilCo guest, not an anonymous visitor.
 */
@RestController
@RequestMapping("/api/v1/rental/inquiries")
@PreAuthorize("hasAuthority('PRODUCT_NAZILCO')")
public class RentalInquiryController {

    private final RentalInquiryOrchestrator orchestrator;

    public RentalInquiryController(RentalInquiryOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> send(@Valid @RequestBody RentalInquiryRequest request) {
        orchestrator.send(request);
        return ResponseEntity.ok(ApiResponse.<Void>success(null));
    }
}
