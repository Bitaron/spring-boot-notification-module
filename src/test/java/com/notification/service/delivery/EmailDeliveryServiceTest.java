package com.notification.service.delivery;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.notification.service.delivery.email.EmailDeliveryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import com.notification.config.EmailProperties;
import com.notification.domain.notification.DeliveryChannel;
import com.notification.domain.notification.Notification;
import com.notification.domain.notification.NotificationPriority;
import com.notification.domain.notification.NotificationType;

@ExtendWith(MockitoExtension.class)
class EmailDeliveryServiceTest {

    @Mock
    private JavaMailSender mailSender;
    
    @Mock
    private EmailProperties emailProperties;
    
    @InjectMocks
    private EmailDeliveryService emailDeliveryService;
    
    @Test
    void testDeliver_sendsEmail() throws Exception {
        // Given
        when(emailProperties.getFromAddress()).thenReturn("noreply@example.com");
        
        Notification notification = new Notification();
        notification.setRecipient("user@example.com");
        notification.setSubject("Test Email");
        notification.setContent("<p>This is a test email</p>");
        notification.setChannel(DeliveryChannel.EMAIL);
        notification.setType(NotificationType.INFO);
        notification.setPriority(NotificationPriority.NORMAL);
        notification.setHtmlEnabled(true);
        
        // When
        emailDeliveryService.deliver(notification);
        
        // Then
        verify(mailSender).send(any(MimeMessagePreparator.class));
    }
    
    @Test
    void testIsSupported() {
        assertTrue(emailDeliveryService.isSupported());
    }
} 