package com.hostflow.app.publicapi;

import jakarta.validation.constraints.NotBlank;

public record ReplyToInquiryRequest(@NotBlank String message) {
}
