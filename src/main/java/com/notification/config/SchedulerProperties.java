package com.notification.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "notification.scheduler")
@Data
public class SchedulerProperties {

    /**
     * Enable scheduler . By default false
     */

    private boolean enabled = false;

    private int scheduledNotificationsInterval = 6000;
}
