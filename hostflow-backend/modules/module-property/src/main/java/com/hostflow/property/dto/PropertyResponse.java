package com.hostflow.property.dto;

import com.hostflow.property.entity.Property;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PropertyResponse(
        UUID id, String name, String description, String propertyType, String rentalModel, String status,
        String addressLine, String city, String country,
        Double latitude, Double longitude, BigDecimal basePrice, Instant manualOccupiedUntil,
        Integer totalUnits, Integer occupiedUnits, int occupancyPercent
) {
    public static PropertyResponse from(Property property) {
        int totalUnits = property.getTotalUnits();
        int occupiedUnits = property.getOccupiedUnits();
        return new PropertyResponse(
                property.getId(), property.getName(), property.getDescription(),
                property.getPropertyType().name(), property.getRentalModel().name(), property.getStatus().name(),
                property.getAddressLine(), property.getCity(), property.getCountry(),
                property.getLatitude(), property.getLongitude(), property.getBasePrice(),
                property.getManualOccupiedUntil(),
                totalUnits, occupiedUnits, Math.round(100f * occupiedUnits / totalUnits)
        );
    }
}
