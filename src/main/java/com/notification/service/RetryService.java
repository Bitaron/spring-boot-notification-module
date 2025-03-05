package com.notification.service;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.notification.config.RetryProperties;
import com.notification.domain.notification.Notification;
import com.notification.domain.notification.NotificationPriority;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service to handle notification retry logic.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RetryService {

    /*private final RetryProperties retryProperties;
    private final Random random = new Random();
    
    *//**
     * Calculate the next retry time for a failed notification.
     * 
     * @param notification The failed notification
     * @return The next retry time
     *//*
    public LocalDateTime calculateNextRetryTime(Notification notification) {
        // Base delay in seconds
        int baseDelay = retryProperties.getBaseDelaySeconds();
        
        // Apply exponential backoff if configured
        if (retryProperties.isExponentialBackoff()) {
            double attemptFactor = Math.pow(notification.getAttemptCount(), 
                    retryProperties.getBackoffMultiplier());
            baseDelay = (int) (baseDelay * attemptFactor);
        }
        
        // Priority-based adjustment
        if (notification.getPriority() != null) {
            if (notification.getPriority() == NotificationPriority.HIGH) {
                // Reduce delay for high priority
                baseDelay = Math.max(retryProperties.getMinDelaySeconds(), baseDelay / 2);
            } else if (notification.getPriority() == NotificationPriority.LOW) {
                // Increase delay for low priority
                baseDelay = Math.min(retryProperties.getMinDelaySeconds() * 2, baseDelay * 2);
            }
        }
        
        // Add jitter to prevent thundering herd
        if (retryProperties.isAddJitter()) {
            // +/- 20% random jitter
            double jitterPercent = 0.2;
            double jitterFactor = 1.0 - jitterPercent + (random.nextDouble() * jitterPercent * 2);
            baseDelay = (int) (baseDelay * jitterFactor);
        }
        
        // Ensure delay is within configured min/max bounds
        baseDelay = Math.max(retryProperties.getMinDelaySeconds(), baseDelay);
        baseDelay = Math.min(retryProperties.getMaxDelaySeconds(), baseDelay);
        
        return LocalDateTime.now().plusSeconds(baseDelay);
    }
    
    *//**
     * Determine if a notification has reached its maximum retry attempts.
     * 
     * @param notification The notification to check
     * @return true if max attempts reached, false otherwise
     *//*
    public boolean hasReachedMaxAttempts(Notification notification) {
        int maxAttempts = notification.getMaxAttempts();
        if (maxAttempts <= 0) {
            // Use default from config if not specified in notification
            maxAttempts = retryProperties.getMaxAttempts();
        }
        
        return notification.getAttemptCount() >= maxAttempts;
    }*/
} 