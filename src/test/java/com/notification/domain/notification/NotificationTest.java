package com.notification.domain.notification;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class NotificationTest {

    @Test
    void testNotificationCreation() {
        Notification notification = new Notification();
        notification.setId(UUID.randomUUID());
        notification.setRecipient("user@example.com");
        notification.setSubject("Test Subject");
        notification.setContent("Test Content");
        notification.setChannel(DeliveryChannel.EMAIL);
        notification.setType(NotificationType.INFO);
        notification.setPriority(NotificationPriority.NORMAL);
        notification.setHtmlEnabled(true);
        notification.setStatus(NotificationStatus.PENDING);
        
        assertEquals("user@example.com", notification.getRecipient());
        assertEquals("Test Subject", notification.getSubject());
        assertEquals("Test Content", notification.getContent());
        assertEquals(DeliveryChannel.EMAIL, notification.getChannel());
        assertEquals(NotificationType.INFO, notification.getType());
        assertEquals(NotificationPriority.NORMAL, notification.getPriority());
        assertTrue(notification.isHtmlEnabled());
        assertEquals(NotificationStatus.PENDING, notification.getStatus());
    }
    
    @Test
    void testStatusTransitions() {
        Notification notification = new Notification();
        notification.setStatus(NotificationStatus.PENDING);
        
        assertEquals(NotificationStatus.PENDING, notification.getStatus());
        
        notification.setStatus(NotificationStatus.SENDING);
        assertEquals(NotificationStatus.SENDING, notification.getStatus());
        
        notification.setStatus(NotificationStatus.SENT);
        assertEquals(NotificationStatus.SENT, notification.getStatus());
        
        notification.setStatus(NotificationStatus.DELIVERED);
        assertEquals(NotificationStatus.DELIVERED, notification.getStatus());
        
        notification.setStatus(NotificationStatus.FAILED);
        assertEquals(NotificationStatus.FAILED, notification.getStatus());
    }
} 