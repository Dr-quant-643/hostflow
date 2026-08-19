package com.hostflow.app.publicapi;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
public class GuestNotificationQueries {

    private final JdbcTemplate jdbcTemplate;

    public GuestNotificationQueries(@Qualifier("platformAdminJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public record InboxNotificationRow(UUID id, String templateCode, String channel, String status, Instant createdAt) {
    }

    public List<InboxNotificationRow> listMine(UUID recipientUserId, int limit, int offset) {
        String sql = "SELECT id, template_code, channel, status, created_at FROM notification_logs " +
                "WHERE recipient_user_id = ? ORDER BY created_at DESC LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new InboxNotificationRow(
                UUID.fromString(rs.getString("id")), rs.getString("template_code"), rs.getString("channel"),
                rs.getString("status"), rs.getTimestamp("created_at").toInstant()), recipientUserId, limit, offset);
    }
}
