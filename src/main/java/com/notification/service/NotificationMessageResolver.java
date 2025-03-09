package com.notification.service;


import com.notification.annotation.NotificationUserContext;
import com.notification.domain.notification.NotificationChannel;
import com.notification.domain.notification.NotificationMessage;
import com.notification.domain.template.Template;
import com.notification.exception.NotificationMessageException;
import com.notification.repository.TemplateRepository;
import com.notification.service.template.TemplateResolver;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

@Component
public class NotificationMessageResolver {
    private static final Logger logger = LoggerFactory.getLogger(NotificationMessageResolver.class);
    private static final DateTimeFormatter UTC_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final TemplateRepository templateRepository;
    private final TemplateResolver templateResolver;
    private final NotificationUserContext userContext;

    public NotificationMessageResolver(TemplateRepository templateRepository,
                                       TemplateResolver templateResolver,
                                       NotificationUserContext userContext) {
        this.templateRepository = templateRepository;
        this.templateResolver = templateResolver;
        this.userContext = userContext;
    }

    /**
     * Resolve notification message for specified channel
     *
     * @param notification notification object containing template and data
     * @param channel      channel to get message for
     * @return resolved message
     * @throws NotificationMessageException if message cannot be resolved
     */
    public NotificationContent resolveMessage(NotificationMessage notification, NotificationChannel channel) {
     //   logProcessingStart(notification, channel);

        try {
            NotificationContent message = resolveMessageInternal(notification, channel);
        //    logProcessingSuccess(notification, channel);
            return message;
        } catch (Exception e) {
         //   logProcessingError(notification, channel, e);
            throw new NotificationMessageException(
                    "Failed to resolve notification message", e);
        }
    }

    private NotificationContent resolveMessageInternal(NotificationMessage notification, NotificationChannel channel) {
        // Step 1: Check if template is provided
        if (notification.getTemplateName() != null && !notification.getTemplateName().isEmpty()) {
            // Step 2: Check template repository for template_channel
            Optional<Template> channelTemplate =
                    templateRepository.findByNameAndChannel(
                            notification.getTemplateName(),
                            channel
                    );

            if (channelTemplate.isPresent()) {
              //  logTemplateFound("channel-specific", notification.getTemplateName(), channel);
                String content = processTemplate(
                        channelTemplate.get().getContent(),
                        notification.getTemplateData()
                );
                return new NotificationContent(content, channelTemplate.get().isHtmlEnabled());
            }

            // Step 3: Check template repository for template only
            Optional<Template> defaultTemplate =
                    templateRepository.findByName(notification.getTemplateName());

            if (defaultTemplate.isPresent()) {
           //     logTemplateFound("default", notification.getTemplateName(), channel);
                String content = processTemplate(
                        defaultTemplate.get().getContent(),
                        notification.getTemplateData()
                );
                return new NotificationContent(content, defaultTemplate.get().isHtmlEnabled());
            }

            throw new NotificationMessageException(
                    String.format("Template not found: %s for channel: %s",
                            notification.getTemplateName(),
                            channel)
            );
        }

        // Step 5: Check raw message
        if (notification.getRawMessage() != null && !notification.getRawMessage().isEmpty()) {
          //  logRawMessageFound(channel);
            return new NotificationContent(notification.getRawMessage(), notification.getIsHtml());
        }

        // Step 7: Nothing found, throw exception
        throw new NotificationMessageException(
                "No template or raw message found for notification"
        );
    }

    private String processTemplate(String templateContent, Map<String, Object> data) {
        // Add common data
       /* data.put("timestamp", userContext.getCurrentTimestamp());
        data.put("currentUser", userContext.getCurrentUser());*/

        return templateResolver.processTemplate(templateContent, data);
    }

    private void logProcessingStart(NotificationMessage notification, NotificationChannel channel) {
        logger.info(
                "Current Date and Time (UTC - YYYY-MM-DD HH:MM:SS formatted): {}\n" +
                        "Current User's Login: {}\n" +
                        "Starting message resolution for notification ID: {} and channel: {}",
                userContext.getCurrentTimestamp().format(UTC_FORMATTER),
                userContext.getCurrentUser(),
                notification.getId(),
                channel
        );
    }

    private void logProcessingSuccess(NotificationMessage notification, NotificationChannel channel) {
        logger.info(
                "Current Date and Time (UTC - YYYY-MM-DD HH:MM:SS formatted): {}\n" +
                        "Current User's Login: {}\n" +
                        "Successfully resolved message for notification ID: {} and channel: {}",
                userContext.getCurrentTimestamp().format(UTC_FORMATTER),
                userContext.getCurrentUser(),
                notification.getId(),
                channel
        );
    }

    private void logProcessingError(NotificationMessage notification,
                                    NotificationChannel channel,
                                    Exception e) {
        logger.error(
                "Current Date and Time (UTC - YYYY-MM-DD HH:MM:SS formatted): {}\n" +
                        "Current User's Login: {}\n" +
                        "Failed to resolve message for notification ID: {} and channel: {}\n" +
                        "Error: {}",
                userContext.getCurrentTimestamp().format(UTC_FORMATTER),
                userContext.getCurrentUser(),
                notification.getId(),
                channel,
                e.getMessage(),
                e
        );
    }

    private void logTemplateFound(String type, String templateName, NotificationChannel channel) {
        logger.info(
                "Current Date and Time (UTC - YYYY-MM-DD HH:MM:SS formatted): {}\n" +
                        "Current User's Login: {}\n" +
                        "Found {} template: {} for channel: {}",
                userContext.getCurrentTimestamp().format(UTC_FORMATTER),
                userContext.getCurrentUser(),
                type,
                templateName,
                channel
        );
    }

    private void logRawMessageFound(NotificationChannel channel) {
        logger.info(
                "Current Date and Time (UTC - YYYY-MM-DD HH:MM:SS formatted): {}\n" +
                        "Current User's Login: {}\n" +
                        "Using raw message for channel: {}",
                userContext.getCurrentTimestamp().format(UTC_FORMATTER),
                userContext.getCurrentUser(),
                channel
        );
    }

    @Data
    @AllArgsConstructor
    public class NotificationContent {
        private String content;
        private Boolean isHtml;

        public NotificationContent(String content) {
            this.content = content;
        }
    }
}
