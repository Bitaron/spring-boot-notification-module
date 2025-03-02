package com.notification.metrics;

import java.util.concurrent.atomic.AtomicLong;

import com.notification.domain.notification.NotificationChannel;
import com.notification.domain.notification.NotificationStatus;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Metrics collection for notifications.
 */

@RequiredArgsConstructor
public class NotificationMetrics {
    
    private final MeterRegistry meterRegistry;
    
    // Total counts
    @Getter private final AtomicLong totalNotifications = new AtomicLong(0);
    @Getter private final AtomicLong successfulNotifications = new AtomicLong(0);
    @Getter private final AtomicLong failedNotifications = new AtomicLong(0);
    
    // Counters by channel
    private final Counter emailCounter;
    private final Counter smsCounter;
    private final Counter pushCounter;
    private final Counter webCounter;
    
    // Timers
    private final Timer emailTimer;
    private final Timer smsTimer;
    private final Timer pushTimer;
    private final Timer webTimer;
    
    /**
     * Records a notification attempt.
     *
     * @param channel The delivery channel
     */
    public void recordNotificationAttempt(NotificationChannel channel) {
        totalNotifications.incrementAndGet();
        
        switch (channel) {
            case EMAIL:
                emailCounter.increment();
                break;
            case SMS:
                smsCounter.increment();
                break;
            case PUSH:
                pushCounter.increment();
                break;
            case WEB:
                webCounter.increment();
                break;
            default:
                // No specific counter for other channels
                break;
        }
    }
    
    /**
     * Records a notification result.
     *
     * @param status The notification status
     */
    public void recordNotificationResult(NotificationStatus status) {
        if (status == NotificationStatus.SENT || status == NotificationStatus.DELIVERED) {
            successfulNotifications.incrementAndGet();
        } else if (status == NotificationStatus.FAILED) {
            failedNotifications.incrementAndGet();
        }
    }
    
    /**
     * Gets a timer for measuring notification delivery time.
     *
     * @param channel The delivery channel
     * @return The timer
     */
    public Timer getTimerForChannel(NotificationChannel channel) {
        switch (channel) {
            case EMAIL:
                return emailTimer;
            case SMS:
                return smsTimer;
            case PUSH:
                return pushTimer;
            case WEB:
                return webTimer;
            default:
                // Use email timer as default
                return emailTimer;
        }
    }
} 