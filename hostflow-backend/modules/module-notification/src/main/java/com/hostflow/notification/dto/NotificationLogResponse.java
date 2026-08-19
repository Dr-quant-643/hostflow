package com.hostflow.notification.dto;

import com.hostflow.notification.entity.NotificationLog;

import java.util.UUID;

public record NotificationLogResponse(UUID id, UUID recipientUserId, String templateCode, String channel, String status) {

    public static NotificationLogResponse from(NotificationLog log) {
        return new NotificationLogResponse(
                log.getId(), log.getRecipientUserId(), log.getTemplateCode(),
                log.getChannel().name(), log.getStatus().name());
    }
}
