package com.notification.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import com.notification.service.delivery.sms.SmsSender;
import com.notification.service.delivery.sms.MockSmsSender;
import com.notification.config.SmsProperties;

@TestConfiguration
public class TestSmsConfig {
    
    @Bean
    @Primary
    public SmsSender smsSender(SmsProperties smsProperties) {
        return new MockSmsSender(smsProperties);
    }
}