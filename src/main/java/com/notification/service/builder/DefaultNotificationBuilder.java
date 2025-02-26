package com.notification.service.builder;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.notification.domain.notification.DeliveryChannel;
import com.notification.domain.notification.Notification;
import com.notification.domain.notification.NotificationPriority;
import com.notification.domain.notification.NotificationStatus;
import com.notification.domain.notification.NotificationType;
import com.notification.service.template.TemplateService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Default implementation of NotificationBuilder that supports building both template-based
 * and direct content notifications.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DefaultNotificationBuilder implements NotificationBuilder {
    
    private final TemplateService templateService;
    private final ObjectMapper objectMapper;
    
    private String recipient;
    private String subject;
    private String content;
    private DeliveryChannel channel;
    private NotificationType type;
    private NotificationPriority priority = NotificationPriority.NORMAL;
    private String templateId;
    private Map<String, Object> templateParams;
    private String groupId;
    private LocalDateTime scheduledFor;
    private String sender;
    private boolean htmlEnabled;
    private String attachmentUrl;
    private String metadata;
    private int maxAttempts = 3;
    
    @Override
    public NotificationBuilder recipient(String recipient) {
        this.recipient = recipient;
        return this;
    }
    
    @Override
    public NotificationBuilder subject(String subject) {
        this.subject = subject;
        return this;
    }
    
    @Override
    public NotificationBuilder content(String content) {
        this.content = content;
        return this;
    }
    
    @Override
    public NotificationBuilder channel(DeliveryChannel channel) {
        this.channel = channel;
        return this;
    }
    
    @Override
    public NotificationBuilder type(NotificationType type) {
        this.type = type;
        return this;
    }
    
    @Override
    public NotificationBuilder priority(NotificationPriority priority) {
        this.priority = priority;
        return this;
    }
    
    @Override
    public NotificationBuilder templateId(String templateId) {
        this.templateId = templateId;
        return this;
    }
    
    @Override
    public NotificationBuilder templateParams(Map<String, Object> templateParams) {
        this.templateParams = templateParams;
        return this;
    }
    
    @Override
    public NotificationBuilder groupId(String groupId) {
        this.groupId = groupId;
        return this;
    }
    
    @Override
    public NotificationBuilder scheduledFor(LocalDateTime scheduledFor) {
        this.scheduledFor = scheduledFor;
        return this;
    }
    
    @Override
    public NotificationBuilder sender(String sender) {
        this.sender = sender;
        return this;
    }
    
    @Override
    public NotificationBuilder htmlEnabled(boolean htmlEnabled) {
        this.htmlEnabled = htmlEnabled;
        return this;
    }
    
    @Override
    public NotificationBuilder attachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
        return this;
    }
    
    @Override
    public NotificationBuilder metadata(String metadata) {
        this.metadata = metadata;
        return this;
    }
    
    @Override
    public NotificationBuilder maxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
        return this;
    }
    
    @Override
    public Notification build() {
        validateRequiredFields();
        
        // Process template if templateId is provided
        if (templateId != null && templateParams != null) {
            String[] processedContent = templateService.processTemplate(templateId, templateParams);
            if (processedContent.length > 0) {
                content = processedContent[0];
            }
            if (processedContent.length > 1 && subject == null) {
                subject = processedContent[1];
            }
        }
        
        // Convert templateParams to metadata if not already set
        if (metadata == null && templateParams != null) {
            try {
                metadata = objectMapper.writeValueAsString(templateParams);
            } catch (JsonProcessingException e) {
                log.warn("Failed to convert template params to JSON metadata", e);
            }
        }
        
        NotificationStatus status = scheduledFor != null && scheduledFor.isAfter(LocalDateTime.now()) ?
                NotificationStatus.SCHEDULED : NotificationStatus.CREATED;
        
        return Notification.builder()
                .id(UUID.randomUUID())
                .recipient(recipient)
                .subject(subject)
                .content(content)
                .type(type)
                .channel(channel)
                .status(status)
                .priority(priority)
                .templateId(templateId)
                .groupId(groupId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .scheduledFor(scheduledFor)
                .sender(sender)
                .htmlEnabled(htmlEnabled)
                .attachmentUrl(attachmentUrl)
                .metadata(metadata)
                .maxAttempts(maxAttempts)
                .attemptCount(0)
                .build();
    }
    
    private void validateRequiredFields() {
        if (recipient == null) {
            throw new IllegalStateException("Recipient is required");
        }
        
        if (channel == null) {
            throw new IllegalStateException("Delivery channel is required");
        }
        
        if (content == null && templateId == null) {
            throw new IllegalStateException("Either content or templateId is required");
        }
        
        if (type == null) {
            throw new IllegalStateException("Notification type is required");
        }
    }
} 