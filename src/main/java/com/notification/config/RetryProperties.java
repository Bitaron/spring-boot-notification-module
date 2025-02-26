package com.notification.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * Configuration properties for notification retry mechanism.
 */
@Configuration
@ConfigurationProperties(prefix = "notification.retry")
@Data
public class RetryProperties {
    
    /**
     * Whether to enable retry for failed notifications.
     */
    private boolean enabled = true;
    
    /**
     * Maximum number of retry attempts.
     */
    private int maxAttempts = 3;
    
    /**
     * Base delay in seconds between retry attempts.
     */
    private int baseDelaySeconds = 60;
    
    /**
     * Minimum delay in seconds between retry attempts.
     */
    private int minDelaySeconds = 30;
    
    /**
     * Maximum delay in seconds between retry attempts.
     */
    private int maxDelaySeconds = 7200; // 2 hours
    
    /**
     * Whether to use exponential backoff for retry delays.
     */
    private boolean exponentialBackoff = true;
    
    /**
     * Multiplier for exponential backoff.
     */
    private double backoffMultiplier = 2.0;
    
    /**
     * Whether to add jitter to retry delays.
     */
    private boolean addJitter = true;
} 