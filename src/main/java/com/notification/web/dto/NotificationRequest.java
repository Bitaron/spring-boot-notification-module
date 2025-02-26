package com.notification.web.dto;

import java.time.LocalDateTime;
import java.util.Map;

import com.notification.domain.notification.DeliveryChannel;
import com.notification.domain.notification.NotificationPriority;
import com.notification.domain.notification.NotificationType;

import lombok.Data;

/**
 * DTO for notification creation requests.
 */
@Data
public class NotificationRequest {
    
    private String recipient;
    private String subject;
    private String content;
    private String templateId;
    private Map<String, Object> templateParams;
    private DeliveryChannel channel;
    private NotificationType type;
    private NotificationPriority priority;
    private boolean htmlEnabled = true;
    private String attachmentUrl;
    private LocalDateTime scheduledFor;
    private String groupId;
} 