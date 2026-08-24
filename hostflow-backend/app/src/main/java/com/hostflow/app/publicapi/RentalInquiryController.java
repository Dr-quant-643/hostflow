package com.hostflow.app.publicapi;

import com.hostflow.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Same auth boundary as GuestBookingController: an inquiry, like a booking,
 * comes from a signed-in NazilCo guest, not an anonymous visitor. The
 * owner-facing counterpart (list-by-property, reply) lives in
 * RentalInquiryOwnerController -- separate class since it needs a different
 * authority (PRODUCT_XANUOS) and this class's PRODUCT_NAZILCO gate applies
 * to every method here.
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
    public ResponseEntity<ApiResponse<Void>> send(@Valid @RequestBody RentalInquiryRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        orchestrator.send(UUID.fromString(jwt.getSubject()), request);
        return ResponseEntity.ok(ApiResponse.<Void>success(null));
    }

    @GetMapping("/mine")
    public ResponseEntity<ApiResponse<List<RentalInquiryOrchestrator.MyRentalInquiryRow>>> mine(
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(ApiResponse.success(orchestrator.myInquiries(UUID.fromString(jwt.getSubject()))));
    }
}
