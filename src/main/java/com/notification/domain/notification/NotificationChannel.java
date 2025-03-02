package com.notification.domain.notification;

/**
 * Enumerates the available delivery channels for notifications.
 */
public enum NotificationChannel {
    /**
     * Email delivery channel.
     */
    EMAIL,
    
    /**
     * SMS delivery channel.
     */
    SMS,
    
    /**
     * Push notification to mobile devices.
     */
    PUSH,
    
    /**
     * Web notification via WebSocket.
     */
    WEB,
    
    /**
     * Multiple channels at once.
     */
    MULTI
} 