package com.notification.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Main configuration class for the notification module.
 */
@Configuration
@ComponentScan(basePackages = "com.notification")
@Import({
    CacheConfig.class,
    FreemarkerConfig.class,
    WebSocketConfig.class
})
public class NotificationModuleConfig {
    
    /**
     * Creates a default task executor if none is provided.
     *
     * @return The task executor for asynchronous operations
     */
    @Bean
    @ConditionalOnMissingBean(name = "notificationTaskExecutor")
    public TaskExecutor notificationTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(25);
        executor.setThreadNamePrefix("notification-");
        executor.initialize();
        return executor;
    }
} 