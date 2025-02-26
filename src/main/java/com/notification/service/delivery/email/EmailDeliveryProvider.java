package com.notification.service.delivery.email;

import java.util.Map;

/**
 * Interface that must be implemented by clients if they want to enable email notifications.
 * This forces clients to provide a concrete implementation for sending emails.
 */
public interface EmailDeliveryProvider {
    
    /**
     * Sends an email message.
     *
     * @param to The recipient's email address
     * @param subject The email subject
     * @param body The email body
     * @param isHtml Whether the body is HTML
     * @return True if the message was sent successfully, false otherwise
     */
    boolean sendEmail(String to, String subject, String body, boolean isHtml);
    
    /**
     * Sends an email with attachments.
     *
     * @param to The recipient's email address
     * @param subject The email subject
     * @param body The email body
     * @param isHtml Whether the body is HTML
     * @param attachments Map of attachment names to their contents (byte arrays)
     * @return True if the message was sent successfully, false otherwise
     */
    boolean sendEmailWithAttachments(String to, String subject, String body, boolean isHtml,
                                    Map<String, byte[]> attachments);
    
    /**
     * Checks if the provider is properly configured.
     *
     * @return True if the provider is properly configured, false otherwise
     */
    boolean isConfigured();
}
