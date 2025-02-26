package com.notification.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.notification.domain.notification.Notification;

/**
 * Custom repository interface for notifications.
 * This allows for database-specific implementations.
 */
public interface NotificationRepositoryCustom {
    
    /**
     * Searches notifications by recipient, subject or content.
     * This will be implemented in a database-specific way.
     *
     * @param searchTerm The search term
     * @param pageable Pagination information
     * @return Page of matching notifications
     */
    Page<Notification> searchWithDatabaseSpecifics(String searchTerm, Pageable pageable);
}
