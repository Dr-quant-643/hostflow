package com.hostflow.app.publicapi;

import com.hostflow.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Owner-facing global list across ALL of their properties -- same split as
 * RentalInquiryOwnerController alongside module-rental's own controller:
 * WorkOrderController (module-maintenance) stays per-property, this adds the
 * cross-property view the Maintenance tab actually needs to default to.
 */
@RestController
@RequestMapping("/api/v1/maintenance/work-orders")
@PreAuthorize("hasAuthority('PRODUCT_XANUOS')")
public class OwnerMaintenanceController {

    private final OwnerWorkOrderQueries queries;

    public OwnerMaintenanceController(OwnerWorkOrderQueries queries) {
        this.queries = queries;
    }

    @GetMapping("/mine-as-owner")
    public ResponseEntity<ApiResponse<List<OwnerWorkOrderQueries.OwnerWorkOrderRow>>> mineAsOwner(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "50") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        return ResponseEntity.ok(ApiResponse.success(
                queries.mineAsOwner(UUID.fromString(jwt.getSubject()), limit, offset)));
    }
}
