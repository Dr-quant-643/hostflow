package com.hostflow.app.publicapi;

import com.hostflow.common.exception.BusinessRuleException;
import com.hostflow.common.exception.ResourceNotFoundException;
import com.hostflow.rental.service.RentalTenantService;
import com.hostflow.tenancy.context.TenantContext;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Closes the gap flagged in PROJECT_STATE.md: RentalTenant.linkToUser() existed
 * with nothing calling it. A rental tenant is created by landlord staff under
 * their own tenant with no user attached (linkedUserId nullable by design). This
 * lets the actual tenant, once they hold a NazilCo guest account, claim that
 * record by matching on the email the landlord entered — resolved cross-tenant
 * (platformAdminJdbcTemplate) since the guest performing the link has no tenant
 * of their own, same pattern as GuestBookingOrchestrator/GuestReviewOrchestrator.
 */
@Component
public class RentalTenantLinkOrchestrator {

    private final JdbcTemplate platformAdminJdbcTemplate;
    private final RentalTenantService rentalTenantService;

    public RentalTenantLinkOrchestrator(@Qualifier("platformAdminJdbcTemplate") JdbcTemplate platformAdminJdbcTemplate,
            RentalTenantService rentalTenantService) {
        this.platformAdminJdbcTemplate = platformAdminJdbcTemplate;
        this.rentalTenantService = rentalTenantService;
    }

    public void linkMyAccount(UUID guestUserId, String guestEmail) {
        if (guestEmail == null || guestEmail.isBlank()) {
            throw new BusinessRuleException("No verified email on this account to match against");
        }

        if (alreadyLinked(guestUserId)) {
            return;
        }

        Match match = resolveUnlinkedMatch(guestEmail);

        TenantContext.set(match.tenantId());
        try {
            rentalTenantService.linkToUser(match.rentalTenantId(), guestUserId);
        } finally {
            TenantContext.clear();
        }
    }

    private record Match(UUID tenantId, UUID rentalTenantId) {
    }

    private boolean alreadyLinked(UUID guestUserId) {
        List<UUID> rows = platformAdminJdbcTemplate.query(
                "SELECT id FROM rental_tenants WHERE linked_user_id = ?",
                (rs, rowNum) -> UUID.fromString(rs.getString("id")), guestUserId);
        return !rows.isEmpty();
    }

    private Match resolveUnlinkedMatch(String guestEmail) {
        List<Match> rows = platformAdminJdbcTemplate.query(
                "SELECT tenant_id, id FROM rental_tenants WHERE linked_user_id IS NULL AND lower(email) = lower(?)",
                (rs, rowNum) -> new Match(
                        UUID.fromString(rs.getString("tenant_id")), UUID.fromString(rs.getString("id"))),
                guestEmail);
        if (rows.isEmpty()) {
            throw new ResourceNotFoundException("RentalTenant matching email", guestEmail);
        }
        return rows.get(0);
    }
}
