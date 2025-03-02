package com.notification.service.delivery.web;

import com.notification.service.delivery.DeliveryException;
import com.notification.service.delivery.DeliveryService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.notification.config.WebSocketProperties;
import com.notification.domain.notification.NotificationChannel;
import com.notification.domain.notification.Notification;
import com.notification.web.dto.NotificationResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for delivering notifications via WebSocket.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WebDeliveryService implements DeliveryService {
    
    private final SimpMessagingTemplate messagingTemplate;
    private final WebSocketProperties webSocketProperties;
    
    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.WEB;
    }
    
    @Override
    public void deliver(Notification notification) throws DeliveryException {
        try {
            // Use safe getter methods with fallbacks
            String userDestPrefix = getUserDestinationPrefix();
            String recipient = notification.getRecipient();
            String notificationTopic = getNotificationTopic();
            
            String destination = userDestPrefix + recipient + "/" + notificationTopic;
            
            // Convert to DTO for sending over the wire
            NotificationResponse response = new NotificationResponse(notification);
            
            log.info("Sending WebSocket notification to {}, subject: {}", 
                    recipient, notification.getSubject());
            
            messagingTemplate.convertAndSend(destination, response);
            
        } catch (Exception e) {
            throw new DeliveryException("Failed to deliver WebSocket notification", e);
        }
    }
    
    private String getUserDestinationPrefix() {
        try {
            return webSocketProperties.getUserDestinationPrefix();
        } catch (Exception e) {
            log.warn("Could not access userDestinationPrefix property, using default value");
            return "/user/";
        }
    }
    
    private String getNotificationTopic() {
        try {
            return webSocketProperties.getNotificationTopic();
        } catch (Exception e) {
            log.warn("Could not access notificationTopic property, using default value");
            return "notifications";
        }
    }
    
    @Override
    public boolean isSupported() {
        return true; // WebSocket delivery is always supported if the service is available
    }
}