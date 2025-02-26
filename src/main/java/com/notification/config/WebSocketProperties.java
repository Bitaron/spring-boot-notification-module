package com.notification.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * Configuration properties for WebSocket notifications.
 */
@Configuration
@ConfigurationProperties(prefix = "notification.websocket")
@Data
public class WebSocketProperties {
    
    /**
     * WebSocket endpoint path.
     */
    private String endpoint = "/ws";
    
    /**
     * Destination prefix for sending messages.
     */
    private String applicationDestinationPrefix = "/app";
    
    /**
     * Topic prefix for subscription.
     */
    private String topicPrefix = "/topic";
    
    /**
     * User destination prefix.
     */
    private String userDestinationPrefix = "/user/";
    
    /**
     * Notification topic name.
     */
    private String notificationTopic = "notifications";
    
    /**
     * Allow origins for CORS.
     */
    private String[] allowedOrigins = {"*"};
} 