package com.notification.service.delivery.sms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.notification.config.NotificationProperties.SmsProperties;
import com.notification.domain.notification.Notification;
import com.notification.service.delivery.DeliveryService;

/**
 * Service for delivering SMS notifications.
 * This service will only be instantiated if SMS notifications are enabled.
 */
public class SmsDeliveryService implements DeliveryService {

    private static final Logger logger = LoggerFactory.getLogger(SmsDeliveryService.class);
    
    private final SmsDeliveryProvider smsProvider;
    private final SmsProperties smsProperties;
    
    public SmsDeliveryService(SmsDeliveryProvider smsProvider, SmsProperties smsProperties) {
        this.smsProvider = smsProvider;
        this.smsProperties = smsProperties;
        logger.info("SMS delivery service initialized with provider: {}", smsProvider.getClass().getName());
    }
    
    @Override
    public boolean deliver(Notification notification) {
        if (!smsProperties.isEnabled()) {
            logger.warn("Attempted to send SMS but SMS channel is disabled");
            return false;
        }
        
        String recipient = notification.getRecipient();
        String content = notification.getContent();
        
        // Apply SMS length limits if needed
        if (content.length() > smsProperties.getMaxLength() && !smsProperties.isSplitLongMessages()) {
            content = content.substring(0, smsProperties.getMaxLength());
            logger.warn("SMS content truncated to {} characters for notification {}", 
                    smsProperties.getMaxLength(), notification.getId());
        }
        
        logger.debug("Sending SMS to {}, content length: {}", recipient, content.length());
        return smsProvider.sendSms(recipient, content);
    }
    
    @Override
    public boolean isSupported(Notification notification) {
        return smsProperties.isEnabled() && notification.getChannel() != null && 
               notification.getChannel().name().equals("SMS");
    }
}
