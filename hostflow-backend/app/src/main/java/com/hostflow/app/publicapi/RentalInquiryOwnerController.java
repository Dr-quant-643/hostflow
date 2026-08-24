package com.hostflow.app.publicapi;

import com.hostflow.common.response.ApiResponse;
import com.hostflow.rental.dto.RentalInquiryResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Owner-facing counterpart to RentalInquiryController -- separate class
 * (rather than mixing authorities on one controller) since PRODUCT_XANUOS
 * here vs PRODUCT_NAZILCO there apply at the class level. Lives in
 * app/publicapi rather than module-rental because reply() needs the
 * cross-tenant guest-notification machinery RentalInquiryOrchestrator
 * already has (module-rental's own services are deliberately tenant-scoped
 * only, same as LeaseService).
 */
@RestController
@RequestMapping("/api/v1/rental/inquiries")
@PreAuthorize("hasAuthority('PRODUCT_XANUOS')")
public class RentalInquiryOwnerController {

    private final RentalInquiryOrchestrator orchestrator;

    public RentalInquiryOwnerController(RentalInquiryOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<RentalInquiryResponse>>> listByProperty(
            @RequestParam UUID propertyId,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        return ResponseEntity.ok(ApiResponse.success(
                orchestrator.listForProperty(propertyId, limit, offset).map(RentalInquiryResponse::from)));
    }

    // Global FIFO queue across all of the owner's properties -- backs the
    // Notifications tab so an owner has one place to see every open/replied
    // inquiry in the order it came in, instead of checking property-by-property.
    @GetMapping("/mine-as-owner")
    public ResponseEntity<ApiResponse<List<RentalInquiryOrchestrator.MyRentalInquiryRow>>> mineAsOwner(
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(ApiResponse.success(orchestrator.myInquiriesAsOwner(UUID.fromString(jwt.getSubject()))));
    }

    @PatchMapping("/{id}/reply")
    public ResponseEntity<ApiResponse<RentalInquiryResponse>> reply(@PathVariable UUID id,
            @Valid @RequestBody ReplyToInquiryRequest request) {
        return ResponseEntity.ok(ApiResponse.success(RentalInquiryResponse.from(orchestrator.reply(id, request.message()))));
    }
}
