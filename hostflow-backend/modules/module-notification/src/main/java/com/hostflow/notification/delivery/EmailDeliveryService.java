package com.hostflow.notification.delivery;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Resend's HTTP API (not raw SMTP -- see NotificationProviderProperties.Email
 * for why). Same WebClient-over-HTTP pattern as SmsDeliveryService: fail
 * loudly with the provider's real response on any error rather than silently
 * pretending to succeed.
 */
@Service
public class EmailDeliveryService {

    private final WebClient webClient;
    private final NotificationProviderProperties properties;

    public EmailDeliveryService(NotificationProviderProperties properties) {
        this.properties = properties;
        this.webClient = WebClient.builder().build();
    }

    public void send(String toEmail, String subject, String body) {
        try {
            Map<String, Object> payload = Map.of(
                    "from", properties.getEmail().getFromName() + " <" + properties.getEmail().getFromAddress() + ">",
                    "to", List.of(toEmail),
                    "subject", subject != null ? subject : "Notification from HostFlow",
                    "text", body);

            webClient.post()
                    .uri(properties.getEmail().getApiUrl())
                    .header("Authorization", "Bearer " + properties.getEmail().getApiKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(payload)
                    .retrieve()
                    .toBodilessEntity()
                    .block(Duration.ofSeconds(15));
        } catch (Exception e) {
            throw new DeliveryException("Failed to send email to " + toEmail, e);
        }
    }
}
