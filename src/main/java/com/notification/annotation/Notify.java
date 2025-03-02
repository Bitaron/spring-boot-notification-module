package com.notification.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.notification.domain.notification.NotificationChannel;
import com.notification.domain.notification.NotificationPriority;
import com.notification.domain.notification.NotificationType;

/**
 * Annotation for declarative notification sending.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Notify {
    
    /**
     * The template code to use for the notification.
     * 
     * @return The template code
     */
    String templateCode();
    
    /**
     * The delivery channel to use.
     * 
     * @return The delivery channel
     */
    NotificationChannel channel();
    
    /**
     * The notification type.
     * 
     * @return The notification type
     */
    NotificationType type() default NotificationType.INFO;
    
    /**
     * The notification priority.
     * 
     * @return The notification priority
     */
    NotificationPriority priority() default NotificationPriority.NORMAL;
    
    /**
     * SpEL expression to determine the recipient.
     * 
     * @return The recipient expression
     */
    String recipient();
    
    /**
     * SpEL expression to extract template parameters.
     * 
     * @return The template parameters expression
     */
    String templateParams() default "#root";
    
    /**
     * Whether to send the notification asynchronously.
     * 
     * @return True if async
     */
    boolean async() default true;
    
    /**
     * Whether to skip the notification if an exception is thrown by the method.
     * 
     * @return True if should skip on error
     */
    boolean skipOnError() default true;
    
    /**
     * Group identifier for related notifications.
     * 
     * @return The group ID expression
     */
    String groupId() default "";
} 