package com.notification.service.delivery.sms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.notification.config.SmsProperties;

import lombok.Getter;

@Component
@ConditionalOnProperty(name = "notification.sms.provider", havingValue = "test", matchIfMissing = false)
public class TestSmsSender implements SmsSender {
    
    private static final Logger log = LoggerFactory.getLogger(TestSmsSender.class);
    
    private final SmsProperties smsProperties;
    
    @Getter
    private String lastRecipient;
    
    @Getter
    private String lastContent;
    
    public TestSmsSender(SmsProperties smsProperties) {
        this.smsProperties = smsProperties;
    }
    
    @Override
    public void sendSms(String recipient, String content) throws SmsException {
        log.info("TEST SMS SENDER: To: {}, Content: {}", recipient, content);
        this.lastRecipient = recipient;
        this.lastContent = content;
    }
    
    @Override
    public boolean isConfigured() {
        return true; // Always configured for testing
    }
}