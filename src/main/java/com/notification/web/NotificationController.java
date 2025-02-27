package com.notification.web;

import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.notification.domain.notification.DeliveryChannel;
import com.notification.domain.notification.Notification;
import com.notification.domain.notification.NotificationPriority;
import com.notification.domain.notification.NotificationType;
import com.notification.service.NotificationService;
import com.notification.web.dto.NotificationRequest;
import com.notification.web.dto.NotificationResponse;

import lombok.RequiredArgsConstructor;

/**
 * REST controller for notification operations.
 */

@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    
    private final NotificationService notificationService;
    
    /**
     * Sends a notification using the provided request data.
     *
     * @param request The notification request
     * @return The created notification
     */
    @PostMapping
    public ResponseEntity<NotificationResponse> sendNotification(@RequestBody NotificationRequest request) {
        Notification notification = createNotificationFromRequest(request);
        Notification sent = notificationService.sendNotificationImmediately(notification);
        return ResponseEntity.ok(createResponseFromNotification(sent));
    }
    
    /**
     * Gets notification by ID.
     *
     * @param id The notification ID
     * @return The notification
     */
    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponse> getNotification(@PathVariable UUID id) {
        return notificationService.getNotification(id)
                .map(NotificationResponse::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Gets notifications for a recipient.
     *
     * @param recipient The recipient
     * @param pageable Pagination parameters
     * @return Page of notifications
     */
    @GetMapping("/recipient/{recipient}")
    public ResponseEntity<Page<NotificationResponse>> getNotificationsByRecipient(
            @PathVariable String recipient, Pageable pageable) {
        Page<Notification> notifications = notificationService.getNotificationsByRecipient(recipient, pageable);
        Page<NotificationResponse> response = notifications.map(NotificationResponse::new);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Searches notifications.
     *
     * @param term The search term
     * @param pageable Pagination parameters
     * @return Page of matching notifications
     */
    @GetMapping("/search")
    public ResponseEntity<Page<NotificationResponse>> searchNotifications(
            @RequestParam String term, Pageable pageable) {
        Page<Notification> notifications = notificationService.searchNotifications(term, pageable);
        Page<NotificationResponse> response = notifications.map(NotificationResponse::new);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Deletes a notification.
     *
     * @param id The notification ID
     * @return Empty response with OK status
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable UUID id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok().build();
    }

    private Notification createNotificationFromRequest(NotificationRequest request) {
        Notification notification = new Notification();
        notification.setRecipient(request.getRecipient());
        notification.setSubject(request.getSubject());
        notification.setContent(request.getContent());
        notification.setChannel(request.getChannel());
        notification.setScheduledFor(request.getScheduledFor());
        notification.setPriority(request.getPriority());
        notification.setType(request.getType());
        notification.setHtmlEnabled(request.isHtmlEnabled());
        return notification;
    }
    
    private NotificationResponse createResponseFromNotification(Notification notification) {
        NotificationResponse response = new NotificationResponse();
        response.setId(notification.getId());
        response.setRecipient(notification.getRecipient());
        response.setSubject(notification.getSubject());
        response.setContent(notification.getContent());
        response.setChannel(notification.getChannel());
        response.setStatus(notification.getStatus());
        response.setCreatedAt(notification.getCreatedAt());
        response.setSentAt(notification.getSentAt());
        response.setDeliveredAt(notification.getDeliveredAt());
        response.setFailureReason(notification.getFailureReason());
        return response;
    }
} 