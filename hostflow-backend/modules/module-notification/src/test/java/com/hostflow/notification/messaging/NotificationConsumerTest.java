package com.hostflow.notification.messaging;

import com.hostflow.notification.delivery.EmailDeliveryService;
import com.hostflow.notification.delivery.PushDeliveryService;
import com.hostflow.notification.delivery.SmsDeliveryService;
import com.hostflow.notification.delivery.WhatsAppDeliveryService;
import com.hostflow.notification.entity.NotificationChannel;
import com.hostflow.notification.entity.NotificationLog;
import com.hostflow.notification.entity.NotificationStatus;
import com.hostflow.notification.repository.NotificationLogRepository;
import com.hostflow.tenancy.context.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationConsumerTest {

    @Mock
    private NotificationLogRepository notificationLogRepository;
    @Mock
    private EmailDeliveryService emailDeliveryService;
    @Mock
    private SmsDeliveryService smsDeliveryService;
    @Mock
    private PushDeliveryService pushDeliveryService;
    @Mock
    private WhatsAppDeliveryService whatsAppDeliveryService;

    private NotificationConsumer consumer;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        consumer = new NotificationConsumer(notificationLogRepository, emailDeliveryService,
                smsDeliveryService, pushDeliveryService, whatsAppDeliveryService);
    }

    @AfterEach
    void cleanup() {
        TenantContext.clear();
    }

    @Test
    void consumeEmail_onSuccess_callsRealEmailDeliveryService_marksSent() {
        UUID logId = UUID.randomUUID();
        NotificationLog notificationLog = new NotificationLog(UUID.randomUUID(), "welcome_email", NotificationChannel.EMAIL);
        when(notificationLogRepository.findById(logId)).thenReturn(Optional.of(notificationLog));

        NotificationMessage message = new NotificationMessage(
                logId, UUID.randomUUID(), UUID.randomUUID(), "jane@example.com", NotificationChannel.EMAIL, "Welcome", "Hello!");

        consumer.consumeEmail(message);

        verify(emailDeliveryService).send("jane@example.com", "Welcome", "Hello!");
        assertThat(notificationLog.getStatus()).isEqualTo(NotificationStatus.SENT);
        assertThat(TenantContext.isSet()).isFalse();
    }

    @Test
    void consumeSms_onProviderFailure_marksFailedAndRethrows() {
        UUID logId = UUID.randomUUID();
        NotificationLog notificationLog = new NotificationLog(UUID.randomUUID(), "otp_code", NotificationChannel.SMS);
        when(notificationLogRepository.findById(logId)).thenReturn(Optional.of(notificationLog));
        doThrow(new RuntimeException("Africa's Talking 401")).when(smsDeliveryService).send(any(), any());

        NotificationMessage message = new NotificationMessage(
                logId, UUID.randomUUID(), UUID.randomUUID(), "+254700000000", NotificationChannel.SMS, null, "Your code is 1234");

        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> consumer.consumeSms(message));

        assertThat(notificationLog.getStatus()).isEqualTo(NotificationStatus.FAILED);
        assertThat(TenantContext.isSet()).isFalse();
    }
}
