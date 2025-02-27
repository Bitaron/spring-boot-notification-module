package com.notification.service;

import com.notification.domain.notification.DeliveryChannel;
import com.notification.domain.notification.Notification;
import com.notification.domain.notification.NotificationStatus;
import com.notification.domain.notification.NotificationType;
import com.notification.repository.NotificationRepository;
import com.notification.service.builder.NotificationBuilder;
import com.notification.service.delivery.DeliveryService;
import com.notification.service.delivery.DeliveryServiceFactoryImpl;
import com.notification.service.delivery.web.DeliveryServiceFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Service for managing and sending notifications.
 */
@Service
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final DeliveryServiceFactory deliveryServiceFactory;
    public final List<DeliveryService> deliveryServices;

    public NotificationService(NotificationRepository notificationRepository, List<DeliveryService> deliveryServices) {
        this.notificationRepository = notificationRepository;
        this.deliveryServices = deliveryServices;
        this.deliveryServiceFactory = new DeliveryServiceFactoryImpl(deliveryServices);
    }

    /**
     * Creates a new notification builder.
     *
     * @return A notification builder instance
     */
    public NotificationBuilder createNotificationBuilder() {
        // Implementation needed
        throw new UnsupportedOperationException("Method not implemented");
    }

    /**
     * Sends a notification.
     *
     * @param notification The notification to send
     * @return The sent notification with updated status
     */
    @Transactional
    public Notification sendNotificationImmediately(Notification notification) {
        Notification savedNotification = createNotification(notification);
        sendNotification(savedNotification.getId());
        return notificationRepository.findById(savedNotification.getId()).orElse(savedNotification);
    }

    /**
     * Sends a notification built from the provided builder.
     *
     * @return The sent notification with updated status
     */
    @Transactional
    public void sendNotification(UUID notificationId) {
        Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);

        if (notificationOpt.isPresent()) {
            Notification notification = notificationOpt.get();

            try {
                // Update status to sending
                notification.setStatus(NotificationStatus.SENDING);
                notification = notificationRepository.save(notification);

                // Get the appropriate delivery service
                DeliveryService deliveryService = deliveryServiceFactory.getDeliveryService(notification);

                // Deliver notification
                deliveryService.deliver(notification);


                // Update status to sent
                notification.setStatus(NotificationStatus.SENT);
                notification.setSentAt(LocalDateTime.now());
                notification = notificationRepository.save(notification);

                log.info("Notification sent: {}", notificationId);

            } catch (Exception e) {
                notification.setStatus(NotificationStatus.FAILED);
                notification.setFailureReason(e.getMessage());
                notification.setAttemptCount(notification.getAttemptCount() + 1);
                notificationRepository.save(notification);

                log.error("Error sending notification: {}", notificationId, e);
            }
        } else {
            log.warn("Notification not found: {}", notificationId);
        }
    }

    /**
     * Sends a template-based notification.
     *
     * @param recipient      The notification recipient
     * @param templateCode   The template code to use
     * @param templateParams The template parameters
     * @param channel        The delivery channel
     * @return The sent notification with updated status
     */
    @Transactional
    public Notification sendWithTemplate(String recipient, String templateCode,
                                         Map<String, Object> templateParams, DeliveryChannel channel) {
        // Implementation needed
        throw new UnsupportedOperationException("Method not implemented");
    }

    /**
     * Creates a notification but doesn't send it immediately.
     *
     * @param notification The notification to create
     * @return The created notification
     */
    public Notification createNotification(Notification notification) {
        notification.setCreatedAt(LocalDateTime.now());
        notification.setStatus(NotificationStatus.PENDING);
        return notificationRepository.save(notification);
    }

    /**
     * Gets a notification by its ID.
     *
     * @param id The notification ID
     * @return The notification, if found
     */
    @Transactional(readOnly = true)
    public Optional<Notification> getNotification(UUID id) {
        return notificationRepository.findById(id);
    }

    /**
     * Updates the status of a notification.
     *
     * @param id     The notification ID
     * @param status The new status
     * @return The updated notification
     */
    @Transactional
    public Notification updateStatus(UUID id, NotificationStatus status) {
        Optional<Notification> notificationOpt = notificationRepository.findById(id);

        if (notificationOpt.isPresent()) {
            Notification notification = notificationOpt.get();
            notification.setStatus(status);
            notification.setUpdatedAt(LocalDateTime.now());
            return notificationRepository.save(notification);
        } else {
            throw new IllegalArgumentException("Notification not found");
        }
    }

    /**
     * Marks a notification as read.
     *
     * @param id The notification ID
     * @return The updated notification
     */
    @Transactional
    public Notification markAsRead(UUID id) {
        Optional<Notification> notificationOpt = notificationRepository.findById(id);

        if (notificationOpt.isPresent()) {
            Notification notification = notificationOpt.get();
            notification.setStatus(NotificationStatus.READ);
            notification.setReadAt(LocalDateTime.now());
            notification.setUpdatedAt(LocalDateTime.now());
            return notificationRepository.save(notification);
        } else {
            throw new IllegalArgumentException("Notification not found");
        }
    }

    /**
     * Cancels a scheduled notification.
     *
     * @param id The notification ID
     * @return The updated notification
     */
    @Transactional
    public Notification cancelNotification(UUID id) {
        Optional<Notification> notificationOpt = notificationRepository.findById(id);

        if (notificationOpt.isPresent()) {
            Notification notification = notificationOpt.get();

            // Only pending or scheduled notifications can be canceled
            if (notification.getStatus() == NotificationStatus.PENDING ||
                    notification.getStatus() == NotificationStatus.SCHEDULED) {

                notification.setStatus(NotificationStatus.CANCELLED);
                notification.setUpdatedAt(LocalDateTime.now());
                return notificationRepository.save(notification);
            } else {
                throw new IllegalStateException("Cannot cancel notification with status: " + notification.getStatus());
            }
        } else {
            throw new IllegalArgumentException("Notification not found");
        }
    }

    /**
     * Gets notifications for a recipient.
     *
     * @param recipient The recipient identifier
     * @param pageable  Pagination information
     * @return Page of notifications
     */
    @Transactional(readOnly = true)
    public Page<Notification> getNotificationsForRecipient(String recipient, Pageable pageable) {
        return notificationRepository.findByRecipient(recipient, pageable);
    }

    /**
     * Gets notifications by status.
     *
     * @param status   The notification status
     * @param pageable Pagination information
     * @return Page of notifications
     */
    @Transactional(readOnly = true)
    public Page<Notification> getNotificationsByStatus(NotificationStatus status, Pageable pageable) {
        return notificationRepository.findByStatus(status, pageable);
    }

    /**
     * Gets notifications by type.
     *
     * @param type     The notification type
     * @param pageable Pagination information
     * @return Page of notifications
     */
    @Transactional(readOnly = true)
    public Page<Notification> getNotificationsByType(NotificationType type, Pageable pageable) {
        return notificationRepository.findByType(type, pageable);
    }

    /**
     * Gets notifications by channel.
     *
     * @param channel  The delivery channel
     * @param pageable Pagination information
     * @return Page of notifications
     */
    @Transactional(readOnly = true)
    public Page<Notification> getNotificationsByChannel(DeliveryChannel channel, Pageable pageable) {
        return notificationRepository.findByChannel(channel, pageable);
    }

    /**
     * Gets notifications in a group.
     *
     * @param groupId  The group identifier
     * @param pageable Pagination information
     * @return Page of notifications
     */
    @Transactional(readOnly = true)
    public Page<Notification> getNotificationsByGroup(String groupId, Pageable pageable) {
        return notificationRepository.findByGroupId(groupId, pageable);
    }

    /**
     * Searches notifications.
     *
     * @param searchTerm The search term
     * @param pageable   Pagination information
     * @return Page of matching notifications
     */
    @Transactional(readOnly = true)
    public Page<Notification> searchNotifications(String searchTerm, Pageable pageable) {
        return notificationRepository.search(searchTerm, pageable);
    }

    /**
     * Processes scheduled notifications.
     */
    @Transactional
    public void processScheduledNotifications() {
        List<Notification> scheduledNotifications = notificationRepository
                .findByStatusAndScheduledForBefore(NotificationStatus.PENDING, LocalDateTime.now());

        log.info("Processing {} scheduled notifications", scheduledNotifications.size());

        for (Notification notification : scheduledNotifications) {
            sendNotification(notification.getId());
        }
    }

    /**
     * Processes notifications that need retry.
     */
    @Transactional
    public void processRetryNotifications() {
        // Implementation needed
        throw new UnsupportedOperationException("Method not implemented");
    }

    /**
     * Sends a group of notifications.
     *
     * @param notifications The list of notifications to send
     * @return The list of sent notifications with updated status
     */
    @Transactional
    public List<Notification> sendBatch(List<Notification> notifications) {
        // Implementation needed
        throw new UnsupportedOperationException("Method not implemented");
    }

    /**
     * Get notifications by recipient
     *
     * @param recipient The recipient to filter by
     * @param pageable  Pagination information
     * @return Page of notifications for the recipient
     */
    @Transactional(readOnly = true)
    public Page<Notification> getNotificationsByRecipient(String recipient, Pageable pageable) {
        return notificationRepository.findByRecipient(recipient, pageable);
    }

    /**
     * Delete a notification
     *
     * @param id The notification ID to delete
     */
    @Transactional
    public void deleteNotification(UUID id) {
        notificationRepository.deleteById(id);
    }
}