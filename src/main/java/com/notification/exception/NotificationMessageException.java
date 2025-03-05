package com.notification.exception;

public class NotificationMessageException extends RuntimeException {
    public NotificationMessageException(String failedToResolveNotificationMessage, Exception e) {
    }

    public NotificationMessageException(String format) {

    }
}
