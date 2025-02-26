package com.notification.service.delivery;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.notification.config.SmsProperties;
import com.notification.domain.notification.DeliveryChannel;
import com.notification.domain.notification.Notification;
import com.notification.service.delivery.sms.SmsSender;
import com.notification.service.delivery.sms.SmsException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for delivering notifications via SMS.
 * This is a placeholder implementation - in a real application,
 * you would integrate with an SMS provider API like Twilio, Nexmo, etc.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SmsDeliveryService implements DeliveryService {
    
    private final SmsSender smsSender;
    private final SmsProperties smsProperties;
    
    @Override
    public DeliveryChannel getChannel() {
        return DeliveryChannel.SMS;
    }
    
    @Override
    public void deliver(Notification notification) throws DeliveryException {
        if (!isSupported()) {
            throw new DeliveryException("SMS delivery is not configured properly");
        }
        
        try {
            // Use accessor methods
            String content = sanitizeContent(notification.getContent());
            String recipient = sanitizePhoneNumber(notification.getRecipient());
            
            log.info("Sending SMS to {}", recipient);
            
            smsSender.sendSms(recipient, content);
            
        } catch (SmsException e) {
            throw new DeliveryException("Failed to deliver SMS notification", e);
        }
    }
    
    @Override
    public boolean isSupported() {
        return smsSender.isConfigured();
    }
    
    private String sanitizeContent(String content) {
        if (content == null) {
            return "";
        }
        
        // Limit message length
        int maxLength = 160; // Default value if getter not available
        try {
            maxLength = smsProperties.getMaxLength();
        } catch (Exception e) {
            log.warn("Could not access maxLength property, using default value: {}", maxLength);
        }
        
        if (content.length() > maxLength) {
            boolean splitMessages = false; // Default value if getter not available
            try {
                splitMessages = smsProperties.isSplitLongMessages();
            } catch (Exception e) {
                log.warn("Could not access splitLongMessages property, using default value: {}", splitMessages);
            }
            
            if (splitMessages) {
                // Implement message splitting logic if needed
                return content;
            } else {
                // Truncate
                return content.substring(0, maxLength);
            }
        }
        
        return content;
    }
    
    private String sanitizePhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            return "";
        }
        
        // Strip non-numeric characters for E.164 format
        return phoneNumber.replaceAll("[^+0-9]", "");
    }
}