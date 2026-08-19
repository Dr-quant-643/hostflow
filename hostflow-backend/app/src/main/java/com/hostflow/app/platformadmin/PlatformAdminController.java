package com.hostflow.app.platformadmin;

import com.hostflow.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Every endpoint here is hasRole('PLATFORM_ADMIN') ONLY — never PRODUCT_XANUOS
 * or
 * PRODUCT_NAZILCO alone, since those authorities say nothing about cross-tenant
 * access. This is the single controller backing /admin/billing/invoices,
 * /admin/bookings, /organizations (list) — matching the frontend's assumed
 * routes
 * from the reconciliation pass.
 */
@RestController
@PreAuthorize("hasRole('PLATFORM_ADMIN')")
public class PlatformAdminController {

    private final PlatformBillingQueries billingQueries;
    private final PlatformBookingQueries bookingQueries;
    private final PlatformOrganizationQueries organizationQueries;

    public PlatformAdminController(PlatformBillingQueries billingQueries,
            PlatformBookingQueries bookingQueries,
            PlatformOrganizationQueries organizationQueries) {
        this.billingQueries = billingQueries;
        this.bookingQueries = bookingQueries;
        this.organizationQueries = organizationQueries;
    }

    @GetMapping("/api/v1/admin/billing/invoices")
    public ResponseEntity<ApiResponse<List<PlatformBillingQueries.InvoiceSummaryRow>>> invoices(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "50") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        return ResponseEntity.ok(ApiResponse.success(billingQueries.findAllAcrossTenants(status, limit, offset)));
    }

    @GetMapping("/api/v1/admin/bookings")
    public ResponseEntity<ApiResponse<List<PlatformBookingQueries.BookingOversightRow>>> bookings(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "50") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        return ResponseEntity.ok(ApiResponse.success(bookingQueries.findAllAcrossTenants(status, limit, offset)));
    }

    @GetMapping("/api/v1/organizations")
    public ResponseEntity<ApiResponse<List<PlatformOrganizationQueries.OrganizationRow>>> organizations(
            @RequestParam(defaultValue = "50") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        return ResponseEntity.ok(ApiResponse.success(organizationQueries.findAllOrganizations(limit, offset)));
    }

    @GetMapping("/api/v1/admin/platform-users")
    public ResponseEntity<ApiResponse<List<PlatformOrganizationQueries.PlatformUserRow>>> platformUsers(
            @RequestParam(defaultValue = "50") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        return ResponseEntity.ok(ApiResponse.success(organizationQueries.findAllUsersAcrossTenants(limit, offset)));
    }
}
