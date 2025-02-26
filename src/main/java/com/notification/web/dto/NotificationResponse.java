package com.notification.web.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.notification.domain.notification.DeliveryChannel;
import com.notification.domain.notification.Notification;
import com.notification.domain.notification.NotificationPriority;
import com.notification.domain.notification.NotificationStatus;
import com.notification.domain.notification.NotificationType;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for notification responses to clients.
 * This prevents sending the entire entity with all internal data to clients.
 */
@Data
@NoArgsConstructor
public class NotificationResponse {
    
    private UUID id;
    private String recipient;
    private String subject;
    private String content;
    private DeliveryChannel channel;
    private NotificationType type;
    private NotificationPriority priority;
    private NotificationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime sentAt;
    private LocalDateTime deliveredAt;
    private String sender;
    private String failureReason;
    
    /**
     * Constructs a response DTO from an entity
     * 
     * @param notification The entity to convert
     */
    public NotificationResponse(Notification notification) {
        this.id = notification.getId();
        this.recipient = notification.getRecipient();
        this.subject = notification.getSubject();
        this.content = notification.getContent();
        this.channel = notification.getChannel();
        this.type = notification.getType();
        this.priority = notification.getPriority();
        this.status = notification.getStatus();
        this.createdAt = notification.getCreatedAt();
        this.sentAt = notification.getSentAt();
        this.sender = notification.getSender();
        
        // Add the new fields with null checks to avoid NPEs
        this.deliveredAt = notification.getDeliveredAt();
        this.failureReason = notification.getFailureReason();
    }
}