package com.notification.queue;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.notification.config.RabbitMQConfig;
import com.notification.domain.notification.Notification;
import com.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Listener for notification queue messages.
 */
@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "notification.use-queue", havingValue = "true")
public class NotificationQueueListener {
    
    private final NotificationService notificationService;
    
    /**
     * Handles notifications received from the queue.
     *
     * @param notification The notification to process
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void handleNotification(Notification notification) {
        log.info("Received notification from queue: {}", notification.getId());
        
        try {
            notificationService.sendNotification(notification.getId());
            log.info("Successfully processed notification from queue: {}", notification.getId());
        } catch (Exception e) {
            log.error("Error processing notification from queue: {}", notification.getId(), e);
        }
    }
} 