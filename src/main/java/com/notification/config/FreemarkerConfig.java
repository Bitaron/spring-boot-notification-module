package com.notification.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;

/**
 * Configuration for FreeMarker template engine.
 */
@AutoConfiguration
public class FreemarkerConfig {
    
    /**
     * Configures the FreeMarker template engine.
     *
     * @return FreeMarker configuration
     */
   /* @Bean
    @ConditionalOnMissingBean
    public freemarker.template.Configuration freemarkerConfig() {
        FreeMarkerConfigurationFactoryBean factory = new FreeMarkerConfigurationFactoryBean();
        factory.setTemplateLoaderPath("classpath:/templates/");
        factory.setDefaultEncoding("UTF-8");
        try {
            factory.afterPropertiesSet();
            freemarker.template.Configuration configuration = factory.getObject();
            configuration.setDefaultEncoding("UTF-8");
            configuration.setNumberFormat("0.######");
            configuration.setBooleanFormat("true,false");
            configuration.setDateTimeFormat("yyyy-MM-dd HH:mm:ss");
            return configuration;
        } catch (Exception e) {
            throw new RuntimeException("Failed to configure FreeMarker", e);
        }
    }*/
} 