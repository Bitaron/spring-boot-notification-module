package com.notification.service.template;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.notification.domain.notification.NotificationChannel;
import com.notification.domain.template.Template;

/**
 * Service for managing notification templates and processing template content.
 */
public interface TemplateService {
    
    /**
     * Creates a new template.
     *
     * @param template The template to create
     * @return The created template
     */
    Template createTemplate(Template template);
    
    /**
     * Updates an existing template.
     *
     * @param template The template with updated values
     * @return The updated template
     * @throws IllegalArgumentException if the template doesn't exist
     */
    Template updateTemplate(Template template);
    
    /**
     * Retrieves a template by its ID.
     *
     * @param id The template ID
     * @return The template, if found
     */
    Optional<Template> getTemplate(Long id);
    
    /**
     * Retrieves a template by its code.
     *
     * @param code The template code
     * @return The template, if found
     */
    Optional<Template> getTemplateByCode(String code);
    
    /**
     * Retrieves templates by delivery channel.
     *
     * @param channel The delivery channel
     * @return List of templates for the specified channel
     */
    List<Template> getTemplatesByChannel(NotificationChannel channel);
    
    /**
     * Retrieves templates by locale.
     *
     * @param locale The locale code (e.g., 'en', 'fr', etc.)
     * @return List of templates for the specified locale
     */
    List<Template> getTemplatesByLocale(String locale);
    
    /**
     * Deletes a template by its ID.
     *
     * @param id The template ID to delete
     */
    void deleteTemplate(Long id);
    
    /**
     * Activates or deactivates a template.
     *
     * @param id The template ID to update
     * @param active Whether the template should be active
     * @return The updated template
     */
    Template setTemplateActive(Long id, boolean active);
    
    /**
     * Processes a template identified by code with the given parameters.
     * 
     * @param templateCode The unique code of the template
     * @param parameters The data to be merged with the template
     * @return Array of processed strings, typically [content, subject]
     * @throws TemplateException if template processing fails
     */
    String[] processTemplate(String templateCode, Map<String, Object> parameters);
    
    /**
     * Processes a template with the given parameters.
     * 
     * @param template The template object
     * @param parameters The data to be merged with the template
     * @return Array of processed strings, typically [content, subject]
     * @throws TemplateException if template processing fails
     */
    String[] processTemplate(Template template, Map<String, Object> parameters);
    
    /**
     * Check if a template exists
     * 
     * @param templateId The template identifier
     * @return True if the template exists, false otherwise
     */
    boolean templateExists(String templateId);
}