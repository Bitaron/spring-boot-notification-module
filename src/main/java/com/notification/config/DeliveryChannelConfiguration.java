package com.notification.config;

import com.notification.service.NotificationMessageResolver;
import com.notification.service.delivery.email.EmailDeliveryService;
import com.notification.service.delivery.sms.SmsDeliveryService;
import com.notification.service.delivery.sms.SmsSender;
import com.notification.service.delivery.web.WebDeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.simp.SimpMessagingTemplate;


/**
 * Configuration for notification delivery channels.
 * This configuration will conditionally require clients to implement the necessary
 * interfaces based on which channels they've enabled.
 */
@AutoConfiguration
@EnableConfigurationProperties(NotificationProperties.class)
public class DeliveryChannelConfiguration {
    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    NotificationMessageResolver notificationMessageResolver;

    /**
     * SMS delivery service that will only be created if SMS is enabled.
     * If SMS is enabled but no SmsDeliveryProvider is provided, a compile-time error will occur.
     */
    @Bean
    @ConditionalOnProperty(prefix = "notification.sms", name = "enabled", havingValue = "true")
    public SmsDeliveryService smsDeliveryService(SmsSender smsDeliveryProvider,
                                                 SmsProperties properties) {
        if (!smsDeliveryProvider.isConfigured()) {
            throw new IllegalStateException("SMS is enabled but the SmsDeliveryProvider is not properly configured");
        }
        return new SmsDeliveryService(smsDeliveryProvider, properties,notificationMessageResolver);
    }

    /**
     * Email delivery service that will only be created if email is enabled.
     * If email is enabled but no EmailDeliveryProvider is provided, a compile-time error will occur.
     */
    @Bean
    @ConditionalOnProperty(prefix = "notification.email", name = "enabled", havingValue = "true")
    public EmailDeliveryService emailDeliveryService(EmailProperties properties) {
        return new EmailDeliveryService(javaMailSender, properties,notificationMessageResolver);
    }

    /**
     * Web delivery service that will only be created if web notifications are enabled.
     * Web notifications are always available by default and don't require any external provider.
     */
    @Bean
    @ConditionalOnProperty(prefix = "notification.web", name = "enabled", havingValue = "true", matchIfMissing = true)
    public WebDeliveryService webDeliveryService(WebSocketProperties properties) {
        return new WebDeliveryService(simpMessagingTemplate, properties,notificationMessageResolver);
    }


}
