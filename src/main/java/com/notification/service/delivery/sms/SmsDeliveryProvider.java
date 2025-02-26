package com.notification.service.delivery.sms;

/**
 * Interface that must be implemented by clients if they want to enable SMS notifications.
 * This forces clients to provide a concrete implementation for sending SMS.
 */
public interface SmsDeliveryProvider {
    
    /**
     * Sends an SMS message.
     *
     * @param phoneNumber The recipient's phone number
     * @param message The message content
     * @return True if the message was sent successfully, false otherwise
     */
    boolean sendSms(String phoneNumber, String message);
    
    /**
     * Checks if the provider is properly configured.
     *
     * @return True if the provider is properly configured, false otherwise
     */
    boolean isConfigured();
}
