package com.hostflow.identity.dto;

import com.hostflow.identity.entity.ApiKey;

import java.time.Instant;
import java.util.UUID;

public record ApiKeyResponse(UUID id, String name, String keyPrefix, Instant lastUsedAt, boolean revoked) {
    public static ApiKeyResponse from(ApiKey key) {
        return new ApiKeyResponse(key.getId(), key.getName(), key.getKeyPrefix(), key.getLastUsedAt(), key.isRevoked());
    }
}
