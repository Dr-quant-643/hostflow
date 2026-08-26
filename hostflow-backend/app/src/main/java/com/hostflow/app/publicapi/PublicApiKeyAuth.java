package com.hostflow.app.publicapi;

import com.hostflow.identity.service.ApiKeyService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

/**
 * Resolves which tenant an incoming X-Api-Key header belongs to -- a raw key
 * carries no tenant of its own (same "no tenant of their own" situation as
 * every guest-facing endpoint), so this is a cross-tenant, RLS-bypassing
 * lookup by design, same platformAdminJdbcTemplate pattern used throughout
 * app/publicapi. Hashes the presented key with the exact same algorithm
 * ApiKeyService used to store it (SHA-256, never compares raw keys).
 */
@Component
public class PublicApiKeyAuth {

    private final JdbcTemplate platformAdminJdbcTemplate;

    public PublicApiKeyAuth(@Qualifier("platformAdminJdbcTemplate") JdbcTemplate platformAdminJdbcTemplate) {
        this.platformAdminJdbcTemplate = platformAdminJdbcTemplate;
    }

    public UUID resolveTenant(String rawKey) {
        if (rawKey == null || rawKey.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing X-Api-Key header");
        }
        String hash = ApiKeyService.hash(rawKey);
        List<Object[]> rows = platformAdminJdbcTemplate.query(
                "SELECT id, tenant_id, revoked FROM api_keys WHERE key_hash = ?",
                (rs, rowNum) -> new Object[] { UUID.fromString(rs.getString("id")),
                        UUID.fromString(rs.getString("tenant_id")), rs.getBoolean("revoked") },
                hash);
        if (rows.isEmpty() || (Boolean) rows.get(0)[2]) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or revoked API key");
        }
        UUID keyId = (UUID) rows.get(0)[0];
        platformAdminJdbcTemplate.update("UPDATE api_keys SET last_used_at = now() WHERE id = ?", keyId);
        return (UUID) rows.get(0)[1];
    }
}
