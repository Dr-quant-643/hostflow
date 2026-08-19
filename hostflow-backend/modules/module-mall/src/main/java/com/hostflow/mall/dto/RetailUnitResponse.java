package com.hostflow.mall.dto;

import com.hostflow.mall.entity.RetailUnit;

import java.math.BigDecimal;
import java.util.UUID;

public record RetailUnitResponse(UUID id, String unitNumber, BigDecimal sizeSqm, String status) {
    public static RetailUnitResponse from(RetailUnit unit) {
        return new RetailUnitResponse(unit.getId(), unit.getUnitNumber(), unit.getSizeSqm(), unit.getStatus().name());
    }
}
