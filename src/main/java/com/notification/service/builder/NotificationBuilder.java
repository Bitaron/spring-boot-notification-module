package com.notification.service.builder;

import com.notification.domain.notification.NotificationChannel;
import com.notification.domain.notification.NotificationPriority;
import com.notification.domain.notification.NotificationType;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class NotificationBuilder {
    private NotificationType type;
    private final Set<NotificationChannel> channels;
    private String sender;
    private final List<Recipient> recipients;
    private RecipientMessage defaultMessage;
    private LocalDateTime scheduledTime;
    private final Map<String, String> metadata;
    private NotificationPriority priority;
    private Set<String> tags = new HashSet<>();
    private Boolean retry = false;
    private Integer maxRetryAttempt;

    public NotificationBuilder() {
        this.channels = new HashSet<>();
        this.recipients = new ArrayList<>();
        this.metadata = new HashMap<>();
        this.priority = NotificationPriority.NORMAL; // Default priority
    }

    public NotificationBuilder setType(NotificationType type) {
        this.type = type;
        return this;
    }

    public NotificationBuilder addChannel(NotificationChannel channel) {
        this.channels.add(channel);
        return this;
    }

    public NotificationBuilder addChannels(NotificationChannel... channels) {
        Collections.addAll(this.channels, channels);
        return this;
    }

    public NotificationBuilder setSender(String sender) {
        this.sender = sender;
        return this;
    }

    public NotificationBuilder setPriority(NotificationPriority priority) {
        this.priority = priority;
        return this;
    }

    // Email specific builders
    public NotificationBuilder forRecipientWithEmail(String recipientId,
                                                     String subject,
                                                     String htmlContent,
                                                     String plainTextContent,
                                                     NotificationPriority priority) {
        EmailMessage emailMessage = new EmailMessage.Builder()
                .setSubject(subject)
                .setHtmlContent(htmlContent)
                .setPlainTextContent(plainTextContent)
                .build();

        Recipient recipient = new Recipient.Builder(recipientId)
                .setMessage(RecipientMessage.withEmail(emailMessage, priority))
                .build();

        this.recipients.clear();
        this.recipients.add(recipient);
        this.priority = priority;
        return this;
    }

    public NotificationBuilder forGroupWithEmail(List<String> recipientIds,
                                                 String subject,
                                                 String htmlContent,
                                                 String plainTextContent,
                                                 NotificationPriority priority) {
        EmailMessage emailMessage = new EmailMessage.Builder()
                .setSubject(subject)
                .setHtmlContent(htmlContent)
                .setPlainTextContent(plainTextContent)
                .build();

        this.recipients.clear();
        this.defaultMessage = RecipientMessage.withEmail(emailMessage, priority);
        this.priority = priority;

        for (String recipientId : recipientIds) {
            this.recipients.add(new Recipient.Builder(recipientId).build());
        }
        return this;
    }

    public NotificationBuilder addRecipientWithCustomEmail(String recipientId,
                                                           String subject,
                                                           String htmlContent,
                                                           String plainTextContent,
                                                           NotificationPriority priority) {
        EmailMessage emailMessage = new EmailMessage.Builder()
                .setSubject(subject)
                .setHtmlContent(htmlContent)
                .setPlainTextContent(plainTextContent)
                .build();

        this.recipients.add(new Recipient.Builder(recipientId)
                .setMessage(RecipientMessage.withEmail(emailMessage, priority))
                .build());
        return this;
    }

    // Template based builders
    public NotificationBuilder forRecipientWithTemplate(String recipientId,
                                                        String templateName,
                                                        Map<String, Object> templateData,
                                                        NotificationPriority priority) {
        Recipient recipient = new Recipient.Builder(recipientId)
                .setMessage(RecipientMessage.withTemplate(templateName, templateData, priority))
                .build();
        this.recipients.clear();
        this.recipients.add(recipient);
        this.priority = priority;
        return this;
    }

    public NotificationBuilder forGroupWithTemplate(List<String> recipientIds,
                                                    String templateName,
                                                    Map<String, Object> templateData,
                                                    NotificationPriority priority) {
        this.recipients.clear();
        this.defaultMessage = RecipientMessage.withTemplate(templateName, templateData, priority);
        this.priority = priority;

        for (String recipientId : recipientIds) {
            this.recipients.add(new Recipient.Builder(recipientId).build());
        }
        return this;
    }

    // Raw message builders
    public NotificationBuilder forRecipientWithRawMessage(String recipientId,
                                                          Map<NotificationChannel, String> address,
                                                          String rawMessage) {
        Recipient recipient = new Recipient.Builder(recipientId, address)
                .setMessage(RecipientMessage.withRawMessage(rawMessage, priority))
                .build();
        this.recipients.clear();
        this.recipients.add(recipient);
        return this;
    }

    public NotificationBuilder forGroupWithRawMessage(List<String> recipientIds,
                                                      String rawMessage,
                                                      NotificationPriority priority) {
        this.recipients.clear();
        this.defaultMessage = RecipientMessage.withRawMessage(rawMessage, priority);
        this.priority = priority;

        for (String recipientId : recipientIds) {
            this.recipients.add(new Recipient.Builder(recipientId).build());
        }
        return this;
    }

    public NotificationBuilder scheduleFor(LocalDateTime scheduledTime) {
        this.scheduledTime = scheduledTime;
        return this;
    }

    public NotificationBuilder addMetadata(String key, String value) {
        this.metadata.put(key, value);
        return this;
    }

    public NotificationBuilder addMetadata(Map<String, String> metadata) {
        this.metadata.putAll(metadata);
        return this;
    }

    public NotificationBuilder addTag(String tag) {
        this.tags.add(tag);
        return this;
    }

    public NotificationBuilder retry() {
        this.retry = true;
        return this;
    }

    public NotificationBuilder maxRetryAttempt(Integer maxRetryAttempt) {
        this.maxRetryAttempt = maxRetryAttempt;
        return this;
    }

    private void validate() {
        List<String> errors = new ArrayList<>();

        if (type == null) {
            errors.add("Notification type is required");
        }

        if (channels.isEmpty()) {
            errors.add("At least one notification channel is required");
        }

        /*if (sender == null || sender.trim().isEmpty()) {
            errors.add("Sender is required");
        }
*/
        if (recipients.isEmpty()) {
            errors.add("At least one recipient is required");
        }

        // Validate email specific requirements
        if (channels.contains(NotificationChannel.EMAIL)) {
            validateEmailRequirements(errors);
        }

        boolean hasDefaultMessage = defaultMessage != null;
        boolean allRecipientsHaveMessages = recipients.stream()
                .allMatch(r -> r.getMessage() != null);
        boolean noRecipientsHaveMessages = recipients.stream()
                .noneMatch(r -> r.getMessage() != null);

        if (!hasDefaultMessage && !allRecipientsHaveMessages) {
            errors.add("Either all recipients must have messages or a default message must be provided");
        }

        if (hasDefaultMessage && !noRecipientsHaveMessages) {
            errors.add("Cannot mix default message with recipient-specific messages");
        }

        if (!errors.isEmpty()) {
            throw new IllegalStateException("Invalid notification request: " +
                    errors.stream().collect(Collectors.joining(", ")));
        }
    }

    private void validateEmailRequirements(List<String> errors) {
        if (defaultMessage != null && defaultMessage.isEmail()) {
            if (defaultMessage.getEmailMessage().getSubject() == null) {
                errors.add("Email subject is required");
            }
        } else {
            for (Recipient recipient : recipients) {
                RecipientMessage message = recipient.getMessage();
                if (message != null && message.isEmail() &&
                        message.getEmailMessage().getSubject() == null) {
                    errors.add("Email subject is required for recipient: " + recipient.getRecipientId());
                }
            }
        }
    }

    public NotificationRequest build() {
        validate();
        return new NotificationRequest(
                type,
                channels,
                sender,
                recipients,
                defaultMessage,
                scheduledTime,
                metadata,
                priority,
                tags,
                retry,
                maxRetryAttempt
        );
    }
}