package com.notification.scheduler;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.notification.service.NotificationService;

@ExtendWith(MockitoExtension.class)
class NotificationSchedulerTest {

    @Mock
    private NotificationService notificationService;
    
    @InjectMocks
    private NotificationScheduler scheduler;
    
    @Test
    void testProcessScheduledNotifications() {
        // When
        scheduler.processScheduledNotifications();
        
        // Then
        verify(notificationService).processScheduledNotifications();
    }
    
    @Test
    void testProcessRetryNotifications() {
        // When
        scheduler.processRetryNotifications();
        
        // Then
        verify(notificationService).processRetryNotifications();
    }
    
    @Test
    void testHandlesExceptions() {
        // Given
        doThrow(new RuntimeException("Test exception")).when(notificationService).processScheduledNotifications();
        
        // When - this should not throw an exception
        scheduler.processScheduledNotifications();
        
        // Then - method completed without throwing exception
        verify(notificationService).processScheduledNotifications();
    }
} 