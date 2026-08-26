package com.hostflow.app.publicapi;

import com.hostflow.common.exception.BusinessRuleException;
import com.hostflow.common.exception.ResourceNotFoundException;
import com.hostflow.notification.dto.SendNotificationRequest;
import com.hostflow.notification.service.NotificationService;
import com.hostflow.rental.dto.CreateLeaseRequest;
import com.hostflow.rental.dto.CreateRentalTenantRequest;
import com.hostflow.rental.dto.LeaseResponse;
import com.hostflow.rental.entity.Lease;
import com.hostflow.rental.entity.RentalTenant;
import com.hostflow.rental.service.LeaseService;
import com.hostflow.rental.service.RentalTenantService;
import com.hostflow.tenancy.context.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Self-service alternative to RentalInquiryOrchestrator's inquiry flow -- a
 * guest who doesn't want to wait for an owner reply can reserve a MONTHLY
 * property directly instead of inquiring first. Creates a RentalTenant
 * (pre-linked to the guest's own account, unlike the staff-entered ones
 * RentalTenantLinkOrchestrator links after the fact) and an ACTIVE Lease in
 * one step.
 *
 * Deliberately a different trust model from staff-created leases: normally
 * an owner vets a prospective tenant (via an inquiry or their own channels)
 * before creating a Lease themselves through LeaseController. This path
 * skips the up-front inquiry step by design -- inquiry is meant to stay
 * optional support/engagement, not a gate in front of booking, so a guest
 * confident they want a specific unit can request it immediately without
 * waiting on a reply. The Lease still starts DRAFT (not activated) and the
 * owner is notified to approve or decline it -- self-service means "skip the
 * back-and-forth," not "skip the owner's decision entirely."
 */
@Component
public class RentalReservationOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(RentalReservationOrchestrator.class);

    private final JdbcTemplate platformAdminJdbcTemplate;
    private final LeaseService leaseService;
    private final RentalTenantService rentalTenantService;
    private final NotificationService notificationService;

    public RentalReservationOrchestrator(@Qualifier("platformAdminJdbcTemplate") JdbcTemplate platformAdminJdbcTemplate,
            LeaseService leaseService, RentalTenantService rentalTenantService, NotificationService notificationService) {
        this.platformAdminJdbcTemplate = platformAdminJdbcTemplate;
        this.leaseService = leaseService;
        this.rentalTenantService = rentalTenantService;
        this.notificationService = notificationService;
    }

    public LeaseResponse reserve(UUID guestUserId, ReserveRentalRequest request) {
        Map<String, Object> row = resolveMonthlyProperty(request.propertyId());
        UUID tenantId = UUID.fromString(row.get("tenant_id").toString());
        UUID ownerUserId = UUID.fromString(row.get("owner_user_id").toString());
        String propertyName = row.get("name").toString();
        Object priceObj = row.get("base_price");
        if (priceObj == null) {
            throw new BusinessRuleException("This property doesn't have a rate set yet, so it can't be reserved online");
        }
        BigDecimal monthlyRent = new BigDecimal(priceObj.toString());
        LocalDate endDate = request.moveInDate().plusMonths(request.months());

        String[] guestNameEmail = resolveGuestNameAndEmail(guestUserId);

        TenantContext.set(tenantId);
        try {
            RentalTenant tenant = rentalTenantService.create(
                    new CreateRentalTenantRequest(guestNameEmail[0], guestNameEmail[1], null));
            rentalTenantService.linkToUser(tenant.getId(), guestUserId);
            Lease lease = leaseService.create(new CreateLeaseRequest(
                    request.propertyId(), tenant.getId(), request.moveInDate(), endDate, monthlyRent, null));
            notifyOwner(ownerUserId, propertyName, request, monthlyRent);
            return LeaseResponse.from(lease);
        } finally {
            TenantContext.clear();
        }
    }

    /**
     * Best-effort, same reasoning as booking-confirmed emails elsewhere: the
     * Lease is already created and visible to the owner in XanuOS regardless
     * of whether this notification succeeds, so a delivery failure here
     * shouldn't undo (or fail the response for) a reservation that already
     * happened.
     */
    private void notifyOwner(UUID ownerUserId, String propertyName, ReserveRentalRequest request, BigDecimal monthlyRent) {
        try {
            String recipientAddress = resolveEmail(ownerUserId);
            if (recipientAddress == null) {
                return;
            }
            notificationService.send(new SendNotificationRequest(ownerUserId, recipientAddress,
                    "rental_reservation_owner",
                    Map.of("property_name", propertyName, "move_in_date", request.moveInDate().toString(),
                            "months", String.valueOf(request.months()), "monthly_rent", monthlyRent.toString())));
        } catch (Exception e) {
            log.warn("Failed to notify owner {} of new rental reservation: {}", ownerUserId, e.getMessage());
        }
    }

    private Map<String, Object> resolveMonthlyProperty(UUID propertyId) {
        List<Map<String, Object>> rows = platformAdminJdbcTemplate.queryForList(
                "SELECT tenant_id, owner_user_id, name, base_price, rental_model FROM properties WHERE id = ? AND status = 'ACTIVE'",
                propertyId);
        if (rows.isEmpty()) {
            throw new ResourceNotFoundException("Property", propertyId);
        }
        Map<String, Object> row = rows.get(0);
        if (!"MONTHLY".equals(row.get("rental_model"))) {
            throw new BusinessRuleException("This property doesn't accept direct reservations");
        }
        return row;
    }

    private String[] resolveGuestNameAndEmail(UUID guestUserId) {
        List<String[]> rows = platformAdminJdbcTemplate.query(
                "SELECT first_name, last_name, email FROM guest_profiles WHERE keycloak_id = ?",
                (rs, rowNum) -> new String[] {
                        rs.getString("first_name") + " " + rs.getString("last_name"), rs.getString("email") },
                guestUserId.toString());
        if (rows.isEmpty()) {
            throw new ResourceNotFoundException("Guest profile", guestUserId);
        }
        return rows.get(0);
    }

    private String resolveEmail(UUID userId) {
        List<String> userEmail = platformAdminJdbcTemplate.query(
                "SELECT email FROM users WHERE keycloak_id = ?",
                (rs, rowNum) -> rs.getString("email"), userId.toString());
        return userEmail.isEmpty() ? null : userEmail.get(0);
    }
}
