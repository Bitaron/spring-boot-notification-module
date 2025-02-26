package com.notification.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.notification.domain.notification.DeliveryChannel;
import com.notification.domain.notification.Notification;
import com.notification.domain.notification.NotificationStatus;
import com.notification.repository.NotificationRepository;
import com.notification.service.delivery.DeliveryService;
import com.notification.service.delivery.DeliveryServiceFactory;
import com.notification.service.delivery.DeliveryException;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;
    
    @Mock
    private DeliveryServiceFactory deliveryServiceFactory;
    
    @Mock
    private DeliveryService deliveryService;
    
    @InjectMocks
    private NotificationService notificationService;
    
    @Test
    void testCreateNotification() {
        // Given
        Notification notification = new Notification();
        notification.setRecipient("test@example.com");
        notification.setContent("Test content");
        notification.setChannel(DeliveryChannel.EMAIL);
        
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
        
        // When
        Notification result = notificationService.createNotification(notification);
        
        // Then
        assertNotNull(result);
        verify(notificationRepository).save(notification);
    }
    
    @Test
    void testSendNotification_successful() throws Exception {
        // Given
        UUID id = UUID.randomUUID();
        Notification notification = new Notification();
        notification.setId(id);
        notification.setRecipient("test@example.com");
        notification.setContent("Test content");
        notification.setChannel(DeliveryChannel.EMAIL);
        notification.setStatus(NotificationStatus.PENDING);
        
        when(notificationRepository.findById(id)).thenReturn(Optional.of(notification));
        when(deliveryServiceFactory.getDeliveryService(DeliveryChannel.EMAIL)).thenReturn(deliveryService);
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
        
        // When
        notificationService.sendNotification(id);
        
        // Then
        verify(deliveryService).deliver(notification);
        
        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository, times(2)).save(captor.capture());
        
        // Check status updates
        List<Notification> savedNotifications = captor.getAllValues();
        assertEquals(NotificationStatus.SENDING, savedNotifications.get(0).getStatus());
        assertEquals(NotificationStatus.SENT, savedNotifications.get(1).getStatus());
        assertNotNull(savedNotifications.get(1).getSentAt());
    }
    
    @Test
    void testSendNotification_deliveryFails() throws Exception {
        // Given
        UUID id = UUID.randomUUID();
        Notification notification = new Notification();
        notification.setId(id);
        notification.setRecipient("test@example.com");
        notification.setContent("Test content");
        notification.setChannel(DeliveryChannel.EMAIL);
        notification.setStatus(NotificationStatus.PENDING);
        
        when(notificationRepository.findById(id)).thenReturn(Optional.of(notification));
        when(deliveryServiceFactory.getDeliveryService(DeliveryChannel.EMAIL)).thenReturn(deliveryService);
        doThrow(new DeliveryException("Test delivery error")).when(deliveryService).deliver(notification);
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
        
        // When
        notificationService.sendNotification(id);
        
        // Then
        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository, times(2)).save(captor.capture());
        
        // Check status updates
        List<Notification> savedNotifications = captor.getAllValues();
        assertEquals(NotificationStatus.FAILED, savedNotifications.get(1).getStatus());
    }
    
    @Test
    void testGetNotificationsByRecipient() {
        // Given
        Notification notification1 = new Notification();
        notification1.setId(UUID.randomUUID());
        notification1.setRecipient("user@example.com");
        
        Notification notification2 = new Notification();
        notification2.setId(UUID.randomUUID());
        notification2.setRecipient("user@example.com");
        
        List<Notification> notifications = Arrays.asList(notification1, notification2);
        Page<Notification> page = new PageImpl<>(notifications);
        
        when(notificationRepository.findByRecipient(eq("user@example.com"), any(Pageable.class))).thenReturn(page);
        
        // When
        Page<Notification> result = notificationService.getNotificationsByRecipient("user@example.com", Pageable.unpaged());
        
        // Then
        assertEquals(2, result.getContent().size());
    }
    
    @Test
    void testProcessScheduledNotifications() throws Exception {
        // Given
        LocalDateTime now = LocalDateTime.now();
        
        Notification notification = new Notification();
        notification.setId(UUID.randomUUID());
        notification.setStatus(NotificationStatus.PENDING);
        notification.setScheduledFor(now.minusMinutes(5));
        notification.setChannel(DeliveryChannel.EMAIL);
        
        List<Notification> scheduledNotifications = Arrays.asList(notification);
        
        when(notificationRepository.findByStatusAndScheduledForBefore(
                eq(NotificationStatus.PENDING), any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(notification));
        
        when(deliveryServiceFactory.getDeliveryService(DeliveryChannel.EMAIL)).thenReturn(deliveryService);
        
        // When
        notificationService.processScheduledNotifications();
        
        // Then
        verify(deliveryService).deliver(notification);
        verify(notificationRepository, atLeastOnce()).save(any(Notification.class));
    }
} 