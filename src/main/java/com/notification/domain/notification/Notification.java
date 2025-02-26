package com.notification.domain.notification;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Index;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a notification.
 */
@Entity
@Table(name = "notifications", indexes = {
    @Index(name = "idx_notifications_recipient", columnList = "recipient"),
    @Index(name = "idx_notifications_status", columnList = "status"),
    @Index(name = "idx_notifications_scheduled_for", columnList = "scheduledFor")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    
    @Id
    @GeneratedValue
    private UUID id;
    
    /**
     * The recipient to whom the notification is addressed.
     */
    @Column(nullable = false)
    private String recipient;
    
    /**
     * The subject or title of the notification.
     */
    @Column
    private String subject;
    
    /**
     * The content of the notification.
     */
    @Lob
    @Column(nullable = false)
    private String content;
    
    /**
     * The type of notification.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;
    
    /**
     * The delivery channel for this notification.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryChannel channel;
    
    /**
     * The current status of the notification.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status;
    
    /**
     * The priority of the notification.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationPriority priority;
    
    /**
     * Template identifier used to generate this notification.
     */
    private String templateId;
    
    /**
     * Reference to a grouping of notifications, if any.
     */
    private String groupId;
    
    /**
     * When the notification was created.
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    /**
     * When the notification's status was last updated.
     */
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    /**
     * When the notification should be sent, for scheduled notifications.
     */
    @Column
    private LocalDateTime scheduledFor;
    
    /**
     * When the notification was sent.
     */
    @Column
    private LocalDateTime sentAt;
    
    /**
     * When the notification was delivered.
     */
    @Column
    private LocalDateTime deliveredAt;
    
    /**
     * When the notification was read by the recipient.
     */
    @Column
    private LocalDateTime readAt;
    
    /**
     * Additional data stored as JSON.
     */
    @Column(columnDefinition = "TEXT")
    private String metadata;
    
    /**
     * Number of delivery attempts.
     */
    @Column(nullable = false)
    private int attemptCount;
    
    /**
     * Maximum number of retry attempts.
     */
    private int maxAttempts;
    
    /**
     * Next scheduled retry time.
     */
    @Column
    private LocalDateTime nextRetryAt;
    
    /**
     * Path to attachment, if any.
     */
    @Column
    private String attachmentUrl;
    
    /**
     * The sender's identifier.
     */
    private String sender;
    
    /**
     * Whether HTML formatting is enabled for this notification.
     */
    @Column(nullable = false)
    private Boolean htmlEnabled;
    
    /**
     * Failure reason for notification delivery failure.
     */
    @Column
    private String failureReason;
    
    /**
     * Pre-persist hook to initialize default values.
     */
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = NotificationStatus.PENDING;
        }
        if (priority == null) {
            priority = NotificationPriority.NORMAL;
        }
        updatedAt = LocalDateTime.now();
    }
} 