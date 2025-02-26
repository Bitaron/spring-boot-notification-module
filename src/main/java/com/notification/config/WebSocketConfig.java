package com.notification.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import lombok.RequiredArgsConstructor;

/**
 * WebSocket configuration for notification delivery via web sockets.
 */
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    private final WebSocketProperties properties;
    
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Set prefixes for topics and user destinations
        registry.enableSimpleBroker(properties.getTopicPrefix());
        registry.setApplicationDestinationPrefixes(properties.getApplicationDestinationPrefix());
        registry.setUserDestinationPrefix(properties.getUserDestinationPrefix());
    }
    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Register the WebSocket endpoint used by clients to connect
        registry.addEndpoint(properties.getEndpoint())
                .setAllowedOrigins(properties.getAllowedOrigins())
                .withSockJS();
    }
} 