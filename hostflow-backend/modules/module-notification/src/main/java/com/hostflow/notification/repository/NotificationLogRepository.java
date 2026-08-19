package com.hostflow.notification.repository;

import com.hostflow.notification.entity.NotificationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NotificationLogRepository extends JpaRepository<NotificationLog, UUID> {
    /** NEW: backs the notification inbox — a user's own notification history. */
    Page<NotificationLog> findByRecipientUserId(UUID recipientUserId, Pageable pageable);
}
