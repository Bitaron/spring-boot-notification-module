package com.notification.config;

import com.notification.service.template.TemplateResolver;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;

@AutoConfiguration
@ConditionalOnMissingBean(TemplateResolver.class)
public class FreemarkerConfig {

    @Bean
    public Configuration freemarkerConfiguration(ResourceLoader resourceLoader) {
        Configuration configuration = new Configuration(Configuration.getVersion());

        configuration.setClassLoaderForTemplateLoading(
                resourceLoader.getClassLoader(),
                "templates/notification"
        );

        configuration.setDefaultEncoding("UTF-8");
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        configuration.setLogTemplateExceptions(false);
        configuration.setWrapUncheckedExceptions(true);

        return configuration;
    }
}