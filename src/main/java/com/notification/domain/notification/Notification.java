package com.notification.domain.notification;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "notification_notifications")
public class Notification extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "notification_id", nullable = false, unique = true)
    private String notificationId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private NotificationType type;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "notification_channels",
            joinColumns = @JoinColumn(name = "notification_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "channel")
    private Set<NotificationChannel> channels = new HashSet<>();

    @Column(name = "sender", nullable = false)
    private String sender;

    @OneToMany(mappedBy = "notification", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<NotificationRecipient> recipients = new HashSet<>();

    @Column(name = "scheduled_time")
    private LocalDateTime scheduledTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private NotificationStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private NotificationPriority priority;

    @Column(name = "time_to_live")
    private Long timeToLiveSeconds;

    @Column(name = "max_retries")
    private Integer maxRetries;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "notification_metadata",
            joinColumns = @JoinColumn(name = "notification_id")
    )
    @MapKeyColumn(name = "metadata_key")
    @Column(name = "metadata_value")
    private Map<String, String> metadata = new HashMap<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "notification_tags",
            joinColumns = @JoinColumn(name = "notification_id")
    )
    @Column(name = "tag")
    private Set<String> tags = new HashSet<>();

    // Getters and Setters
}