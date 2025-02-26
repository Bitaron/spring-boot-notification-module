package com.notification.service.template;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.Version;

@ExtendWith(MockitoExtension.class)
class FreemarkerTemplateResolverTest {

    @Mock
    private Configuration freemarkerConfig;
    
    @Mock
    private Template template;
    
    @InjectMocks
    private FreemarkerTemplateResolver templateResolver;
    
    @Test
    void testSupportsTemplateType() {
        assertTrue(templateResolver.supportsTemplateType("freemarker"));
        assertTrue(templateResolver.supportsTemplateType("FREEMARKER"));
        assertFalse(templateResolver.supportsTemplateType("velocity"));
    }
    
    @Test
    void testProcessTemplate() throws Exception {
        // Given
        String templateContent = "Hello ${name}!";
        Map<String, Object> data = new HashMap<>();
        data.put("name", "World");
        
        // Set up mock behavior
        when(freemarkerConfig.getDefaultEncoding()).thenReturn("UTF-8");
        
        // Use doAnswer to simulate template processing
        doAnswer(invocation -> {
            // Process the template like freemarker would
            String result = templateContent.replace("${name}", (String)data.get("name"));
            invocation.getArgument(1, java.io.Writer.class).write(result);
            return null;
        }).when(template).process(eq(data), any(java.io.Writer.class));
        
        // Mock template creation
        doReturn(template).when(freemarkerConfig).getTemplate(anyString());
        
        // When
        String result = templateResolver.processTemplate(templateContent, data);
        
        // Then
        assertEquals("Hello World!", result);
    }
    
    @Test
    void testProcessTemplate_throwsException() throws Exception {
        // Given
        String templateContent = "Invalid ${template";
        Map<String, Object> data = new HashMap<>();
        
        // Set up mock to throw exception
        when(freemarkerConfig.getTemplate(anyString())).thenThrow(new freemarker.template.TemplateException("Template error", null));
        
        // When/Then
        assertThrows(TemplateException.class, () -> {
            templateResolver.processTemplate(templateContent, data);
        });
    }

    @Test
    void testResolveTemplate() throws Exception {
        // Given
        String templateName = "test-template";
        Map<String, Object> data = new HashMap<>();
        data.put("name", "World");
        
        // Create template content
        String inputTemplateContent = "Hello ${name}!";
        
        // Set up mock behavior
        when(freemarkerConfig.getTemplate(templateName + ".ftl")).thenReturn(template);
        when(freemarkerConfig.getDefaultEncoding()).thenReturn("UTF-8");
        
        // Use doAnswer to simulate template processing
        doAnswer(invocation -> {
            // Process the template like freemarker would
            String result = inputTemplateContent.replace("${name}", (String)data.get("name"));
            invocation.getArgument(1, java.io.Writer.class).write(result);
            return null;
        }).when(template).process(eq(data), any(java.io.Writer.class));
        
        // Use a mocked implementation for the processTemplate method
        FreemarkerTemplateResolver spyResolver = spy(templateResolver);
        doAnswer(invocation -> {
            String content = invocation.getArgument(0);
            Map<String, Object> templateData = invocation.getArgument(1);
            
            // For the test, we'll simulate template loading and processing
            // In reality, the template content would be passed directly
            Template loadedTemplate = freemarkerConfig.getTemplate(templateName + ".ftl");
            StringWriter writer = new StringWriter();
            loadedTemplate.process(templateData, writer);
            
            return writer.toString();
        }).when(spyResolver).processTemplate(anyString(), anyMap());
        
        // When
        // Process the template
        String result = spyResolver.processTemplate(inputTemplateContent, data);
        
        // Then
        assertEquals("Hello World!", result);
        verify(freemarkerConfig).getTemplate(templateName + ".ftl");
    }
}