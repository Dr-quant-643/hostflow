package com.hostflow.property.dto;

import com.hostflow.property.entity.PropertyType;
import com.hostflow.property.entity.RentalModel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreatePropertyRequest(
        @NotBlank String name,
        @NotNull PropertyType propertyType,
        @NotNull RentalModel rentalModel,
        @NotBlank String addressLine,
        @NotBlank String city,
        @NotBlank String country
) {
}
