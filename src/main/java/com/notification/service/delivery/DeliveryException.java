package com.notification.service.delivery;

/**
 * Exception thrown when notification delivery fails.
 */
public class DeliveryException extends RuntimeException {
    
    public DeliveryException(String message) {
        super(message);
    }
    
    public DeliveryException(String message, Throwable cause) {
        super(message, cause);
    }
} 