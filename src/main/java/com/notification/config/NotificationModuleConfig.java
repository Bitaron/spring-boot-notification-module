package com.notification.config;

import com.notification.annotation.DefaultNotificationUserContext;
import com.notification.annotation.NotificationUserContext;
import com.notification.repository.NotificationRepository;
import com.notification.service.NotificationService;
import com.notification.service.delivery.DeliveryService;
import com.notification.service.delivery.DeliveryServiceFactoryImpl;
import com.notification.service.delivery.web.DeliveryServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;

/**
 * Main configuration class for the notification module.
 */
@AutoConfiguration
@EnableAsync
@ComponentScan(basePackages = "com.notification")
@EntityScan(basePackages = "com.notification.domain")
@EnableJpaRepositories(basePackages = "com.notification.repository")
public class NotificationModuleConfig {
    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    List<DeliveryService> deliveryServices;

    @Bean
    public NotificationService notificationService() {
        return new NotificationService(notificationRepository, deliveryServices);
    }

    @Bean
    @ConditionalOnMissingBean(NotificationUserContext.class)
    public NotificationUserContext defaultNotificationUserContext() {
        return new DefaultNotificationUserContext();
    }

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