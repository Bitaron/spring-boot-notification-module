package com.notification.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.notification.domain.notification.NotificationChannel;
import com.notification.domain.template.Template;

/**
 * Repository for managing notification templates.
 */
@Repository
public interface TemplateRepository extends JpaRepository<Template, UUID> {
    
    /**
     * Finds a template by its unique code.
     *
     * @param code The template code
     * @return The template, if found
     */
    Optional<Template> findByCode(String code);
    
    /**
     * Finds templates for a specific delivery channel.
     *
     * @param channel The delivery channel
     * @return List of templates for the channel
     */
    List<Template> findByChannel(NotificationChannel channel);
    
    /**
     * Finds templates for a specific locale.
     *
     * @param locale The locale code
     * @return List of templates for the locale
     */
    List<Template> findByLocale(String locale);
    
    /**
     * Finds active templates for a specific channel.
     *
     * @param channel The delivery channel
     * @param active Whether templates are active
     * @return List of active templates for the channel
     */
    List<Template> findByChannelAndActive(NotificationChannel channel, boolean active);
} 