package com.hostflow.identity.entity;

import com.hostflow.tenancy.entity.TenantScopedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * Foundation for the eventual paid public-API product mentioned in planning
 * -- deliberately just the credential + a handful of read-only endpoints for
 * now, no rate limiting/usage metering/billing (that's a distinct, sizable
 * follow-up once this base is in real use). The raw key is shown to the
 * owner exactly once at creation (see ApiKeyService.create) -- only its
 * SHA-256 hash and a display prefix are ever persisted, same principle as a
 * password hash.
 */
@Entity
@Table(name = "api_keys")
public class ApiKey extends TenantScopedEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "key_hash", nullable = false, unique = true)
    private String keyHash;

    @Column(name = "key_prefix", nullable = false)
    private String keyPrefix;

    @Column(name = "last_used_at")
    private Instant lastUsedAt;

    @Column(name = "revoked", nullable = false)
    private boolean revoked;

    protected ApiKey() {
    }

    public ApiKey(String name, String keyHash, String keyPrefix) {
        this.name = name;
        this.keyHash = keyHash;
        this.keyPrefix = keyPrefix;
        this.revoked = false;
    }

    public void markUsed() {
        this.lastUsedAt = Instant.now();
    }

    public void revoke() {
        this.revoked = true;
    }

    public String getName() {
        return name;
    }

    public String getKeyHash() {
        return keyHash;
    }

    public String getKeyPrefix() {
        return keyPrefix;
    }

    public Instant getLastUsedAt() {
        return lastUsedAt;
    }

    public boolean isRevoked() {
        return revoked;
    }
}
