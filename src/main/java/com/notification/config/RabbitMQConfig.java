package com.notification.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for RabbitMQ messaging.
 */
@Configuration
@ConditionalOnProperty(name = "notification.use-queue", havingValue = "true")
public class RabbitMQConfig {
    @Autowired
    QueueProperties queueProperties;

    /**
     * Creates the notification queue.
     *
     * @return The queue
     */
    @Bean
    public Queue notificationQueue() {
        return new Queue(queueProperties.getQueueName(), true);
    }

    /**
     * Creates the notification exchange.
     *
     * @return The exchange
     */
    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange(queueProperties.getExchange());
    }

    /**
     * Binds the queue to the exchange.
     *
     * @param queue    The queue
     * @param exchange The exchange
     * @return The binding
     */
    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(queueProperties.getRoutingKey());
    }

    /**
     * Configures the RabbitTemplate with JSON message conversion.
     *
     * @param connectionFactory The connection factory
     * @return The configured RabbitTemplate
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }
} 