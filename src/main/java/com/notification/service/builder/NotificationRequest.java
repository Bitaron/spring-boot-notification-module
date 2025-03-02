package com.notification.service.builder;

import com.notification.domain.notification.NotificationChannel;
import com.notification.domain.notification.NotificationPriority;
import com.notification.domain.notification.NotificationType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Immutable class representing a notification request.
 * Created at: 2025-03-02 08:46:21 UTC
 *
 * @author Bitaron
 */
@Data
public class NotificationRequest {
    private final String requestId;
    private final LocalDateTime createdAt;
    private final String createdBy;
    private final NotificationType type;
    private final Set<NotificationChannel> channels;
    private final String sender;
    private final List<Recipient> recipients;
    private final RecipientMessage defaultMessage;
    private final LocalDateTime scheduledTime;
    private final Map<String, String> metadata;
    private final NotificationPriority priority;
    private Set<String> tags = new HashSet<>();
    private Boolean retry = false;
    private Integer maxRetryAttempt;

    /**
     * Creates a new NotificationRequest instance.
     * This constructor is package-private and should only be used by the NotificationBuilder.
     */
    NotificationRequest(NotificationType type,
                        Set<NotificationChannel> channels,
                        String sender,
                        List<Recipient> recipients,
                        RecipientMessage defaultMessage,
                        LocalDateTime scheduledTime,
                        Map<String, String> metadata,
                        NotificationPriority priority,
                        Set<String> tags,
                        Boolean retry,
                        Integer maxRetryAttempt
    ) {
        this.requestId = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.parse("2025-03-02T08:46:21");
        this.createdBy = "Bitaron";
        this.type = type;
        this.channels = Collections.unmodifiableSet(new HashSet<>(channels));
        this.sender = sender;
        this.recipients = Collections.unmodifiableList(new ArrayList<>(recipients));
        this.defaultMessage = defaultMessage;
        this.scheduledTime = scheduledTime;
        this.metadata = Collections.unmodifiableMap(new HashMap<>(metadata));
        this.priority = priority;
        this.tags = tags;
        this.retry = retry;
        this.maxRetryAttempt= maxRetryAttempt;
    }

    /**
     * @return true if this is a scheduled notification
     */
    public boolean isScheduled() {
        return scheduledTime != null;
    }

    /**
     * @return true if this notification uses a default message for all recipients
     */
    public boolean hasDefaultMessage() {
        return defaultMessage != null;
    }

    /**
     * @return true if this notification is meant to be sent immediately
     */
    public boolean isImmediate() {
        return scheduledTime == null;
    }

    /**
     * @return true if this notification uses email channel
     */
    public boolean isEmailNotification() {
        return channels.contains(NotificationChannel.EMAIL);
    }

    /**
     * @return true if this notification uses SMS channel
     */
    public boolean isSmsNotification() {
        return channels.contains(NotificationChannel.SMS);
    }

    /**
     * @return true if this notification uses push channel
     */
    public boolean isPushNotification() {
        return channels.contains(NotificationChannel.PUSH);
    }

    /**
     * @return true if this notification uses web channel
     */
    public boolean isWebNotification() {
        return channels.contains(NotificationChannel.WEB);
    }

    /**
     * Creates a new builder instance for creating notification requests
     *
     * @return a new NotificationBuilder instance
     */
    public static NotificationBuilder builder() {
        return new NotificationBuilder();
    }

    /**
     * @return true if the notification should be processed with high priority
     */
    public boolean isHighPriority() {
        return priority == NotificationPriority.HIGH || priority == NotificationPriority.URGENT;
    }

    /**
     * Gets the message for a specific recipient, either their custom message or the default message
     *
     * @param recipientId the ID of the recipient
     * @return the message for the recipient
     * @throws IllegalArgumentException if the recipient is not found
     */
    public RecipientMessage getMessageForRecipient(String recipientId) {
        Optional<Recipient> recipient = recipients.stream()
                .filter(r -> r.getRecipientId().equals(recipientId))
                .findFirst();

        if (recipient.isPresent()) {
            RecipientMessage message = recipient.get().getMessage();
            return message != null ? message : defaultMessage;
        }
        throw new IllegalArgumentException("Recipient not found: " + recipientId);
    }

    /**
     * @return a map of recipient IDs to their respective messages
     */
    public Map<String, RecipientMessage> getAllMessages() {
        Map<String, RecipientMessage> messages = new HashMap<>();
        for (Recipient recipient : recipients) {
            messages.put(recipient.getRecipientId(),
                    recipient.getMessage() != null ? recipient.getMessage() : defaultMessage);
        }
        return Collections.unmodifiableMap(messages);
    }

    /**
     * @return true if the notification has expired (scheduled time is in the past)
     */
    public boolean hasExpired() {
        return scheduledTime != null && scheduledTime.isBefore(LocalDateTime.now());
    }

    /**
     * @return the number of recipients for this notification
     */
    public int getRecipientCount() {
        return recipients.size();
    }
}