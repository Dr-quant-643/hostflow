package com.hostflow.property.dto;

import com.hostflow.property.entity.Property;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PropertyResponse(
        UUID id, String name, String description, String propertyType, String rentalModel, String status,
        String addressLine, String city, String country,
        Double latitude, Double longitude, BigDecimal basePrice, Instant manualOccupiedUntil
) {
    public static PropertyResponse from(Property property) {
        return new PropertyResponse(
                property.getId(), property.getName(), property.getDescription(),
                property.getPropertyType().name(), property.getRentalModel().name(), property.getStatus().name(),
                property.getAddressLine(), property.getCity(), property.getCountry(),
                property.getLatitude(), property.getLongitude(), property.getBasePrice(),
                property.getManualOccupiedUntil()
        );
    }
}
