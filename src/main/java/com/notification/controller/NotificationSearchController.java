package com.notification.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.notification.domain.notification.Notification;
import com.notification.repository.NotificationRepository;

/**
 * Controller for searching notifications.
 * This controller uses the database-agnostic search implementation.
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationSearchController {

    private final NotificationRepository notificationRepository;
    
    @Autowired
    public NotificationSearchController(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }
    
    /**
     * Search for notifications using the database-specific implementation.
     * 
     * @param query The search query
     * @param pageable Pagination parameters
     * @return Page of matching notifications
     */
 /*   @GetMapping("/search")
    public Page<Notification> search(@RequestParam String query, Pageable pageable) {
        return notificationRepository.searchWithDatabaseSpecifics(query, pageable);
    }*/
}
