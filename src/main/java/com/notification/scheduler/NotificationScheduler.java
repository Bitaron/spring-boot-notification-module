package com.notification.scheduler;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Scheduler for notification processing tasks.
 */
@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "notification.scheduler.enabled", havingValue = "true", matchIfMissing = true)
public class NotificationScheduler {
    
    private final NotificationService notificationService;
    
    /**
     * Processes scheduled notifications that are due to be sent.
     */
    @Scheduled(fixedDelayString = "${notification.scheduler.scheduled-notifications-interval:60000}")
    public void processScheduledNotifications() {
        log.info("Processing scheduled notifications");
        try {
            notificationService.processScheduledNotifications();
        } catch (Exception e) {
            log.error("Error processing scheduled notifications", e);
        }
    }
    
    /**
     * Processes failed notifications that are eligible for retry.
     */
    @Scheduled(fixedDelayString = "${notification.scheduler.retry-notifications-interval:300000}")
    public void processRetryNotifications() {
        log.info("Processing retry notifications");
        try {
            notificationService.processRetryNotifications();
        } catch (Exception e) {
            log.error("Error processing retry notifications", e);
        }
    }
} 