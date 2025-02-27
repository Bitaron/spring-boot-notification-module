package com.notification.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * Configuration properties for SMS delivery.
 */
@Configuration
@ConfigurationProperties(prefix = "notification.sms")
@Data
public class SmsProperties {

    /**
     * Enable sms . By default true
     */

    private boolean enabled = true;

    /**
     * Maximum length of SMS messages.
     */
    private int maxLength = 160;

    /**
     * Whether to split long messages into multiple SMS.
     */
    private boolean splitLongMessages = false;

} 