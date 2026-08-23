package com.hostflow.notification.messaging;

import com.hostflow.notification.delivery.EmailDeliveryService;
import com.hostflow.notification.delivery.PushDeliveryService;
import com.hostflow.notification.delivery.SmsDeliveryService;
import com.hostflow.notification.delivery.WhatsAppDeliveryService;
import com.hostflow.notification.entity.NotificationChannel;
import com.hostflow.notification.service.NotificationLogUpdateService;
import com.hostflow.tenancy.context.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationConsumerTest {

    @Mock
    private NotificationLogUpdateService notificationLogUpdateService;
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
        consumer = new NotificationConsumer(notificationLogUpdateService, emailDeliveryService,
                smsDeliveryService, pushDeliveryService, whatsAppDeliveryService);
    }

    @AfterEach
    void cleanup() {
        TenantContext.clear();
    }

    @Test
    void consumeEmail_onSuccess_callsRealEmailDeliveryService_marksSent() {
        UUID logId = UUID.randomUUID();

        NotificationMessage message = new NotificationMessage(
                logId, UUID.randomUUID(), UUID.randomUUID(), "jane@example.com", NotificationChannel.EMAIL, "Welcome", "Hello!");

        consumer.consumeEmail(message);

        verify(emailDeliveryService).send("jane@example.com", "Welcome", "Hello!");
        verify(notificationLogUpdateService).markSent(logId);
    }

    @Test
    void consumeSms_onProviderFailure_marksFailedAndRethrows() {
        UUID logId = UUID.randomUUID();
        doThrow(new RuntimeException("Africa's Talking 401")).when(smsDeliveryService).send(any(), any());

        NotificationMessage message = new NotificationMessage(
                logId, UUID.randomUUID(), UUID.randomUUID(), "+254700000000", NotificationChannel.SMS, null, "Your code is 1234");

        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> consumer.consumeSms(message));

        verify(notificationLogUpdateService).markFailed(logId, "Africa's Talking 401");
    }
}
