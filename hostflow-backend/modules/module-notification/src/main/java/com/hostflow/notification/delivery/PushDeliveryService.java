package com.hostflow.notification.delivery;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.Map;

/**
 * Uses FCM's legacy HTTP API (server key auth) — simpler to integrate than the v1
 * API (which requires OAuth2 service-account tokens); acceptable for Phase 1/2,
 * with a migration to FCM v1 flagged as a reasonable future hardening step since
 * Google is deprecating the legacy API on a rolling timeline.
 *
 * deviceToken is passed in per-call rather than looked up here — DeviceTokenService
 * (added to close the "no device token storage" gap) now resolves the real "to"
 * address via activeTokensFor(userId); wiring an actual send-path caller is left
 * for whichever future flow needs push (marketing/notification orchestration).
 */
@Service
public class PushDeliveryService {

    private final WebClient webClient;
    private final NotificationProviderProperties properties;

    public PushDeliveryService(NotificationProviderProperties properties) {
        this.properties = properties;
        this.webClient = WebClient.builder().build();
    }

    public void send(String deviceToken, String title, String body) {
        try {
            Map<String, Object> payload = Map.of(
                    "to", deviceToken,
                    "notification", Map.of("title", title != null ? title : "HostFlow", "body", body)
            );

            webClient.post()
                    .uri(properties.getPush().getFcmUrl())
                    .header("Authorization", "key=" + properties.getPush().getFcmServerKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(payload)
                    .retrieve()
                    .toBodilessEntity()
                    .block(Duration.ofSeconds(15));
        } catch (Exception e) {
            throw new DeliveryException("Failed to send push notification", e);
        }
    }
}
