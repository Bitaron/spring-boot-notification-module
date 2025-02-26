package com.notification.service.builder;

import java.time.LocalDateTime;
import java.util.Map;

import com.notification.domain.notification.DeliveryChannel;
import com.notification.domain.notification.Notification;
import com.notification.domain.notification.NotificationPriority;
import com.notification.domain.notification.NotificationType;

/**
 * Interface for building notification objects with various properties.
 */
public interface NotificationBuilder {
    
    /**
     * Sets the recipient of the notification.
     *
     * @param recipient The recipient identifier
     * @return The builder for chaining
     */
    NotificationBuilder recipient(String recipient);
    
    /**
     * Sets the subject of the notification.
     *
     * @param subject The notification subject or title
     * @return The builder for chaining
     */
    NotificationBuilder subject(String subject);
    
    /**
     * Sets the content of the notification.
     *
     * @param content The notification content
     * @return The builder for chaining
     */
    NotificationBuilder content(String content);
    
    /**
     * Sets the delivery channel for the notification.
     *
     * @param channel The delivery channel
     * @return The builder for chaining
     */
    NotificationBuilder channel(DeliveryChannel channel);
    
    /**
     * Sets the notification type.
     *
     * @param type The notification type
     * @return The builder for chaining
     */
    NotificationBuilder type(NotificationType type);
    
    /**
     * Sets the notification priority.
     *
     * @param priority The notification priority
     * @return The builder for chaining
     */
    NotificationBuilder priority(NotificationPriority priority);
    
    /**
     * Sets the template ID to be used for content generation.
     *
     * @param templateId The template ID
     * @return The builder for chaining
     */
    NotificationBuilder templateId(String templateId);
    
    /**
     * Sets template parameters for content generation.
     *
     * @param templateParams The template parameters
     * @return The builder for chaining
     */
    NotificationBuilder templateParams(Map<String, Object> templateParams);
    
    /**
     * Sets the notification group ID.
     *
     * @param groupId The group ID
     * @return The builder for chaining
     */
    NotificationBuilder groupId(String groupId);
    
    /**
     * Sets the scheduled delivery time.
     *
     * @param scheduledFor When the notification should be delivered
     * @return The builder for chaining
     */
    NotificationBuilder scheduledFor(LocalDateTime scheduledFor);
    
    /**
     * Sets the sender identifier.
     *
     * @param sender The sender identifier
     * @return The builder for chaining
     */
    NotificationBuilder sender(String sender);
    
    /**
     * Sets whether HTML is enabled for this notification.
     *
     * @param htmlEnabled True if HTML is enabled
     * @return The builder for chaining
     */
    NotificationBuilder htmlEnabled(boolean htmlEnabled);
    
    /**
     * Sets the attachment URL.
     *
     * @param attachmentUrl URL pointing to the attachment
     * @return The builder for chaining
     */
    NotificationBuilder attachmentUrl(String attachmentUrl);
    
    /**
     * Sets additional metadata for the notification.
     *
     * @param metadata The metadata as a string (typically JSON)
     * @return The builder for chaining
     */
    NotificationBuilder metadata(String metadata);
    
    /**
     * Sets maximum retry attempts.
     *
     * @param maxAttempts The maximum number of retry attempts
     * @return The builder for chaining
     */
    NotificationBuilder maxAttempts(int maxAttempts);
    
    /**
     * Builds and returns the notification object.
     *
     * @return The constructed notification
     * @throws IllegalStateException if the notification cannot be built due to missing required fields
     */
    Notification build();
} 