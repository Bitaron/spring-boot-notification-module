package com.notification.service.delivery.sms;

import org.springframework.stereotype.Component;

import com.notification.config.SmsProperties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Default SMS sender implementation that simply logs messages.
 * This is used when no other SMS provider is configured.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DefaultSmsSender implements SmsSender {
    
    private final SmsProperties smsProperties;
    
    @Override
    public void sendSms(String recipient, String content) throws SmsException {
        log.info("MOCK SMS: To: {}, Content: {}", recipient, content);
        log.info("No actual SMS was sent - this is the default mock implementation");
    }
    
    @Override
    public boolean isConfigured() {
        try {
            // Only consider as configured if explicit API credentials are provided
            String apiKey = smsProperties.getApiKey();
            return apiKey != null && !apiKey.isEmpty();
        } catch (Exception e) {
            log.warn("Could not access SMS API key, service will be considered not configured", e);
            return false;
        }
    }
}