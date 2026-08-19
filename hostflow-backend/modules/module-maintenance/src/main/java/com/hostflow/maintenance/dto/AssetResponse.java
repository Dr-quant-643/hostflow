package com.hostflow.maintenance.dto;

import com.hostflow.maintenance.entity.Asset;

import java.time.LocalDate;
import java.util.UUID;

public record AssetResponse(UUID id, String name, String category, String serialNumber,
                             LocalDate warrantyExpiryDate, boolean underWarranty) {
    public static AssetResponse from(Asset a) {
        return new AssetResponse(a.getId(), a.getName(), a.getCategory(), a.getSerialNumber(),
                a.getWarrantyExpiryDate(), a.isUnderWarranty());
    }
}
