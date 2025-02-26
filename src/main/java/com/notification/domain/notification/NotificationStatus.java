package com.notification.domain.notification;

/**
 * Represents the various states a notification can be in during its lifecycle.
 */
public enum NotificationStatus {
    /**
     * Notification has been created but not yet processed.
     */
    CREATED,
    
    /**
     * Notification is being processed.
     */
    PROCESSING,
    
    /**
     * Notification has been sent to the delivery service.
     */
    SENT,
    
    /**
     * Delivery service has confirmed delivery.
     */
    DELIVERED,
    
    /**
     * Recipient has opened/viewed the notification.
     */
    READ,
    
    /**
     * Notification delivery failed.
     */
    FAILED,
    
    /**
     * Notification is scheduled for future delivery.
     */
    SCHEDULED,
    
    /**
     * Notification is being retried after a failure.
     */
    RETRYING,
    
    /**
     * Notification has been cancelled.
     */
    CANCELLED,
    
    /**
     * Notification is pending.
     */
    PENDING,
    
    /**
     * Notification is sending.
     */
    SENDING
} 