package com.notification.integration;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import com.notification.domain.notification.DeliveryChannel;
import com.notification.domain.notification.Notification;
import com.notification.domain.notification.NotificationStatus;
import com.notification.service.NotificationService;
import com.notification.service.delivery.sms.MockSmsSender;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "notification.sms.provider=mock"
})
class NotificationSmsIntegrationTest {

    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private MockSmsSender mockSmsSender;
    
    @BeforeEach
    void setup() {
        mockSmsSender.clearMessages();
    }
    
    @Test
    void testSendSmsNotification() {
        // Given
        Notification notification = new Notification();
        notification.setRecipient("+1234567890");
        notification.setContent("Test SMS content");
        notification.setChannel(DeliveryChannel.SMS);
        
        // When
        Notification result = notificationService.sendNotificationImmediately(notification);
        
        // Then
        assertEquals(NotificationStatus.SENT, result.getStatus());
        assertNotNull(result.getSentAt());
        
        // Verify the mock SMS sender received the message
        assertEquals(1, mockSmsSender.getSentMessages().size());
        assertEquals("+1234567890", mockSmsSender.getSentMessages().get(0).getRecipient());
        assertEquals("Test SMS content", mockSmsSender.getSentMessages().get(0).getContent());
    }
    
    @Test
    void testLongSmsContentTruncation() {
        // Given
        String longContent = "This is a very long SMS message that should be truncated according to the configured maximum length setting. " +
                            "SMS messages typically have character limits, and our system should handle this appropriately. " +
                            "Let's make this message even longer to ensure truncation works correctly.";
        
        Notification notification = new Notification();
        notification.setRecipient("+1234567890");
        notification.setContent(longContent);
        notification.setChannel(DeliveryChannel.SMS);
        
        // When
        Notification result = notificationService.sendNotificationImmediately(notification);
        
        // Then
        assertEquals(NotificationStatus.SENT, result.getStatus());
        
        // Verify the message was truncated
        String sentContent = mockSmsSender.getSentMessages().get(0).getContent();
        assertTrue(sentContent.length() <= 160);
        assertTrue(sentContent.endsWith("...") || sentContent.length() == longContent.length());
    }
} 