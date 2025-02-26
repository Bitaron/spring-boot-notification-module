package com.notification.service.delivery.sms;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.notification.config.SmsProperties;

@ExtendWith(MockitoExtension.class)
public class DefaultSmsSenderTest {

    @Mock
    private SmsProperties smsProperties;
    
    @InjectMocks
    private DefaultSmsSender smsSender;
    
    @Test
    void testIsConfigured_whenApiKeyIsPresent_returnsTrue() {
        when(smsProperties.getApiKey()).thenReturn("test-api-key");
        
        assertTrue(smsSender.isConfigured());
    }
    
    @Test
    void testIsConfigured_whenApiKeyIsEmpty_returnsFalse() {
        when(smsProperties.getApiKey()).thenReturn("");
        
        assertFalse(smsSender.isConfigured());
    }
    
    @Test
    void testIsConfigured_whenApiKeyIsNull_returnsFalse() {
        when(smsProperties.getApiKey()).thenReturn(null);
        
        assertFalse(smsSender.isConfigured());
    }
    
    @Test
    void testSendSms_doesNotThrowException() {
        assertDoesNotThrow(() -> smsSender.sendSms("+1234567890", "Test message"));
    }
} 