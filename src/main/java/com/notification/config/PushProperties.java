package com.notification.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * Configuration properties for push notifications.
 */
@Configuration
@ConfigurationProperties(prefix = "notification.push")
@Data
public class PushProperties {
    
    /**
     * FCM (Firebase Cloud Messaging) API key.
     */
    private String fcmApiKey;
    
    /**
     * Path to APNS (Apple Push Notification Service) certificate.
     */
    private String apnsCertificatePath;
    
    /**
     * APNS certificate password.
     */
    private String apnsCertificatePassword;
    
    /**
     * Whether APNS is using the sandbox environment.
     */
    private boolean apnsSandbox = false;
    
    /**
     * Default icon for Android push notifications.
     */
    private String defaultAndroidIcon = "ic_notification";
    
    /**
     * Default sound for push notifications.
     */
    private String defaultSound = "default";
    
    /**
     * Time to live for push notifications in seconds.
     */
    private int timeToLive = 2419200; // 4 weeks
} 