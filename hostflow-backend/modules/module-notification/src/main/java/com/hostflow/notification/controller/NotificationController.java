package com.hostflow.notification.controller;

import com.hostflow.common.response.ApiResponse;
import com.hostflow.notification.dto.NotificationLogResponse;
import com.hostflow.notification.dto.SendNotificationRequest;
import com.hostflow.notification.entity.NotificationLog;
import com.hostflow.notification.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/send")
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    public ResponseEntity<ApiResponse<NotificationLogResponse>> send(@Valid @RequestBody SendNotificationRequest request) {
        NotificationLog log = notificationService.send(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(ApiResponse.success(NotificationLogResponse.from(log)));
    }
}
