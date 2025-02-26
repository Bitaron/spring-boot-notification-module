package com.notification.service.template;

import java.util.Map;

/**
 * Interface for template engines that process notification templates.
 */
public interface TemplateResolver {
    
    /**
     * Checks if this resolver supports a specific template type.
     *
     * @param templateType The template type identifier
     * @return true if supported
     */
    boolean supportsTemplateType(String templateType);
    
    /**
     * Processes a template with the provided data.
     *
     * @param templateContent The template content to process
     * @param data The data to merge with the template
     * @return The processed template output
     * @throws TemplateException if processing fails
     */
    String processTemplate(String templateContent, Map<String, Object> data) throws TemplateException;
} 