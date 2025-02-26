package com.notification.service.delivery;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.notification.config.WebSocketProperties;
import com.notification.domain.notification.DeliveryChannel;
import com.notification.domain.notification.Notification;

@ExtendWith(MockitoExtension.class)
class WebDeliveryServiceTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;
    
    @Mock
    private WebSocketProperties webSocketProperties;
    
    @InjectMocks
    private WebDeliveryService webDeliveryService;
    
    @Test
    void testDeliver_sendsWebsocketMessage() throws Exception {
        // Given
        when(webSocketProperties.getUserDestinationPrefix()).thenReturn("/user");
        when(webSocketProperties.getNotificationTopic()).thenReturn("notifications");
        
        Notification notification = new Notification();
        notification.setRecipient("user123");
        notification.setContent("Test websocket notification");
        notification.setChannel(DeliveryChannel.WEB);
        
        // When
        webDeliveryService.deliver(notification);
        
        // Then
        verify(messagingTemplate).convertAndSend("/user/user123/notifications", notification);
    }
    
    @Test
    void testIsSupported() {
        assertTrue(webDeliveryService.isSupported());
    }
} 