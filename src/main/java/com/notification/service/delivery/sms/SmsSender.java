package com.notification.service.delivery.sms;

/**
 * Interface for SMS service providers.
 * Applications can implement this interface to integrate with their preferred SMS gateway.
 */
public interface SmsSender {
    
    /**
     * Sends an SMS message.
     *
     * @param recipient The recipient phone number
     * @param content The message content
     * @throws SmsException if sending fails
     */
    void sendSms(String recipient, String content) throws SmsException;
    
    /**
     * Checks if this SMS sender is properly configured and ready to use.
     *
     * @return true if the sender is configured and operational
     */
    boolean isConfigured();
} 