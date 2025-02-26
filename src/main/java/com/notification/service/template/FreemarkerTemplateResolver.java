package com.notification.service.template;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

import org.springframework.stereotype.Component;

import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * TemplateResolver implementation using the FreeMarker template engine.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class FreemarkerTemplateResolver implements TemplateResolver {
    
    private final Configuration freemarkerConfig;
    
    @Override
    public boolean supportsTemplateType(String templateType) {
        return "freemarker".equalsIgnoreCase(templateType);
    }
    
    @Override
    public String processTemplate(String templateContent, Map<String, Object> data) throws TemplateException {
        try {
            // Create a template from the provided content
            Template template = new Template("inline_template", new StringReader(templateContent), freemarkerConfig);
            
            // Process the template with the provided data
            StringWriter writer = new StringWriter();
            template.process(data, writer);
            
            return writer.toString();
        } catch (Exception e) {
            log.error("Failed to process FreeMarker template: {}", e.getMessage());
            throw new TemplateException("Failed to process template: " + e.getMessage(), e);
        }
    }
} 