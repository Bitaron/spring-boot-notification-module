package com.notification.service.delivery;

import com.notification.domain.notification.Notification;

/**
 * Interface for services that deliver notifications through different channels.
 */
public interface DeliveryService {
    
    /**
     * Checks if this delivery service can deliver the given notification.
     * 
     * @param notification The notification to check
     * @return true if this service can deliver the notification, false otherwise
     */
    boolean isSupported(Notification notification);
    
    /**
     * Delivers a notification
     * 
     * @param notification The notification to deliver
     * @return true if delivery was successful, false otherwise
     */
    boolean deliver(Notification notification);
}