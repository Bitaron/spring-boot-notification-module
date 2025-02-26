package com.notification.service.builder;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.notification.domain.notification.DeliveryChannel;
import com.notification.domain.notification.Notification;
import com.notification.domain.notification.NotificationPriority;
import com.notification.domain.notification.NotificationStatus;
import com.notification.domain.notification.NotificationType;
import com.notification.service.template.TemplateService;
import com.fasterxml.jackson.databind.ObjectMapper;

class DefaultNotificationBuilderTest {

    @Mock
    private TemplateService templateService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private DefaultNotificationBuilder builder;

    @Test
    void testBuildEmailNotification() {
        Notification notification = builder
                .recipient("user@example.com")
                .subject("Test Email")
                .content("This is a test email content")
                .channel(DeliveryChannel.EMAIL)
                .type(NotificationType.INFO)
                .priority(NotificationPriority.NORMAL)
                .htmlEnabled(true)
                .build();
        
        assertNotNull(notification);
        assertEquals("user@example.com", notification.getRecipient());
        assertEquals("Test Email", notification.getSubject());
        assertEquals("This is a test email content", notification.getContent());
        assertEquals(DeliveryChannel.EMAIL, notification.getChannel());
        assertEquals(NotificationType.INFO, notification.getType());
        assertEquals(NotificationPriority.NORMAL, notification.getPriority());
        assertTrue(notification.isHtmlEnabled());
        assertEquals(NotificationStatus.PENDING, notification.getStatus());
    }
    
    @Test
    void testBuildSmsNotification() {
        Notification notification = builder
                .recipient("+1234567890")
                .content("This is a test SMS content")
                .channel(DeliveryChannel.SMS)
                .build();
        
        assertNotNull(notification);
        assertEquals("+1234567890", notification.getRecipient());
        assertEquals("This is a test SMS content", notification.getContent());
        assertEquals(DeliveryChannel.SMS, notification.getChannel());
        assertEquals(NotificationType.INFO, notification.getType()); // Default value
        assertEquals(NotificationPriority.NORMAL, notification.getPriority()); // Default value
    }
    
    @Test
    void testScheduledNotification() {
        LocalDateTime scheduledTime = LocalDateTime.now().plusHours(1);
        
        Notification notification = builder
                .recipient("user@example.com")
                .subject("Scheduled Notification")
                .content("This notification is scheduled")
                .channel(DeliveryChannel.EMAIL)
                .scheduledFor(scheduledTime)
                .build();
        
        assertNotNull(notification);
        assertEquals(scheduledTime, notification.getScheduledFor());
    }
} 