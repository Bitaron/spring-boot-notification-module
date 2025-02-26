package com.notification.service.delivery.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.notification.config.NotificationProperties.WebProperties;
import com.notification.domain.notification.Notification;
import com.notification.service.delivery.DeliveryService;

/**
 * Service for delivering web notifications.
 * This service will only be instantiated if web notifications are enabled.
 */
public class WebDeliveryService implements DeliveryService {

    private static final Logger logger = LoggerFactory.getLogger(WebDeliveryService.class);
    
    private final WebProperties webProperties;
    
    public WebDeliveryService(WebProperties webProperties) {
        this.webProperties = webProperties;
        logger.info("Web delivery service initialized");
    }
    
    @Override
    public boolean deliver(Notification notification) {
        if (!webProperties.isEnabled()) {
            logger.warn("Attempted to send web notification but web channel is disabled");
            return false;
        }
        
        // Web notifications are persisted in the database and retrieved by clients,
        // so delivery is considered successful if the notification exists in the database.
        logger.debug("Web notification ready for recipient: {}", notification.getRecipient());
        return true;
    }
    
    @Override
    public boolean isSupported(Notification notification) {
        return webProperties.isEnabled() && notification.getChannel() != null && 
               notification.getChannel().name().equals("WEB");
    }
}
