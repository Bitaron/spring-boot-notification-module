package com.notification.queue;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.notification.config.QueueProperties;
import com.notification.domain.notification.Notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Sends notifications to the RabbitMQ queue.
 */
@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "notification.use-queue", havingValue = "true")
public class NotificationQueueSender {
    
    private final RabbitTemplate rabbitTemplate;
    private final QueueProperties queueProperties;
    
    /**
     * Sends a notification to the queue for asynchronous processing.
     *
     * @param notification The notification to send
     */
    public void sendNotification(Notification notification) {
        log.info("Sending notification to queue: {}", notification.getId());
        
        String routingKey = queueProperties.getRoutingKey() + "." + notification.getChannel().name().toLowerCase();
        rabbitTemplate.convertAndSend(queueProperties.getExchange(), routingKey, notification);
    }
} 