package com.notification.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.notification.config.RetryProperties;
import com.notification.domain.notification.DeliveryChannel;
import com.notification.domain.notification.Notification;
import com.notification.domain.notification.NotificationPriority;
import com.notification.domain.notification.NotificationStatus;

@ExtendWith(MockitoExtension.class)
class RetryServiceTest {
    
    @Mock
    private RetryProperties retryProperties;
    
    @InjectMocks
    private RetryService retryService;
    
    @Test
    void testCalculateNextRetryTime() {
        // Given
        Notification notification = new Notification();
        notification.setAttemptCount(1);
        notification.setPriority(NotificationPriority.NORMAL);
        
        // Configure retry properties
        when(retryProperties.isEnabled()).thenReturn(true);
        when(retryProperties.getBaseDelaySeconds()).thenReturn(60);
        when(retryProperties.getMinDelaySeconds()).thenReturn(30);
        when(retryProperties.getMaxDelaySeconds()).thenReturn(7200);
        when(retryProperties.isExponentialBackoff()).thenReturn(true);
        when(retryProperties.getBackoffMultiplier()).thenReturn(2.0);
        when(retryProperties.isAddJitter()).thenReturn(false);
        
        // When
        LocalDateTime nextRetryTime = retryService.calculateNextRetryTime(notification);
        
        // Then
        assertNotNull(nextRetryTime);
        LocalDateTime expectedMinTime = LocalDateTime.now().plusSeconds(60);
        assertTrue(nextRetryTime.isAfter(LocalDateTime.now()));
        
        // The time should be at least the base delay (without jitter)
        long secondsDifference = java.time.Duration.between(LocalDateTime.now(), nextRetryTime).getSeconds();
        assertTrue(secondsDifference >= 59, "Retry delay should be at least approximately the base delay");
    }
    
    @Test
    void testCalculateNextRetryTimeWithHighPriority() {
        // Given
        Notification notification = new Notification();
        notification.setAttemptCount(1);
        notification.setPriority(NotificationPriority.HIGH);
        
        // Configure retry properties
        when(retryProperties.getBaseDelaySeconds()).thenReturn(60);
        when(retryProperties.getMinDelaySeconds()).thenReturn(30);
        when(retryProperties.getMaxDelaySeconds()).thenReturn(7200);
        when(retryProperties.isExponentialBackoff()).thenReturn(false);
        when(retryProperties.isAddJitter()).thenReturn(false);
        
        // When
        LocalDateTime nextRetryTime = retryService.calculateNextRetryTime(notification);
        
        // Then
        assertNotNull(nextRetryTime);
        
        // High priority should have reduced delay
        long secondsDifference = java.time.Duration.between(LocalDateTime.now(), nextRetryTime).getSeconds();
        assertTrue(secondsDifference >= 29 && secondsDifference <= 31, 
                   "High priority should reduce delay to min delay (30 secs)");
    }
    
    @Test
    void testHasReachedMaxAttempts() {
        // Given
        Notification notification = new Notification();
        notification.setAttemptCount(3);
        notification.setMaxAttempts(3);
        
        // When/Then
        assertTrue(retryService.hasReachedMaxAttempts(notification));
        
        // Given (notification without max attempts specified)
        notification.setMaxAttempts(0);
        when(retryProperties.getMaxAttempts()).thenReturn(3);
        
        // When/Then
        assertTrue(retryService.hasReachedMaxAttempts(notification));
        
        // Given (notification with attempt count less than max)
        notification.setAttemptCount(2);
        
        // When/Then
        assertFalse(retryService.hasReachedMaxAttempts(notification));
    }
}