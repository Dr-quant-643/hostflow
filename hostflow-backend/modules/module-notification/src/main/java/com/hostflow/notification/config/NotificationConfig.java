package com.hostflow.notification.config;

import com.hostflow.notification.delivery.NotificationProviderProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Registers NotificationProviderProperties as its own bean, deliberately NOT
 * via @EnableConfigurationProperties on one of the delivery services -- that
 * was previously how it got registered (on EmailDeliveryService), and
 * rewriting that one class for the Resend migration silently broke bean
 * creation for every OTHER delivery service (Sms/Push) that also depends on
 * it, since none of them expected to be responsible for enabling it.
 */
@Configuration
@EnableConfigurationProperties(NotificationProviderProperties.class)
public class NotificationConfig {
}
