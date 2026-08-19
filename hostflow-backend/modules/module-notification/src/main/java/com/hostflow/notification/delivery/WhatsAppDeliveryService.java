package com.hostflow.notification.delivery;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.Map;

@Service
@EnableConfigurationProperties(WhatsAppDeliveryService.WhatsAppProperties.class)
public class WhatsAppDeliveryService {

    private final WebClient webClient;
    private final WhatsAppProperties properties;

    public WhatsAppDeliveryService(WhatsAppProperties properties) {
        this.properties = properties;
        this.webClient = WebClient.builder().build();
    }

    public void send(String toPhoneNumber, String message) {
        try {
            Map<String, Object> payload = Map.of(
                    "messaging_product", "whatsapp",
                    "to", toPhoneNumber,
                    "type", "text",
                    "text", Map.of("body", message)
            );

            webClient.post()
                    .uri(properties.getApiUrl() + "/" + properties.getPhoneNumberId() + "/messages")
                    .header("Authorization", "Bearer " + properties.getAccessToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(payload)
                    .retrieve()
                    .toBodilessEntity()
                    .block(Duration.ofSeconds(15));
        } catch (Exception e) {
            throw new DeliveryException("Failed to send WhatsApp message to " + toPhoneNumber, e);
        }
    }

    @ConfigurationProperties(prefix = "hostflow.notification.providers.whatsapp")
    public static class WhatsAppProperties {
        private String apiUrl = "https://graph.facebook.com/v20.0";
        private String phoneNumberId;
        private String accessToken;

        public String getApiUrl() { return apiUrl; }
        public void setApiUrl(String apiUrl) { this.apiUrl = apiUrl; }
        public String getPhoneNumberId() { return phoneNumberId; }
        public void setPhoneNumberId(String phoneNumberId) { this.phoneNumberId = phoneNumberId; }
        public String getAccessToken() { return accessToken; }
        public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    }
}
