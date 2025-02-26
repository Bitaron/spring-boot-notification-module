package com.notification.service.delivery;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.notification.service.delivery.push.PushDeliveryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.notification.config.PushProperties;
import com.notification.domain.notification.DeliveryChannel;
import com.notification.domain.notification.Notification;

@ExtendWith(MockitoExtension.class)
class PushDeliveryServiceTest {

    @Mock
    private PushProperties pushProperties;
    
    @InjectMocks
    private PushDeliveryService pushDeliveryService;
    
    @Test
    void testIsSupported_withFcmApiKey() {
        when(pushProperties.getFcmApiKey()).thenReturn("test-fcm-api-key");
        
        assertTrue(pushDeliveryService.isSupported());
    }
    
    @Test
    void testIsSupported_withApnsCertificate() {
        when(pushProperties.getApnsCertificatePath()).thenReturn("/path/to/certificate.p12");
        
        assertTrue(pushDeliveryService.isSupported());
    }
    
    @Test
    void testIsSupported_withoutConfig() {
        when(pushProperties.getFcmApiKey()).thenReturn(null);
        when(pushProperties.getApnsCertificatePath()).thenReturn(null);
        
        assertFalse(pushDeliveryService.isSupported());
    }
    
    @Test
    void testDeliver_whenNotSupported_throwsException() {
        when(pushProperties.getFcmApiKey()).thenReturn(null);
        when(pushProperties.getApnsCertificatePath()).thenReturn(null);
        
        Notification notification = new Notification();
        notification.setChannel(DeliveryChannel.PUSH);
        
        assertThrows(DeliveryException.class, () -> {
            pushDeliveryService.deliver(notification);
        });
    }
} 