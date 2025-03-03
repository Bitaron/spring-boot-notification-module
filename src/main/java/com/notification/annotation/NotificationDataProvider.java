package com.notification.annotation;

import java.util.List;
import java.util.Map;

/**
 * Interface for providing notification data.
 * Implementations should be registered as Spring beans.
 */
public interface NotificationDataProvider {
    /**
     * Get the name of this provider
     */
    String getName();

    /**
     * Get list of recipients for the notification
     * @param result Method execution result
     * @param args Method arguments
     * @return List of recipient addresses
     */
    List<String> getRecipients(Object result, Object[] args);

    /**
     * Get template data for the notification
     * @param result Method execution result
     * @param args Method arguments
     * @return Map of template variables
     */
    Map<String, Object> getTemplateData(Object result, Object[] args);
}