package com.notification.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * Configuration properties for email delivery.
 */
@Configuration
@ConfigurationProperties(prefix = "notification.email")
@Data
public class EmailProperties {
    
    /**
     * Default from address for emails.
     */
    private String fromAddress = "notifications@example.com";
    
    /**
     * Default subject for emails when none is provided.
     */
    private String defaultSubject = "Notification";
    
    /**
     * SMTP host.
     */
    private String smtpHost = "localhost";
    
    /**
     * SMTP port.
     */
    private int smtpPort = 25;
    
    /**
     * SMTP username.
     */
    private String smtpUsername;
    
    /**
     * SMTP password.
     */
    private String smtpPassword;
    
    /**
     * Whether to use SSL for SMTP.
     */
    private boolean smtpSsl = false;
    
    /**
     * Whether to use TLS for SMTP.
     */
    private boolean smtpTls = false;
    
    /**
     * Maximum size for email attachments in MB.
     */
    private int maxAttachmentSizeMb = 10;
    
    /**
     * Whether to enable HTML in email by default.
     */
    private boolean htmlEnabledByDefault = true;
} 