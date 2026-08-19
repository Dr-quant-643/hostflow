package com.hostflow.mall.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ParkingEntryRequest(@NotNull UUID propertyId, @NotBlank String vehiclePlate) {
}
