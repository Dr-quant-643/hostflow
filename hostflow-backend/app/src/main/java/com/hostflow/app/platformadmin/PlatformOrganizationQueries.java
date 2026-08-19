package com.hostflow.app.platformadmin;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** Backs xanuos-console's Organizations and Platform Users screens. */
@Component
public class PlatformOrganizationQueries {

    private final JdbcTemplate jdbcTemplate;

    public PlatformOrganizationQueries(@Qualifier("platformAdminJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public record OrganizationRow(UUID id, String name, String slug, String primaryProduct,
                                   boolean active, Instant createdAt) {
    }

    public record PlatformUserRow(UUID id, UUID tenantId, String organizationName, String email,
                                   String firstName, String lastName, boolean active) {
    }

    private static final RowMapper<OrganizationRow> ORG_ROW_MAPPER = (rs, rowNum) -> new OrganizationRow(
            UUID.fromString(rs.getString("id")), rs.getString("name"), rs.getString("slug"),
            rs.getString("primary_product"), rs.getBoolean("active"), rs.getTimestamp("created_at").toInstant()
    );

    private static final RowMapper<PlatformUserRow> USER_ROW_MAPPER = (rs, rowNum) -> new PlatformUserRow(
            UUID.fromString(rs.getString("id")), UUID.fromString(rs.getString("tenant_id")),
            rs.getString("organization_name"), rs.getString("email"), rs.getString("first_name"),
            rs.getString("last_name"), rs.getBoolean("active")
    );

    public List<OrganizationRow> findAllOrganizations(int limit, int offset) {
        String sql = "SELECT id, name, slug, primary_product, active, created_at FROM organizations " +
                "ORDER BY created_at DESC LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, ORG_ROW_MAPPER, limit, offset);
    }

    public List<PlatformUserRow> findAllUsersAcrossTenants(int limit, int offset) {
        String sql = """
                SELECT u.id, u.tenant_id, o.name AS organization_name, u.email, u.first_name, u.last_name, u.active
                FROM users u
                JOIN organizations o ON o.id = u.tenant_id
                ORDER BY u.created_at DESC
                LIMIT ? OFFSET ?
                """;
        return jdbcTemplate.query(sql, USER_ROW_MAPPER, limit, offset);
    }
}
