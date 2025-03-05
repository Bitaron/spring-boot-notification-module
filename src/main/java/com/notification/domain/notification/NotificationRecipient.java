package com.notification.domain.notification;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Entity
@Table(name = "notification_recipients")
public class NotificationRecipient extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_id", nullable = false)
    private Notification notification;

    @Column(name = "recipient_id", nullable = false)
    private String recipientId;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "message_id")
    private NotificationMessage message;

    @OneToMany(mappedBy = "recipient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeliveryAttempt> deliveryAttempts = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
            name = "recipient_address",
            joinColumns = @JoinColumn(name = "recipient_id")
    )
    @MapKeyColumn(name = "notification_channel")
    @Column(name = "recipient_address")
    private Map<NotificationChannel, String> address = new HashMap<>();

    // Getters and Setters
}