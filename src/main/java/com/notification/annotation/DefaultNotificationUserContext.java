package com.notification.annotation;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Component
@ConditionalOnMissingBean(name = "notificationUserContext")
public class DefaultNotificationUserContext implements NotificationUserContext {
    private static final DateTimeFormatter UTC_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public String getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return "system"; // Default fallback if no authenticated user found
    }

    @Override
    public LocalDateTime getCurrentTimestamp() {
        return LocalDateTime.now(ZoneOffset.UTC);
    }

    /**
     * Format timestamp as UTC string
     * @return formatted timestamp string
     */
    public String getFormattedTimestamp() {
        return getCurrentTimestamp().format(UTC_FORMATTER);
    }
}
