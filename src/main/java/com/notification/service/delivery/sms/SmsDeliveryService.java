package com.notification.service.delivery.sms;

import com.notification.domain.notification.NotificationRecipient;
import com.notification.service.NotificationMessageResolver;
import com.notification.service.delivery.DeliveryException;
import com.notification.service.delivery.DeliveryService;
import org.springframework.stereotype.Service;

import com.notification.config.SmsProperties;
import com.notification.domain.notification.NotificationChannel;
import com.notification.domain.notification.Notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

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
    private final NotificationMessageResolver notificationMessageResolver;

    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.SMS;
    }

    @Override
    public void deliver(Notification notification) throws DeliveryException {
        if (!isSupported()) {
            throw new DeliveryException("SMS delivery is not configured properly");
        }

        try {
            Map<String, String> bulkSmsData = new HashMap<>();
            for (NotificationRecipient recipient : notification.getRecipients()) {
                // Use accessor methods
                String content = sanitizeContent(
                        notificationMessageResolver
                                .resolveMessage(recipient.getMessage(), getChannel()).getContent());
                String phoneNumber = sanitizePhoneNumber(recipient.getAddress().getOrDefault(getChannel(), ""));
                if (!phoneNumber.isEmpty()) {
                    bulkSmsData.put(phoneNumber, content);
                }
            }

            if (!bulkSmsData.isEmpty()) {
                smsSender.sendBulkSms(bulkSmsData);
            }

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