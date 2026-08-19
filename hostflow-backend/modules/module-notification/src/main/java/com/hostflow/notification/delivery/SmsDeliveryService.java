package com.hostflow.notification.delivery;

import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

/**
 * Africa's Talking uses form-encoded POST + an apiKey header (not Bearer auth).
 * A real, working integration once real credentials are supplied — with no
 * credentials configured (local dev default), this will fail with a 401/403 from
 * the provider, which is the CORRECT behavior (fail loudly, not silently pretend
 * to succeed) rather than the old simulated log.info() approach.
 */
@Service
public class SmsDeliveryService {

    private final WebClient webClient;
    private final NotificationProviderProperties properties;

    public SmsDeliveryService(NotificationProviderProperties properties) {
        this.properties = properties;
        this.webClient = WebClient.builder().build();
    }

    public void send(String toPhoneNumber, String message) {
        try {
            MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
            form.add("username", properties.getSms().getUsername());
            form.add("to", toPhoneNumber);
            form.add("message", message);
            if (properties.getSms().getSenderId() != null) {
                form.add("from", properties.getSms().getSenderId());
            }

            webClient.post()
                    .uri(properties.getSms().getApiUrl())
                    .header("apiKey", properties.getSms().getApiKey())
                    .header("Accept", "application/json")
                    .contentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED)
                    .bodyValue(form)
                    .retrieve()
                    .toBodilessEntity()
                    .block(Duration.ofSeconds(15));
        } catch (Exception e) {
            throw new DeliveryException("Failed to send SMS to " + toPhoneNumber, e);
        }
    }
}
