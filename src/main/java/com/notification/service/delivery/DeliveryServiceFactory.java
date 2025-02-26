package com.notification.service.delivery;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.notification.config.NotificationProperties;
import com.notification.domain.notification.DeliveryChannel;
import com.notification.domain.notification.Notification;

/**
 * Factory that provides the appropriate delivery service for a notification channel.
 */
@Component
public class DeliveryServiceFactory {
    
    private static final Logger logger = LoggerFactory.getLogger(DeliveryServiceFactory.class);
    
    private final List<DeliveryService> deliveryServices = new ArrayList<>();
    private final NotificationProperties properties;
    
    public DeliveryServiceFactory(NotificationProperties properties) {
        this.properties = properties;
        logger.info("DeliveryServiceFactory initialized with properties: {}", properties);
    }
    
    /**
     * Register a delivery service.
     * This will be called by Spring for each DeliveryService bean that is available.
     * 
     * @param deliveryService The delivery service to register
     */
    @Autowired(required = false)
    public void registerDeliveryService(DeliveryService deliveryService) {
        deliveryServices.add(deliveryService);
        logger.info("Registered delivery service: {}", deliveryService.getClass().getSimpleName());
    }
    
    /**
     * Gets the delivery service for a specific notification.
     *
     * @param notification The notification to be delivered
     * @return The appropriate delivery service
     * @throws IllegalArgumentException if no service is found for the channel
     */
    public DeliveryService getDeliveryService(Notification notification) {
        if (notification.getChannel() == null) {
            throw new IllegalArgumentException("Notification channel cannot be null");
        }
        
        Optional<DeliveryService> service = deliveryServices.stream()
                .filter(s -> s.isSupported(notification))
                .findFirst();
        
        if (service.isEmpty()) {
            throw new IllegalArgumentException("No delivery service found for channel: " + notification.getChannel() + 
                    ". Make sure the channel is enabled in configuration and a provider is implemented.");
        }
        
        return service.get();
    }
}