package com.notification.service.delivery;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import com.notification.service.delivery.email.EmailDeliveryService;
import com.notification.service.delivery.push.PushDeliveryService;
import com.notification.service.delivery.sms.SmsDeliveryService;
import com.notification.service.delivery.web.WebDeliveryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.notification.domain.notification.DeliveryChannel;

@ExtendWith(MockitoExtension.class)
class DeliveryServiceFactoryTest {

    @Mock
    private EmailDeliveryService emailDeliveryService;
    
    @Mock
    private SmsDeliveryService smsDeliveryService;
    
    @Mock
    private PushDeliveryService pushDeliveryService;
    
    @Mock
    private WebDeliveryService webDeliveryService;
    
    private DeliveryServiceFactory deliveryServiceFactory;
    
    @BeforeEach
    void setup() {
        // Setup each delivery service to support its channel
        when(emailDeliveryService.getChannel()).thenReturn(DeliveryChannel.EMAIL);
        when(smsDeliveryService.getChannel()).thenReturn(DeliveryChannel.SMS);
        when(pushDeliveryService.getChannel()).thenReturn(DeliveryChannel.PUSH);
        when(webDeliveryService.getChannel()).thenReturn(DeliveryChannel.WEB);
        
        // Setup each service to be supported
        when(emailDeliveryService.isSupported()).thenReturn(true);
        when(smsDeliveryService.isSupported()).thenReturn(true);
        when(pushDeliveryService.isSupported()).thenReturn(true);
        when(webDeliveryService.isSupported()).thenReturn(true);
        
        // Create Map with service names as keys - these match the naming convention in DeliveryServiceFactory
        Map<String, DeliveryService> deliveryServicesMap = new HashMap<>();
        deliveryServicesMap.put("emailDeliveryService", emailDeliveryService);
        deliveryServicesMap.put("smsDeliveryService", smsDeliveryService);
        deliveryServicesMap.put("pushDeliveryService", pushDeliveryService);
        deliveryServicesMap.put("webDeliveryService", webDeliveryService);
        
        deliveryServiceFactory = new DeliveryServiceFactory(deliveryServicesMap);
    }
    
    @Test
    void testGetDeliveryServiceForEmailChannel() {
        DeliveryService service = deliveryServiceFactory.getDeliveryService(DeliveryChannel.EMAIL);
        assertEquals(emailDeliveryService, service);
    }
    
    @Test
    void testGetDeliveryServiceForSmsChannel() {
        DeliveryService service = deliveryServiceFactory.getDeliveryService(DeliveryChannel.SMS);
        assertEquals(smsDeliveryService, service);
    }
    
    @Test
    void testGetDeliveryServiceForPushChannel() {
        DeliveryService service = deliveryServiceFactory.getDeliveryService(DeliveryChannel.PUSH);
        assertEquals(pushDeliveryService, service);
    }
    
    @Test
    void testGetDeliveryServiceForWebChannel() {
        DeliveryService service = deliveryServiceFactory.getDeliveryService(DeliveryChannel.WEB);
        assertEquals(webDeliveryService, service);
    }
    
    @Test
    void testNoServiceFoundForChannel() {
        assertThrows(IllegalArgumentException.class, () -> {
            deliveryServiceFactory.getDeliveryService(DeliveryChannel.MULTI);
        });
    }
    
    @Test
    void testGetDeliveryService() {
        // When all services are supported
        when(emailDeliveryService.isSupported()).thenReturn(true);
        when(smsDeliveryService.isSupported()).thenReturn(true);
        when(pushDeliveryService.isSupported()).thenReturn(true);
        when(webDeliveryService.isSupported()).thenReturn(true);
        
        // Then each should be returned for its channel
        assertEquals(emailDeliveryService, deliveryServiceFactory.getDeliveryService(DeliveryChannel.EMAIL));
        assertEquals(smsDeliveryService, deliveryServiceFactory.getDeliveryService(DeliveryChannel.SMS));
        assertEquals(pushDeliveryService, deliveryServiceFactory.getDeliveryService(DeliveryChannel.PUSH));
        assertEquals(webDeliveryService, deliveryServiceFactory.getDeliveryService(DeliveryChannel.WEB));
    }
    
    @Test
    void testGetDeliveryService_unsupportedChannel() {
        // When SMS service is not supported
        when(emailDeliveryService.isSupported()).thenReturn(true);
        when(smsDeliveryService.isSupported()).thenReturn(false);
        when(pushDeliveryService.isSupported()).thenReturn(true);
        when(webDeliveryService.isSupported()).thenReturn(true);
        
        // Then asking for SMS should throw exception
        assertThrows(DeliveryException.class, () -> {
            deliveryServiceFactory.getDeliveryService(DeliveryChannel.SMS);
        });
    }
    
    @Test
    void testGetDeliveryService_unknownChannel() {
        // When using an unknown channel (shouldn't happen with enum but still test)
        assertThrows(DeliveryException.class, () -> {
            deliveryServiceFactory.getDeliveryService(null);
        });
    }
}