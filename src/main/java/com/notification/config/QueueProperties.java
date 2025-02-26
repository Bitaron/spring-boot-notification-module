package com.notification.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * Configuration properties for notification queue.
 */
@Configuration
@ConfigurationProperties(prefix = "notification.queue")
@Data
public class QueueProperties {
    
    /**
     * Whether to use queueing.
     */
    private boolean enabled = false;
    
    /**
     * The exchange name.
     */
    private String exchange = "notification.exchange";
    
    /**
     * The routing key pattern.
     */
    private String routingKey = "notification.#";
    
    /**
     * The queue name.
     */
    private String queueName = "notification.queue";
    
    /**
     * Whether to persist messages.
     */
    private boolean durable = true;
} 