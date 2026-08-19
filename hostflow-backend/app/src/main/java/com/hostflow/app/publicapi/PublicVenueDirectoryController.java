package com.hostflow.app.publicapi;

import com.hostflow.common.response.ApiResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * The "Office/Mall Guest Experience" from the vision doc, in its minimal form:
 * a store/business directory for a mall property, and a meeting room availability
 * list for an office property — both public-readable (matching mall events'
 * public access), since a visitor doesn't need an account to see what's in a mall
 * or which rooms exist.
 */
@RestController
public class PublicVenueDirectoryController {

    private final JdbcTemplate platformAdminJdbcTemplate;

    public PublicVenueDirectoryController(@Qualifier("platformAdminJdbcTemplate") JdbcTemplate platformAdminJdbcTemplate) {
        this.platformAdminJdbcTemplate = platformAdminJdbcTemplate;
    }

    public record StoreDirectoryEntry(String unitNumber, String businessName) {
    }

    public record MeetingRoomEntry(UUID id, String name, Integer capacity) {
    }

    @GetMapping("/api/v1/mall/public/store-directory")
    public ResponseEntity<ApiResponse<List<StoreDirectoryEntry>>> storeDirectory(@RequestParam UUID propertyId) {
        String sql = """
                SELECT ru.unit_number, rt.business_name
                FROM mall_retail_units ru
                JOIN mall_retail_tenants rt ON rt.retail_unit_id = ru.id
                WHERE ru.property_id = ? AND ru.status = 'OCCUPIED'
                ORDER BY ru.unit_number
                """;
        List<StoreDirectoryEntry> entries = platformAdminJdbcTemplate.query(sql,
                (rs, rowNum) -> new StoreDirectoryEntry(rs.getString("unit_number"), rs.getString("business_name")), propertyId);
        return ResponseEntity.ok(ApiResponse.success(entries));
    }

    @GetMapping("/api/v1/office/public/rooms")
    public ResponseEntity<ApiResponse<List<MeetingRoomEntry>>> availableRooms(@RequestParam UUID propertyId) {
        String sql = "SELECT id, name, capacity FROM office_meeting_rooms WHERE property_id = ? AND active = true ORDER BY name";
        List<MeetingRoomEntry> rooms = platformAdminJdbcTemplate.query(sql,
                (rs, rowNum) -> new MeetingRoomEntry(UUID.fromString(rs.getString("id")), rs.getString("name"), rs.getInt("capacity")), propertyId);
        return ResponseEntity.ok(ApiResponse.success(rooms));
    }
}
