package com.notification.config;

import com.notification.service.delivery.DeliveryServiceFactory;
import com.notification.service.delivery.email.EmailDeliveryService;
import com.notification.service.delivery.sms.SmsDeliveryService;
import com.notification.service.delivery.sms.SmsSender;
import com.notification.service.delivery.web.WebDeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.simp.SimpMessagingTemplate;


/**
 * Configuration for notification delivery channels.
 * This configuration will conditionally require clients to implement the necessary
 * interfaces based on which channels they've enabled.
 */
@Configuration
@EnableConfigurationProperties(NotificationProperties.class)
public class DeliveryChannelConfiguration {
    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    /**
     * SMS delivery service that will only be created if SMS is enabled.
     * If SMS is enabled but no SmsDeliveryProvider is provided, a compile-time error will occur.
     */
    @Bean
    @ConditionalOnProperty(prefix = "notification.channels", name = "sms.enabled", havingValue = "true")
    public SmsDeliveryService smsDeliveryService(SmsSender smsDeliveryProvider,
                                                 SmsProperties properties) {
        if (!smsDeliveryProvider.isConfigured()) {
            throw new IllegalStateException("SMS is enabled but the SmsDeliveryProvider is not properly configured");
        }
        return new SmsDeliveryService(smsDeliveryProvider, properties);
    }

    /**
     * Email delivery service that will only be created if email is enabled.
     * If email is enabled but no EmailDeliveryProvider is provided, a compile-time error will occur.
     */
    @Bean
    @ConditionalOnProperty(prefix = "notification.channels", name = "email.enabled", havingValue = "true")
    public EmailDeliveryService emailDeliveryService(EmailProperties properties) {
        return new EmailDeliveryService(javaMailSender, properties);
    }

    /**
     * Web delivery service that will only be created if web notifications are enabled.
     * Web notifications are always available by default and don't require any external provider.
     */
    @Bean
    @ConditionalOnProperty(prefix = "notification.channels", name = "web.enabled", havingValue = "true", matchIfMissing = true)
    public WebDeliveryService webDeliveryService(WebSocketProperties properties) {
        return new WebDeliveryService(simpMessagingTemplate, properties);
    }

    /**
     * Factory that assembles all available delivery services.
     * The factory will only include services for enabled channels.
     */
    @Bean
    public DeliveryServiceFactory deliveryServiceFactory(NotificationProperties properties) {
        return new DeliveryServiceFactory(properties);
    }
}
