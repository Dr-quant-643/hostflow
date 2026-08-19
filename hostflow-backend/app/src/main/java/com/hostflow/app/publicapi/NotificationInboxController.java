package com.hostflow.app.publicapi;

import com.hostflow.common.response.ApiResponse;
import com.hostflow.notification.repository.NotificationLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Lives in app (not module-notification) because it needs BOTH access paths:
 * XANUOS staff use the normal RLS-scoped NotificationLogRepository (tenant
 * resolved from their JWT as usual); NAZILCO guests (no tenant) use
 * GuestNotificationQueries' cross-tenant read. Routing between the two is decided
 * by which PRODUCT_ authority the caller's token carries.
 */
@RestController
@PreAuthorize("isAuthenticated()")
public class NotificationInboxController {

    private final NotificationLogRepository notificationLogRepository;
    private final GuestNotificationQueries guestNotificationQueries;

    public NotificationInboxController(NotificationLogRepository notificationLogRepository,
                                        GuestNotificationQueries guestNotificationQueries) {
        this.notificationLogRepository = notificationLogRepository;
        this.guestNotificationQueries = guestNotificationQueries;
    }

    @GetMapping("/api/v1/notifications/mine")
    public ResponseEntity<ApiResponse<?>> mine(@AuthenticationPrincipal Jwt jwt,
                                                @RequestParam(defaultValue = "20") int limit,
                                                @RequestParam(defaultValue = "0") int offset) {
        UUID userId = UUID.fromString(jwt.getSubject());
        List<String> productScope = jwt.getClaimAsStringList("product_scope");

        if (productScope != null && productScope.contains("NAZILCO") && jwt.getClaimAsString("tenant_id") == null) {
            return ResponseEntity.ok(ApiResponse.success(
                    guestNotificationQueries.listMine(userId, limit, offset)));
        }

        Page<?> page = notificationLogRepository.findByRecipientUserId(userId, PageRequest.of(offset / Math.max(limit, 1), limit));
        return ResponseEntity.ok(ApiResponse.success(page));
    }
}
