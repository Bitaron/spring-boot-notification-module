package com.notification.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * Configuration properties for the notification module.
 */
@Configuration
@ConfigurationProperties(prefix = "notification")
@Data
public class NotificationProperties {
    
    /**
     * Whether to use a message queue for asynchronous notification processing.
     */
    private boolean useQueue = false;
    
    /**
     * Default locale for templates.
     */
    private String defaultLocale = "en";
    
    /**
     * Default retention period for notifications in days.
     */
    private int retentionDays = 30;
    
    /**
     * Maximum batch size for processing notifications.
     */
    private int maxBatchSize = 100;
    
    /**
     * Whether to throttle notification sending.
     */
    private boolean enableThrottling = false;
    
    /**
     * Maximum rate of notifications per second when throttling is enabled.
     */
    private int maxNotificationsPerSecond = 50;
    
    /**
     * Delivery channel configurations.
     */
    private ChannelProperties channels = new ChannelProperties();
    
    /**
     * Properties for notification delivery channels.
     */
    @Data
    public static class ChannelProperties {
        private EmailProperties email = new EmailProperties();
        private WebProperties web = new WebProperties();
    }
    
    /**
     * Properties for email notifications.
     */
    @Data
    public static class EmailProperties {
        /**
         * Whether email notifications are enabled.
         * When set to true, an EmailDeliveryProvider bean must be provided.
         */
        private boolean enabled = false;
        
        /**
         * Default from address for emails.
         */
        private String fromAddress = "noreply@example.com";
        
        /**
         * Default subject for emails when not specified.
         */
        private String defaultSubject = "Notification";
        
        /**
         * Maximum attachment size in MB.
         */
        private int maxAttachmentSizeMb = 10;
        
        /**
         * Whether HTML is enabled by default for emails.
         */
        private boolean htmlEnabledByDefault = true;
    }

    
    /**
     * Properties for web notifications.
     */
    @Data
    public static class WebProperties {
        /**
         * Whether web notifications are enabled.
         */
        private boolean enabled = true;
    }
}