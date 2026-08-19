package com.hostflow.mall.dto;

import com.hostflow.mall.entity.RetailTenant;

import java.math.BigDecimal;
import java.util.UUID;

public record RetailTenantResponse(UUID id, UUID retailUnitId, String businessName, BigDecimal monthlyRent) {
    public static RetailTenantResponse from(RetailTenant t) {
        return new RetailTenantResponse(t.getId(), t.getRetailUnitId(), t.getBusinessName(), t.getMonthlyRent());
    }
}
