package com.notification.service.delivery;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.notification.config.SmsProperties;
import com.notification.domain.notification.DeliveryChannel;
import com.notification.domain.notification.Notification;
import com.notification.service.delivery.sms.SmsSender;

@ExtendWith(MockitoExtension.class)
public class SmsDeliveryServiceTest {

    @Mock
    private SmsProperties smsProperties;
    
    @Mock
    private SmsSender smsSender;
    
    @InjectMocks
    private SmsDeliveryService smsDeliveryService;
    
    @Test
    void testIsSupported_whenSmsSenderIsConfigured_returnsTrue() {
        when(smsSender.isConfigured()).thenReturn(true);
        
        assertTrue(smsDeliveryService.isSupported());
    }
    
    @Test
    void testIsSupported_whenSmsSenderIsNotConfigured_returnsFalse() {
        when(smsSender.isConfigured()).thenReturn(false);
        
        assertFalse(smsDeliveryService.isSupported());
    }
    
    @Test
    void testDeliver_delegatesToSmsSender() throws Exception {
        // Given
        when(smsSender.isConfigured()).thenReturn(true);
        when(smsProperties.getMaxLength()).thenReturn(160);
        
        Notification notification = new Notification();
        notification.setRecipient("+1234567890");
        notification.setContent("Test SMS content");
        notification.setChannel(DeliveryChannel.SMS);
        
        // When
        smsDeliveryService.deliver(notification);
        
        // Then
        verify(smsSender).sendSms("+1234567890", "Test SMS content");
    }
    
    @Test
    void testDeliver_whenContentExceedsMaxLength_truncatesContent() throws Exception {
        // Given
        when(smsSender.isConfigured()).thenReturn(true);
        when(smsProperties.getMaxLength()).thenReturn(20);
        when(smsProperties.isSplitLongMessages()).thenReturn(false);
        
        Notification notification = new Notification();
        notification.setRecipient("+1234567890");
        notification.setContent("This is a very long message that exceeds the maximum length");
        notification.setChannel(DeliveryChannel.SMS);
        
        // When
        smsDeliveryService.deliver(notification);
        
        // Then
        verify(smsSender).sendSms(eq("+1234567890"), eq("This is a very lo..."));
    }
    
    @Test
    void testDeliver_whenNotSupported_throwsException() {
        // Given
        when(smsSender.isConfigured()).thenReturn(false);
        
        Notification notification = new Notification();
        notification.setChannel(DeliveryChannel.SMS);
        
        // When/Then
        assertThrows(DeliveryException.class, () -> smsDeliveryService.deliver(notification));
    }
} 