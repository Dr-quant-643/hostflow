package com.hostflow.app.publicapi;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record RentalInquiryRequest(@NotNull UUID propertyId, String message) {
}
