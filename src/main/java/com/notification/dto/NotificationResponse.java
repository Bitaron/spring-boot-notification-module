package com.notification.dto;

import com.notification.domain.notification.*;
import com.notification.service.NotificationMessageResolver;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for notification responses to clients.
 * This prevents sending the entire entity with all internal data to clients.
 */
@Data
@NoArgsConstructor
public class NotificationResponse {

    private Long id;
    private String recipient;
    private String subject;
    private String content;
    private NotificationChannel channel;
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
     * @param notificationRecipient The entity to convert
     */
    public NotificationResponse(NotificationRecipient notificationRecipient,
                                NotificationMessageResolver.NotificationContent notificationContent,
                                NotificationChannel channel) {
        this.id = notificationRecipient.getId();
        this.recipient = notificationRecipient.getRecipientId();
        this.subject = notificationRecipient.getMessage().getSubject();
        this.content = notificationContent.getContent();
        this.channel = channel;
        this.type = notificationRecipient.getNotification().getType();
        this.priority = notificationRecipient.getNotification().getPriority();
        this.status = notificationRecipient.getNotification().getStatus();
        this.createdAt = notificationRecipient.getNotification().getCreatedAt();
        //this.sentAt = notificationRecipient.getNotification().getse
        this.sender = notificationRecipient.getNotification().getSender();

 /*       // Add the new fields with null checks to avoid NPEs
        this.deliveredAt = notificationRecipient.getNotification().getDeliveredAt();
        this.failureReason = notificationRecipient.getNotification().getFailureReason();*/
    }
}