package com.hostflow.rental.dto;

import com.hostflow.rental.entity.Lease;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record LeaseResponse(UUID id, UUID propertyId, UUID tenantIdRef, LocalDate startDate, LocalDate endDate,
                             BigDecimal monthlyRent, BigDecimal securityDeposit, String status, String declineReason) {

    public static LeaseResponse from(Lease lease) {
        return new LeaseResponse(lease.getId(), lease.getPropertyId(), lease.getTenantIdRef(),
                lease.getStartDate(), lease.getEndDate(), lease.getMonthlyRent(),
                lease.getSecurityDeposit(), lease.getStatus().name(), lease.getDeclineReason());
    }
}
