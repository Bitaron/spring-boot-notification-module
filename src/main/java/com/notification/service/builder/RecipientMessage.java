package com.notification.service.builder;

import com.notification.domain.notification.NotificationPriority;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RecipientMessage {
    private final String templateName;
    private final Map<String, Object> templateData;
    private final String rawMessage;
    private final EmailMessage emailMessage;
    private final NotificationPriority priority;

    private RecipientMessage(String templateName,
                             Map<String, Object> templateData,
                             String rawMessage,
                             EmailMessage emailMessage,
                             NotificationPriority priority) {
        this.templateName = templateName;
        this.templateData = templateData != null ? Collections.unmodifiableMap(new HashMap<>(templateData)) : null;
        this.rawMessage = rawMessage;
        this.emailMessage = emailMessage;
        this.priority = priority != null ? priority : NotificationPriority.NORMAL;
    }

    public static RecipientMessage withTemplate(String templateName,
                                                Map<String, Object> templateData,
                                                NotificationPriority priority) {
        if (templateName == null || templateData == null) {
            throw new IllegalArgumentException("Template name and data must not be null");
        }
        return new RecipientMessage(templateName, templateData, null, null, priority);
    }

    public static RecipientMessage withRawMessage(String rawMessage, NotificationPriority priority) {
        if (rawMessage == null || rawMessage.trim().isEmpty()) {
            throw new IllegalArgumentException("Raw message must not be null or empty");
        }
        return new RecipientMessage(null, null, rawMessage, null, priority);
    }

    public static RecipientMessage withEmail(EmailMessage emailMessage, NotificationPriority priority) {
        if (emailMessage == null) {
            throw new IllegalArgumentException("Email message must not be null");
        }
        return new RecipientMessage(null, null, null, emailMessage, priority);
    }

    public String getTemplateName() {
        return templateName;
    }

    public Map<String, Object> getTemplateData() {
        return templateData;
    }

    public String getRawMessage() {
        return rawMessage;
    }

    public EmailMessage getEmailMessage() {
        return emailMessage;
    }

    public NotificationPriority getPriority() {
        return priority;
    }

    public boolean isTemplate() {
        return templateName != null;
    }

    public boolean isEmail() {
        return emailMessage != null;
    }
}