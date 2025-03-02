package com.notification.domain.notification;

/**
 * Enumerates the types of notifications supported by the system.
 */
public enum NotificationType {
    /**
     * Informational notifications.
     */
    INFO,
    
    /**
     * Warning notifications.
     */
    WARNING,
    
    /**
     * Critical notifications.
     */
    CRITICAL,
    
    /**
     * Marketing related notifications.
     */
    MARKETING,

    ALERT,
    /**
     * Transactional notifications.
     */
    TRANSACTIONAL
} 