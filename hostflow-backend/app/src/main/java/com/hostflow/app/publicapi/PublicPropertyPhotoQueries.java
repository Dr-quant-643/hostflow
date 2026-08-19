package com.hostflow.app.publicapi;

import com.hostflow.storage.StorageService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Joins property_documents to properties, restricted to status='ACTIVE' — a
 * guest
 * can only ever see photos for a property that's actually publicly listed, same
 * fail-safe restriction as PublicPropertyQueries.findPublicById. Uses
 * platformAdminJdbcTemplate for the same reason as the rest of the public API:
 * no tenant context exists for an anonymous request.
 */
@Component
public class PublicPropertyPhotoQueries {

    private final JdbcTemplate jdbcTemplate;
    private final StorageService storageService;

    public PublicPropertyPhotoQueries(@Qualifier("platformAdminJdbcTemplate") JdbcTemplate jdbcTemplate,
            StorageService storageService) {
        this.jdbcTemplate = jdbcTemplate;
        this.storageService = storageService;
    }

    public List<String> listPhotoUrls(UUID propertyId) {
        String sql = """
                SELECT pd.object_key FROM property_documents pd
                JOIN properties p ON p.id = pd.property_id
                WHERE pd.property_id = ? AND pd.document_type = 'PHOTO' AND p.status = 'ACTIVE'
                ORDER BY pd.created_at ASC
                """;
        List<String> objectKeys = jdbcTemplate.query(sql,
                (rs, rowNum) -> rs.getString("object_key"), propertyId);

        return objectKeys.stream().map(storageService::generatePresignedGetUrl).toList();
    }
}
