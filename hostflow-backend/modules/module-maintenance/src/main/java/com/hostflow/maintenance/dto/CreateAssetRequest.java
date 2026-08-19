package com.hostflow.maintenance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record CreateAssetRequest(@NotNull UUID propertyId, @NotBlank String name, String category,
                                  String serialNumber, LocalDate purchaseDate, LocalDate warrantyExpiryDate) {
}
