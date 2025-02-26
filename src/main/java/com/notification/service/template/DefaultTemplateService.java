package com.notification.service.template;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.notification.domain.notification.DeliveryChannel;
import com.notification.domain.template.Template;
import com.notification.repository.TemplateRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Default implementation of TemplateService.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultTemplateService implements TemplateService {
    
    private final TemplateRepository templateRepository;
    private final List<TemplateResolver> templateResolvers;
    
    @Override
    @Transactional
    public Template createTemplate(Template template) {
        if (template.getId() == null) {
            template.setId(UUID.randomUUID());
        }
        template.setCreatedAt(LocalDateTime.now());
        template.setUpdatedAt(LocalDateTime.now());
        return templateRepository.save(template);
    }
    
    @Override
    @Transactional
    @CacheEvict(value = "templates", key = "#template.code")
    public Template updateTemplate(Template template) {
        if (!templateRepository.existsById(template.getId())) {
            throw new IllegalArgumentException("Template not found with ID: " + template.getId());
        }
        template.setUpdatedAt(LocalDateTime.now());
        return templateRepository.save(template);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Template> getTemplate(UUID id) {
        return templateRepository.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "templates", key = "#code")
    public Optional<Template> getTemplateByCode(String code) {
        return templateRepository.findByCode(code);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Template> getTemplatesByChannel(DeliveryChannel channel) {
        return templateRepository.findByChannel(channel);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Template> getTemplatesByLocale(String locale) {
        return templateRepository.findByLocale(locale);
    }
    
    @Override
    @Transactional
    @CacheEvict(value = "templates", allEntries = true)
    public void deleteTemplate(UUID id) {
        templateRepository.deleteById(id);
    }
    
    @Override
    @Transactional
    @CacheEvict(value = "templates", key = "#result.code")
    public Template setTemplateActive(UUID id, boolean active) {
        Template template = templateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Template not found with ID: " + id));
        template.setActive(active);
        template.setUpdatedAt(LocalDateTime.now());
        return templateRepository.save(template);
    }
    
    @Override
    public String[] processTemplate(String templateCode, Map<String, Object> parameters) {
        Template template = templateRepository.findByCode(templateCode)
                .orElseThrow(() -> new TemplateException("Template not found with code: " + templateCode));
        
        if (!template.isActive()) {
            throw new TemplateException("Template is not active: " + templateCode);
        }
        
        return processTemplate(template, parameters);
    }
    
    @Override
    public String[] processTemplate(Template template, Map<String, Object> parameters) {
        // Default to using FreeMarker
        String templateType = "freemarker";
        
        TemplateResolver resolver = findResolver(templateType);
        
        String content = resolver.processTemplate(template.getContent(), parameters);
        String subject = null;
        
        if (template.getSubject() != null && !template.getSubject().isEmpty()) {
            subject = resolver.processTemplate(template.getSubject(), parameters);
        }
        
        return new String[] { content, subject };
    }
    
    private TemplateResolver findResolver(String templateType) {
        return templateResolvers.stream()
                .filter(resolver -> resolver.supportsTemplateType(templateType))
                .findFirst()
                .orElseThrow(() -> new TemplateException("No resolver found for template type: " + templateType));
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean templateExists(String templateId) {
        try {
            return getTemplateByCode(templateId).isPresent();
        } catch (Exception e) {
            log.error("Error checking if template exists: {}", templateId, e);
            return false;
        }
    }
}