package com.hostflow.notification.service;

import com.hostflow.common.exception.ResourceNotFoundException;
import com.hostflow.notification.dto.SendNotificationRequest;
import com.hostflow.notification.entity.NotificationLog;
import com.hostflow.notification.entity.NotificationTemplate;
import com.hostflow.notification.messaging.NotificationPublisher;
import com.hostflow.notification.repository.NotificationLogRepository;
import com.hostflow.notification.repository.NotificationTemplateRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class NotificationService {

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{\\{(\\w+)}}");

    private final NotificationTemplateRepository templateRepository;
    private final NotificationPublisher publisher;
    private final NotificationLogRepository logRepository;

    public NotificationService(NotificationTemplateRepository templateRepository, NotificationPublisher publisher,
                                NotificationLogRepository logRepository) {
        this.templateRepository = templateRepository;
        this.publisher = publisher;
        this.logRepository = logRepository;
    }

    /** Backs the notification inbox — a user's own notification history. */
    public Page<NotificationLog> myNotifications(UUID recipientUserId, int limit, int offset) {
        return logRepository.findByRecipientUserId(recipientUserId, PageRequest.of(offset / Math.max(limit, 1), limit));
    }

    /** UPDATED: request now carries recipientAddress explicitly — see
     * SendNotificationRequest below. */
    @Transactional
    public NotificationLog send(SendNotificationRequest request) {
        NotificationTemplate template = templateRepository.findByCode(request.templateCode())
                .orElseThrow(() -> new ResourceNotFoundException("NotificationTemplate", request.templateCode()));

        String renderedBody = render(template.getBody(), request.variables());

        return publisher.publish(
                request.recipientUserId(), request.recipientAddress(), template.getCode(), template.getChannel(),
                template.getSubject(), renderedBody);
    }

    String render(String body, Map<String, String> variables) {
        if (variables == null || variables.isEmpty()) {
            return body;
        }
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(body);
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            String key = matcher.group(1);
            String replacement = variables.getOrDefault(key, matcher.group(0));
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);
        return result.toString();
    }
}
