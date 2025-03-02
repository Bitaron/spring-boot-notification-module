package com.notification.service.delivery;

import com.notification.domain.notification.NotificationChannel;
import com.notification.domain.notification.Notification;

/**
 * Interface for services that deliver notifications through different channels.
 */
public interface DeliveryService {
    
    /**
     * Checks if this delivery service can deliver
     *
     * @return true if this service can deliver , false otherwise
     */
    boolean isSupported();

    NotificationChannel getChannel();

    /**
     * Delivers a notification
     * 
     * @param notification The notification to deliver
     * @return true if delivery was successful, false otherwise
     */
    void deliver(Notification notification);
}