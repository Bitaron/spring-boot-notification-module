package com.notification.service;

import com.notification.domain.notification.*;
import com.notification.exception.NotificationException;
import com.notification.queue.NotificationQueueSender;
import com.notification.repository.NotificationRepository;
import com.notification.service.builder.EmailMessage;
import com.notification.service.builder.NotificationRequest;
import com.notification.service.builder.Recipient;
import com.notification.service.builder.RecipientMessage;
import com.notification.service.delivery.DeliveryService;
import com.notification.service.delivery.DeliveryServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;
    private final DeliveryServiceFactory deliveryServiceFactory;
    private final NotificationQueueSender notificationQueueSender;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository,
                               DeliveryServiceFactory deliveryServiceFactory,
                               boolean useMessageQueue, NotificationQueueSender notificationQueueSender) {
        this.notificationRepository = notificationRepository;
        this.deliveryServiceFactory = deliveryServiceFactory;
        this.notificationQueueSender = notificationQueueSender;
    }

    @Transactional
    public String sendNotification(NotificationRequest request) {
        Notification entity = saveNotification(request);

        if (notificationQueueSender != null) {
            notificationQueueSender.sendNotification(entity);
        } else {
            processNotificationAsync(entity.getNotificationId());
        }

        return entity.getNotificationId();
    }

    // Convenient methods for common notification scenarios
    @Transactional
    public String sendSimpleEmail(String to, String subject, String content) {
        NotificationRequest request = NotificationRequest.builder()
                .setType(NotificationType.TRANSACTIONAL)
                .addChannel(NotificationChannel.EMAIL)
                .setSender("system@example.com")
                .forRecipientWithEmail(to, subject, content, content, NotificationPriority.NORMAL)
                .build();

        return sendNotification(request);
    }

    @Transactional
    public String sendUrgentAlert(String to, String subject, String content) {
        NotificationRequest request = NotificationRequest.builder()
                .setType(NotificationType.ALERT)
                .addChannel(NotificationChannel.EMAIL)
                .addChannel(NotificationChannel.SMS)
                .setSender("alerts@example.com")
                .forRecipientWithEmail(to, subject, content, content, NotificationPriority.URGENT)
                .build();

        return sendNotification(request);
    }

    @Transactional
    public String sendBulkEmail(List<String> recipients, String subject, String content) {
        NotificationRequest request = NotificationRequest.builder()
                .setType(NotificationType.MARKETING)
                .addChannel(NotificationChannel.EMAIL)
                .setSender("marketing@example.com")
                .forGroupWithEmail(recipients, subject, content, content, NotificationPriority.LOW)
                .build();

        return sendNotification(request);
    }

    @Async
    protected CompletableFuture<Void> processNotificationAsync(String notificationId) {
        return CompletableFuture.runAsync(() -> {
            try {
                processNotification(notificationId);
            } catch (Exception e) {
                logger.error("Error processing notification: " + notificationId, e);
                updateNotificationStatus(notificationId, NotificationStatus.FAILED);
            }
        });
    }

    @Transactional
    public void processNotification(String notificationId) {
        Notification notification = notificationRepository.findByNotificationId(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found: " + notificationId));

        updateNotificationStatus(notificationId, NotificationStatus.PROCESSING);

        for (NotificationChannel channel : notification.getChannels()) {
            for (NotificationRecipient recipient : notification.getRecipients()) {
                try {
                    sendByChannel(channel, notification, recipient);
                } catch (Exception e) {
                    logger.error("Error sending notification to recipient: " + recipient.getRecipientId(), e);
                    recordDeliveryAttempt(notification, recipient, channel, false, e.getMessage());
                }
            }
        }

        updateNotificationStatus(notificationId, NotificationStatus.DELIVERED);
    }

    private void sendByChannel(NotificationChannel channel,
                               Notification notification,
                               NotificationRecipient recipient) {
        try {
            DeliveryService deliveryService = deliveryServiceFactory.getDeliveryService(channel);
            deliveryService.deliver(notification);
            recordDeliveryAttempt(notification, recipient, channel, true, null);
        } catch (Exception e) {
            throw new NotificationException("Failed to send notification via " + channel, e);
        }
    }

    private Notification saveNotification(NotificationRequest request) {
        Notification entity = new Notification();
        entity.setNotificationId(UUID.randomUUID().toString());
        entity.setType(request.getType());
        entity.setChannels(request.getChannels());
        entity.setSender(request.getSender());
        entity.setScheduledTime(request.getScheduledTime());
        entity.setStatus(request.getScheduledTime() != null ?
                NotificationStatus.SCHEDULED : NotificationStatus.PENDING);
        entity.setPriority(request.getPriority());

        // Set creation details
        entity.setCreatedAt(LocalDateTime.parse("2025-03-02T09:43:30"));
        entity.setCreatedBy("Bitaron");

        // Save recipients
        for (Recipient recipient : request.getRecipients()) {
            NotificationRecipient recipientEntity = new NotificationRecipient();
            recipientEntity.setRecipientId(recipient.getRecipientId());
            recipientEntity.setNotification(entity);

            // Save message
            if (recipient.getMessage() != null) {
                NotificationMessage messageEntity = createMessageEntity(recipient.getMessage());
                recipientEntity.setMessage(messageEntity);
            }

            entity.getRecipients().add(recipientEntity);
        }

        return notificationRepository.save(entity);
    }

    private NotificationMessage createMessageEntity(RecipientMessage message) {
        NotificationMessage entity = new NotificationMessage();

        if (message.isTemplate()) {
            entity.setTemplateName(message.getTemplateName());
            entity.setTemplateData(new HashMap<>(message.getTemplateData()));
        } else if (message.isEmail()) {
            EmailMessage emailMessage = message.getEmailMessage();
            entity.setSubject(emailMessage.getSubject());
            entity.setIsHtml(true);
            //  entity.setPlainTextContent(emailMessage.getPlainTextContent());
        } else {
            entity.setRawMessage(message.getRawMessage());
        }

        return entity;
    }

    private void recordDeliveryAttempt(Notification notification,
                                       NotificationRecipient recipient,
                                       NotificationChannel channel,
                                       boolean successful,
                                       String errorMessage) {
        DeliveryAttempt attempt = new DeliveryAttempt();
        attempt.setNotification(notification);
        attempt.setRecipient(recipient);
        attempt.setChannel(channel);
        attempt.setSuccessful(successful);
        attempt.setErrorMessage(errorMessage);

        recipient.getDeliveryAttempts().add(attempt);
    }

    @Transactional
    protected void updateNotificationStatus(String notificationId, NotificationStatus status) {
        notificationRepository.updateStatus(notificationId, status,
                LocalDateTime.parse("2025-03-02T09:43:30"), "Bitaron");
    }
}