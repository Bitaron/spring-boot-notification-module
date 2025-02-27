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



}