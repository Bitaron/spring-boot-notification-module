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
import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
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
                               @Nullable NotificationQueueSender notificationQueueSender) {
        this.notificationRepository = notificationRepository;
        this.deliveryServiceFactory = deliveryServiceFactory;
        this.notificationQueueSender = notificationQueueSender;
    }


    @Transactional
    public String sendNotification(NotificationRequest request) {
        request.setNotificationId(UUID.randomUUID().toString());
        if (notificationQueueSender != null) {
            notificationQueueSender.sendNotification(request);
        } else {
            processNotificationAsync(request);
        }

        return request.getNotificationId();
    }


    @Transactional
    public String sendSms(String to, String content) {
        Map<NotificationChannel, String> address = new HashMap<>();
        address.put(NotificationChannel.SMS, to);
        NotificationRequest request = NotificationRequest.builder()
                .setType(NotificationType.INFO)
                .addChannel(NotificationChannel.SMS)
                .setPriority(NotificationPriority.NORMAL)
                .forRecipientWithRawMessage(to, address, content)
                .build();

        return sendNotification(request);
    }

    @Transactional
    public String sendSms(String to, String templateCode, Map<String, Object> templateData) {
        Map<NotificationChannel, String> address = new HashMap<>();
        address.put(NotificationChannel.SMS, to);
        NotificationRequest request = NotificationRequest.builder()
                .setType(NotificationType.INFO)
                .addChannel(NotificationChannel.SMS)
                .setPriority(NotificationPriority.NORMAL)
                .forRecipientWithTemplate(to, address, templateCode, templateData)
                .build();

        return sendNotification(request);
    }

    @Transactional
    public String sendWebNotification(String from, String to, String content) {
        Map<NotificationChannel, String> address = new HashMap<>();
        address.put(NotificationChannel.WEB, to);
        NotificationRequest request = NotificationRequest.builder()
                .setType(NotificationType.INFO)
                .addChannel(NotificationChannel.WEB)
                .setPriority(NotificationPriority.NORMAL)
                .setSender(from)
                .forRecipientWithRawMessage(to, address, content)
                .build();

        return sendNotification(request);
    }

    @Transactional
    public String sendWebNotification(String from, String to, String template, Map<String, Object> templateData) {
        Map<NotificationChannel, String> address = new HashMap<>();
        address.put(NotificationChannel.WEB, to);
        NotificationRequest request = NotificationRequest.builder()
                .setType(NotificationType.INFO)
                .addChannel(NotificationChannel.WEB)
                .setPriority(NotificationPriority.NORMAL)
                .setSender(from)
                .forRecipientWithTemplate(to, address, template, templateData)
                .build();

        return sendNotification(request);
    }


    @Transactional
    public String sendEmail(String to, String subject, String content, boolean isHtml, Set<String> attachmentUrls) {
        Map<NotificationChannel, String> address = new HashMap<>();
        address.put(NotificationChannel.EMAIL, to);
        NotificationRequest request = NotificationRequest.builder()
                .setType(NotificationType.INFO)
                .addChannel(NotificationChannel.EMAIL)
                .setPriority(NotificationPriority.NORMAL)
                .forRecipientWithEmail(to, address, subject, content, isHtml, attachmentUrls)
                .build();

        return sendNotification(request);
    }

/*

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
*/

    @Async
    protected CompletableFuture<Void> processNotificationAsync(NotificationRequest request) {
        return CompletableFuture.runAsync(() -> {
            try {
                processNotification(request);
            } catch (Exception e) {
                logger.error("Error processing notification: " + request.getNotificationId(), e);
                updateNotificationStatus(request.getNotificationId(), NotificationStatus.FAILED);
            }
        });
    }


    @Transactional
    public void processNotification(NotificationRequest request) {
        Notification notification = saveNotification(request);
       /* Notification notification = notificationRepository.findByNotificationId(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found: " + notificationId));
*/
        updateNotificationStatus(notification, NotificationStatus.PROCESSING);

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

        updateNotificationStatus(notification, NotificationStatus.DELIVERED);
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
        entity.setNotificationId(request.getNotificationId());
        entity.setType(request.getType());
        entity.setChannels(request.getChannels());
        entity.setSender(request.getSender());
        entity.setScheduledTime(request.getScheduledTime());
        entity.setStatus(request.getScheduledTime() != null ?
                NotificationStatus.SCHEDULED : NotificationStatus.PENDING);
        entity.setPriority(request.getPriority());


        // Save recipients
        for (Recipient recipient : request.getRecipients()) {
            NotificationRecipient recipientEntity = new NotificationRecipient();
            recipientEntity.setRecipientId(recipient.getRecipientId());
            recipientEntity.setNotification(entity);
            recipientEntity.setAddress(recipient.getAddress());

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
            entity.setRawMessage(emailMessage.getRawMessage());
            entity.setIsHtml(emailMessage.isHtml());
            entity.setAttachmentUrls(emailMessage.getAttachmentUrls());
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
    protected void updateNotificationStatus(Notification notification, NotificationStatus status) {
        notification.setStatus(status);
        notificationRepository.save(notification);
    }

    @Transactional
    protected void updateNotificationStatus(String notificationId, NotificationStatus notificationStatus) {
        Notification notification = notificationRepository.findByNotificationId(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found: " + notificationId));
        updateNotificationStatus(notification, notificationStatus);
    }
}