package com.notification.queue;

import com.notification.service.builder.NotificationRequest;
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
     * @param notificationRequest The notification to process
     */
    @RabbitListener(queues = "${notification.queue.name}")
    public void handleNotification(NotificationRequest notificationRequest) {
        log.info("Received notification from queue: {}", notificationRequest.getSender());
        
        try {
            notificationService.sendNotification(notificationRequest);
            log.info("Successfully processed notification from queue: {}", notificationRequest.getSender());
        } catch (Exception e) {
            log.error("Error processing notification from queue: {}",  notificationRequest.getSender(), e);
        }
    }
} 