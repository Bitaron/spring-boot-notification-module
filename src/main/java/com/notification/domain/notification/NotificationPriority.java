package com.notification.domain.notification;

/**
 * Defines priority levels for notifications.
 */
public enum NotificationPriority {
    /**
     * Low priority, can be delayed if needed.
     */
    LOW,
    
    /**
     * Normal priority, default for most notifications.
     */
    NORMAL,
    
    /**
     * High priority, should be delivered promptly.
     */
    HIGH,
    
    /**
     * Urgent priority, deliver immediately.
     */
    URGENT
} 