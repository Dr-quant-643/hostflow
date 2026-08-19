package com.hostflow.notification.entity;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationLogEntityTest {

    @Test
    void newLog_startsInQueuedStatus() {
        NotificationLog log = new NotificationLog(UUID.randomUUID(), "welcome_email", NotificationChannel.EMAIL);

        assertThat(log.getStatus()).isEqualTo(NotificationStatus.QUEUED);
        assertThat(log.getSentAt()).isNull();
    }

    @Test
    void markSent_setsStatusAndTimestamp() {
        NotificationLog log = new NotificationLog(UUID.randomUUID(), "welcome_email", NotificationChannel.EMAIL);

        log.markSent();

        assertThat(log.getStatus()).isEqualTo(NotificationStatus.SENT);
        assertThat(log.getSentAt()).isNotNull();
    }

    @Test
    void markFailed_setsStatusAndReason() {
        NotificationLog log = new NotificationLog(UUID.randomUUID(), "welcome_email", NotificationChannel.EMAIL);

        log.markFailed("SMTP timeout");

        assertThat(log.getStatus()).isEqualTo(NotificationStatus.FAILED);
        assertThat(log.getFailureReason()).isEqualTo("SMTP timeout");
    }
}
