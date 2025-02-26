package com.notification.service.delivery.sms;

import com.notification.service.delivery.DeliveryException;

/**
 * Exception thrown when SMS delivery fails.
 */
public class SmsException extends DeliveryException {
    
    public SmsException(String message) {
        super(message);
    }
    
    public SmsException(String message, Throwable cause) {
        super(message, cause);
    }
} 