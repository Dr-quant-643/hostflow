package com.hostflow.app.publicapi;

import com.hostflow.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/rental/portal")
@PreAuthorize("hasAuthority('PRODUCT_NAZILCO')")
public class RentalPortalController {

    private final RentalPortalQueries queries;
    private final RentalTenantLinkOrchestrator linkOrchestrator;

    public RentalPortalController(RentalPortalQueries queries, RentalTenantLinkOrchestrator linkOrchestrator) {
        this.queries = queries;
        this.linkOrchestrator = linkOrchestrator;
    }

    /**
     * A rental tenant record starts unlinked (created by landlord staff with no
     * user attached). This lets the signed-in guest claim it by email match, after
     * which my-leases/my-rent-schedule stop returning empty for them.
     */
    @PostMapping("/link")
    public ResponseEntity<ApiResponse<Void>> linkMyAccount(@AuthenticationPrincipal Jwt jwt) {
        UUID guestUserId = UUID.fromString(jwt.getSubject());
        String email = jwt.getClaimAsString("email");
        linkOrchestrator.linkMyAccount(guestUserId, email);
        return ResponseEntity.ok(ApiResponse.<Void>success(null));
    }

    @GetMapping("/my-leases")
    public ResponseEntity<ApiResponse<List<RentalPortalQueries.MyLeaseRow>>> myLeases(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(ApiResponse.success(queries.myLeases(UUID.fromString(jwt.getSubject()))));
    }

    @GetMapping("/my-rent-schedule")
    public ResponseEntity<ApiResponse<List<RentalPortalQueries.MyRentPaymentRow>>> myRentSchedule(
            @AuthenticationPrincipal Jwt jwt, @RequestParam UUID leaseId) {
        return ResponseEntity.ok(ApiResponse.success(queries.myRentSchedule(UUID.fromString(jwt.getSubject()), leaseId)));
    }
}
