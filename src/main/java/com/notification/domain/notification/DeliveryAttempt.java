package com.notification.domain.notification;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "delivery_attempts")
public class DeliveryAttempt extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "attempt_id", nullable = false, unique = true)
    private String attemptId;

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

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "error_code")
    private String errorCode;

    @Column(name = "retry_count")
    private Integer retryCount;

    @Column(name = "next_retry_time")
    private LocalDateTime nextRetryTime;

    // Getters and Setters
}