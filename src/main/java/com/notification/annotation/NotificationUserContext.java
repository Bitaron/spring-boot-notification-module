package com.notification.annotation;

import java.time.LocalDateTime;

public interface NotificationUserContext {
    /**
     * Get the current user identifier from security context
     * @return current user identifier
     */
    String getCurrentUser();

    /**
     * Get the current UTC timestamp
     * @return current UTC timestamp in YYYY-MM-DD HH:MM:SS format
     */
    LocalDateTime getCurrentTimestamp();
}