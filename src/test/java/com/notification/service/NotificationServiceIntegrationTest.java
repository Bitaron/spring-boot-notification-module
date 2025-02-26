package com.notification.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.notification.domain.notification.DeliveryChannel;
import com.notification.domain.notification.Notification;
import com.notification.domain.notification.NotificationStatus;
import com.notification.repository.NotificationRepository;
import com.notification.service.delivery.DeliveryServiceFactory;
import com.notification.service.delivery.SmsDeliveryService;
import com.notification.service.delivery.sms.SmsSender;

@SpringBootTest
@ActiveProfiles("test")
public class NotificationServiceIntegrationTest {

    @Autowired
    private NotificationService notificationService;
    
    @MockBean
    private NotificationRepository notificationRepository;
    
    @MockBean
    private SmsSender smsSender;
    
    @MockBean
    private DeliveryServiceFactory deliveryServiceFactory;
    
    @MockBean
    private SmsDeliveryService smsDeliveryService;
    
    @Test
    void testCreateAndSendNotification() throws Exception {
        // Given
        when(deliveryServiceFactory.getDeliveryService(DeliveryChannel.SMS)).thenReturn(smsDeliveryService);
        doNothing().when(smsDeliveryService).deliver(any(Notification.class));
        
        Notification notification = new Notification();
        notification.setRecipient("+1234567890");
        notification.setContent("Test notification");
        notification.setChannel(DeliveryChannel.SMS);
        
        when(notificationRepository.save(any(Notification.class))).thenAnswer(i -> {
            Notification saved = i.getArgument(0);
            saved.setId(UUID.randomUUID());
            return saved;
        });
        
        // When
        Notification result = notificationService.createNotification(notification);
        
        // Then
        assertNotNull(result.getId());
        verify(notificationRepository).save(notification);
        
        // When
        notificationService.sendNotification(result.getId());
        
        // Then
        verify(smsDeliveryService).deliver(any(Notification.class));
        
        // Verify status update
        verify(notificationRepository, times(2)).save(any(Notification.class));
    }
} 