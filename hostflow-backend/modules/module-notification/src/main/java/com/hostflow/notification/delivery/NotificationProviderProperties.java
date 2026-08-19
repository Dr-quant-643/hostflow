package com.hostflow.notification.delivery;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Binds to hostflow.notification.providers.* — kept SEPARATE from the standard
 * spring.mail.* properties (which configure the SMTP connection itself) since
 * these carry business-level config: sender identity and the SMS provider's API
 * credentials. SMS provider chosen as Africa's Talking (a generic HTTP API,
 * config-driven) since it's the dominant SMS gateway across East Africa, fitting
 * the MPESA/Kenya market focus established for this project.
 */
@ConfigurationProperties(prefix = "hostflow.notification.providers")
public class NotificationProviderProperties {

    private Email email = new Email();
    private Sms sms = new Sms();
    private Push push = new Push();

    public Email getEmail() { return email; }
    public void setEmail(Email email) { this.email = email; }
    public Sms getSms() { return sms; }
    public void setSms(Sms sms) { this.sms = sms; }
    public Push getPush() { return push; }
    public void setPush(Push push) { this.push = push; }

    public static class Email {
        private String fromAddress = "no-reply@hostflow.app";
        private String fromName = "HostFlow";

        public String getFromAddress() { return fromAddress; }
        public void setFromAddress(String fromAddress) { this.fromAddress = fromAddress; }
        public String getFromName() { return fromName; }
        public void setFromName(String fromName) { this.fromName = fromName; }
    }

    public static class Sms {
        private String apiUrl = "https://api.africastalking.com/version1/messaging";
        private String username;
        private String apiKey;
        private String senderId;

        public String getApiUrl() { return apiUrl; }
        public void setApiUrl(String apiUrl) { this.apiUrl = apiUrl; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getApiKey() { return apiKey; }
        public void setApiKey(String apiKey) { this.apiKey = apiKey; }
        public String getSenderId() { return senderId; }
        public void setSenderId(String senderId) { this.senderId = senderId; }
    }

    public static class Push {
        private String fcmServerKey;
        private String fcmUrl = "https://fcm.googleapis.com/fcm/send";

        public String getFcmServerKey() { return fcmServerKey; }
        public void setFcmServerKey(String fcmServerKey) { this.fcmServerKey = fcmServerKey; }
        public String getFcmUrl() { return fcmUrl; }
        public void setFcmUrl(String fcmUrl) { this.fcmUrl = fcmUrl; }
    }
}
