package com.notification.service.delivery.sms.examples;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.notification.config.SmsProperties;
import com.notification.service.delivery.sms.SmsException;
import com.notification.service.delivery.sms.SmsSender;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Example SmsSender implementation using Twilio.
 * This is provided as an example and requires the Twilio SDK to be added to the project.
 */
@Component
@ConditionalOnProperty(name = "notification.sms.provider", havingValue = "twilio")
@RequiredArgsConstructor
@Slf4j
public class TwilioSmsSender implements SmsSender {
    
    private final SmsProperties smsProperties;
    private boolean initialized = false;
    
    private synchronized void initTwilio() {
        if (!initialized) {
            Twilio.init(smsProperties.getApiKey(), smsProperties.getApiSecret());
            initialized = true;
            log.info("Twilio SMS client initialized");
        }
    }
    
    @Override
    public void sendSms(String recipient, String content) throws SmsException {
        try {
            initTwilio();
            
            String from = smsProperties.getSenderId();
            if (from == null || from.isEmpty()) {
                throw new SmsException("Sender ID (from number) not configured for Twilio");
            }
            
            Message message = Message.creator(
                    new PhoneNumber(recipient),
                    new PhoneNumber(from),
                    content)
                    .create();
            
            log.info("Sent SMS via Twilio, SID: {}", message.getSid());
            
        } catch (Exception e) {
            throw new SmsException("Failed to send SMS via Twilio", e);
        }
    }
    
    @Override
    public boolean isConfigured() {
        return smsProperties.getApiKey() != null && 
               !smsProperties.getApiKey().isEmpty() &&
               smsProperties.getApiSecret() != null &&
               !smsProperties.getApiSecret().isEmpty() &&
               smsProperties.getSenderId() != null &&
               !smsProperties.getSenderId().isEmpty();
    }
} 