package com.notification.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * Configuration properties for SMS delivery.
 */
@Configuration
@ConfigurationProperties(prefix = "notification.sms")
@Data
public class SmsProperties {
    
    /**
     * SMS provider to use.
     */
    private String provider = "default";
    
    /**
     * API key for SMS service.
     */
    private String apiKey;
    
    /**
     * API secret for SMS service.
     */
    private String apiSecret;
    
    /**
     * Maximum length of SMS messages.
     */
    private int maxLength = 160;
    
    /**
     * Whether to split long messages into multiple SMS.
     */
    private boolean splitLongMessages = false;
    
    /**
     * Default sender ID.
     */
    private String senderId;
} 