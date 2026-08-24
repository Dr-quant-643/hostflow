package com.hostflow.app.publicapi;

import com.hostflow.common.response.ApiResponse;
import com.hostflow.rental.dto.RentalInquiryResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    @PatchMapping("/{id}/reply")
    public ResponseEntity<ApiResponse<RentalInquiryResponse>> reply(@PathVariable UUID id,
            @Valid @RequestBody ReplyToInquiryRequest request) {
        return ResponseEntity.ok(ApiResponse.success(RentalInquiryResponse.from(orchestrator.reply(id, request.message()))));
    }
}
