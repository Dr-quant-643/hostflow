package com.hostflow.notification.delivery;

import jakarta.mail.internet.MimeMessage;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * Real SMTP delivery. JavaMailSender is auto-configured by Spring Boot from
 * spring.mail.* properties (host/port/username/password/starttls) — this class
 * just builds and sends the message. Requires a real SMTP provider configured in
 * application-dev.yml (e.g. Gmail SMTP for dev/testing, SendGrid/SES/Postmark for
 * production — any standard SMTP endpoint works with JavaMailSender unmodified).
 */
@Service
@EnableConfigurationProperties(NotificationProviderProperties.class)
public class EmailDeliveryService {

    private final JavaMailSender mailSender;
    private final NotificationProviderProperties properties;

    public EmailDeliveryService(JavaMailSender mailSender, NotificationProviderProperties properties) {
        this.mailSender = mailSender;
        this.properties = properties;
    }

    public void send(String toEmail, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(properties.getEmail().getFromAddress(), properties.getEmail().getFromName());
            helper.setTo(toEmail);
            helper.setSubject(subject != null ? subject : "Notification from HostFlow");
            helper.setText(body, false);
            mailSender.send(message);
        } catch (Exception e) {
            throw new DeliveryException("Failed to send email to " + toEmail, e);
        }
    }
}
