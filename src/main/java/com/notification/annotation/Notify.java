package com.notification.annotation;

import com.notification.domain.notification.NotificationChannel;
import com.notification.domain.notification.NotificationType;
import com.notification.domain.notification.NotificationPriority;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Notify {
    /**
     * Name of the NotificationDataProvider to use.
     * If specified, recipients and templateData expressions will be ignored.
     */
    String name() default "";

    /**
     * Type of notification
     */
    NotificationType type() default NotificationType.INFO;

    /**
     * Notification channels to use
     */
    NotificationChannel[] channels() default {NotificationChannel.EMAIL};

    /**
     * Priority of the notification
     */
    NotificationPriority priority() default NotificationPriority.NORMAL;

    /**
     * Template name to use for successful execution (required)
     */
    String successTemplate();

    /**
     * Template name to use for error cases (required)
     */
    String errorTemplate();

    /**
     * Recipients expression (SpEL). Ignored if name is specified.
     */
    String recipients() default "";

    /**
     * Template data expressions (SpEL). Ignored if name is specified.
     */
    String templateData() default "";

    /**
     * Condition expression (SpEL) that determines if notification should be sent
     */
    String condition() default "";
}