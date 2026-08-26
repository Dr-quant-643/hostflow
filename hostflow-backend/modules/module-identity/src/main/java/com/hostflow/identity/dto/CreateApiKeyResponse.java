package com.hostflow.identity.dto;

import java.util.UUID;

/** The ONLY response that ever carries the raw key -- shown once at
 *  creation, never retrievable again (same principle as a generated
 *  password: only its hash is persisted). */
public record CreateApiKeyResponse(UUID id, String name, String rawKey) {
}
