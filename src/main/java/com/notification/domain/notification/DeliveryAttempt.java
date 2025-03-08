package com.notification.domain.notification;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "notification_delivery_attempts",
        indexes = {
                @Index(name = "idx_delivery_notification", columnList = "notification_id"),
                @Index(name = "idx_delivery_recipient", columnList = "recipient_id"),
                @Index(name = "idx_delivery_attempt_time", columnList = "attempt_time"),
                @Index(name = "idx_delivery_status", columnList = "successful"),
                @Index(name = "idx_delivery_next_retry", columnList = "next_retry_time")
        })
public class DeliveryAttempt extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "attempt_id", nullable = false, unique = true)
    private String attemptId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_id", nullable = false)
    private Notification notification;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private NotificationRecipient recipient;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false)
    private NotificationChannel channel;

    @Column(name = "attempt_time", nullable = false)
    private LocalDateTime attemptTime;

    @Column(name = "successful", nullable = false)
    private boolean successful;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "error_code")
    private String errorCode;

    @Column(name = "retry_count")
    private Integer retryCount;

    @Column(name = "next_retry_time")
    private LocalDateTime nextRetryTime;

    @Version
    @Column(name = "version")
    private Long version;

    public DeliveryAttempt() {
        super();
        this.attemptId = UUID.randomUUID().toString();
        this.attemptTime = LocalDateTime.now();
    }

}