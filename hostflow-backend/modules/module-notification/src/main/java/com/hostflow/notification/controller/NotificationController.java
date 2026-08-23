package com.hostflow.notification.controller;

import com.hostflow.common.response.ApiResponse;
import com.hostflow.notification.dto.NotificationLogResponse;
import com.hostflow.notification.dto.SendNotificationRequest;
import com.hostflow.notification.entity.NotificationLog;
import com.hostflow.notification.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<NotificationLogResponse>>> myNotifications(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        UUID recipientUserId = UUID.fromString(jwt.getSubject());
        return ResponseEntity.ok(ApiResponse.success(
                notificationService.myNotifications(recipientUserId, limit, offset).map(NotificationLogResponse::from)));
    }

    @PostMapping("/send")
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    public ResponseEntity<ApiResponse<NotificationLogResponse>> send(@Valid @RequestBody SendNotificationRequest request) {
        NotificationLog log = notificationService.send(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(ApiResponse.success(NotificationLogResponse.from(log)));
    }
}
