package com.hostflow.notification.service;

import com.hostflow.common.exception.ResourceNotFoundException;
import com.hostflow.notification.dto.SendNotificationRequest;
import com.hostflow.notification.entity.NotificationChannel;
import com.hostflow.notification.entity.NotificationLog;
import com.hostflow.notification.entity.NotificationTemplate;
import com.hostflow.notification.messaging.NotificationPublisher;
import com.hostflow.notification.repository.NotificationTemplateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationTemplateRepository templateRepository;
    @Mock
    private NotificationPublisher publisher;

    private NotificationService service;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        service = new NotificationService(templateRepository, publisher);
    }

    @Test
    void render_substitutesAllPlaceholders() {
        String result = service.render("Hello {{first_name}}, your booking at {{property_name}} is confirmed.",
                Map.of("first_name", "Jane", "property_name", "Ocean View"));

        assertThat(result).isEqualTo("Hello Jane, your booking at Ocean View is confirmed.");
    }

    @Test
    void send_throwsResourceNotFound_whenTemplateCodeUnknown() {
        when(templateRepository.findByCode("unknown_code")).thenReturn(Optional.empty());
        SendNotificationRequest request = new SendNotificationRequest(UUID.randomUUID(), "jane@example.com", "unknown_code", Map.of());

        assertThatThrownBy(() -> service.send(request)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void send_rendersTemplateThenPublishesWithRecipientAddress() {
        NotificationTemplate template = new NotificationTemplate(
                "booking_confirmed", NotificationChannel.EMAIL, "Booking Confirmed", "Hi {{first_name}}!");
        when(templateRepository.findByCode("booking_confirmed")).thenReturn(Optional.of(template));

        UUID recipientId = UUID.randomUUID();
        when(publisher.publish(any(), anyString(), anyString(), any(), any(), anyString()))
                .thenReturn(new NotificationLog(recipientId, "booking_confirmed", NotificationChannel.EMAIL));

        SendNotificationRequest request = new SendNotificationRequest(
                recipientId, "jane@example.com", "booking_confirmed", Map.of("first_name", "Sam"));

        service.send(request);

        verify(publisher).publish(recipientId, "jane@example.com", "booking_confirmed", NotificationChannel.EMAIL,
                "Booking Confirmed", "Hi Sam!");
    }
}
