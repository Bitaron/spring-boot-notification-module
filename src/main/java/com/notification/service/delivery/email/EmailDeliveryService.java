package com.notification.service.delivery.email;

import java.util.Collections;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.notification.config.NotificationProperties.EmailProperties;
import com.notification.domain.notification.Notification;
import com.notification.service.delivery.DeliveryService;

/**
 * Service for delivering email notifications.
 * This service will only be instantiated if email notifications are enabled.
 */
public class EmailDeliveryService implements DeliveryService {

    private static final Logger logger = LoggerFactory.getLogger(EmailDeliveryService.class);
    
    private final EmailDeliveryProvider emailProvider;
    private final EmailProperties emailProperties;
    
    public EmailDeliveryService(EmailDeliveryProvider emailProvider, EmailProperties emailProperties) {
        this.emailProvider = emailProvider;
        this.emailProperties = emailProperties;
        logger.info("Email delivery service initialized with provider: {}", emailProvider.getClass().getName());
    }
    
    @Override
    public boolean deliver(Notification notification) {
        if (!emailProperties.isEnabled()) {
            logger.warn("Attempted to send email but email channel is disabled");
            return false;
        }
        
        String recipient = notification.getRecipient();
        String subject = notification.getSubject() != null ? 
                notification.getSubject() : emailProperties.getDefaultSubject();
        String content = notification.getContent();
        boolean isHtml = notification.isHtmlEnabled() != null ? 
                notification.isHtmlEnabled() : emailProperties.isHtmlEnabledByDefault();
        
        // Handle attachments if present
        if (notification.getAttachmentUrl() != null && !notification.getAttachmentUrl().isEmpty()) {
            try {
                // In a real implementation, this would retrieve the attachment from the URL
                // For now, we'll just pass an empty attachment map
                Map<String, byte[]> attachments = Collections.emptyMap();
                logger.debug("Sending email with attachment to {}, subject: {}", recipient, subject);
                return emailProvider.sendEmailWithAttachments(recipient, subject, content, isHtml, attachments);
            } catch (Exception e) {
                logger.error("Failed to send email with attachment to {}", recipient, e);
                return false;
            }
        } else {
            logger.debug("Sending email to {}, subject: {}", recipient, subject);
            return emailProvider.sendEmail(recipient, subject, content, isHtml);
        }
    }
    
    @Override
    public boolean isSupported(Notification notification) {
        return emailProperties.isEnabled() && notification.getChannel() != null && 
               notification.getChannel().name().equals("EMAIL");
    }
}
