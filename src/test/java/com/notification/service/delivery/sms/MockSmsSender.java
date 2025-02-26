package com.notification.service.delivery.sms;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.notification.config.SmsProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Component
public class MockSmsSender implements SmsSender {
    
    private static final Logger log = LoggerFactory.getLogger(MockSmsSender.class);
    
    private final SmsProperties smsProperties;
    
    @Getter
    private List<SmsMessage> sentMessages = new ArrayList<>();
    
    public MockSmsSender(SmsProperties smsProperties) {
        this.smsProperties = smsProperties;
    }
    
    @Override
    public void sendSms(String recipient, String content) throws SmsException {
        log.info("MOCK SMS: To: {}, Content: {}", recipient, content);
        sentMessages.add(new SmsMessage(recipient, content));
    }
    
    @Override
    public boolean isConfigured() {
        return true; // Always configured for testing
    }
    
    /**
     * Clear all sent messages (for test reset)
     */
    public void clearMessages() {
        sentMessages.clear();
    }
    
    public static class SmsMessage {
        @Getter
        private final String recipient;
        
        @Getter
        private final String content;
        
        public SmsMessage(String recipient, String content) {
            this.recipient = recipient;
            this.content = content;
        }
    }
}