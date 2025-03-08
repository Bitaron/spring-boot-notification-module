package com.notification.annotation;

import com.notification.config.NotificationProperties;
import com.notification.exception.NotificationAspectException;
import com.notification.service.NotificationService;
import com.notification.service.builder.NotificationRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Aspect
@Component
public class NotificationAspect {
    private static final Logger logger = LoggerFactory.getLogger(NotificationAspect.class);
    private static final DateTimeFormatter UTC_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final NotificationService notificationService;
    private final NotificationProperties properties;
    private final ExpressionParser expressionParser;
    private final ApplicationContext applicationContext;
    private final Map<String, NotificationDataProvider> providers;
    private final NotificationUserContext userContext;

    @Autowired
    public NotificationAspect(NotificationService notificationService,
                              NotificationProperties properties,
                              ApplicationContext applicationContext,
                              NotificationUserContext userContext) {
        this.notificationService = notificationService;
        this.properties = properties;
        this.expressionParser = new SpelExpressionParser();
        this.applicationContext = applicationContext;
        this.providers = new HashMap<>();
        this.userContext = userContext;
    }

    @AfterReturning(
            pointcut = "@annotation(notify)",
            returning = "result"
    )
    public void sendSuccessNotification(JoinPoint joinPoint, Notify notify, Object result) {
        try {
            logCurrentContext("Processing success notification");
            sendNotification(joinPoint, notify, result, null, notify.successTemplate());
        } catch (Exception e) {
            logError("Failed to send success notification", e);
            throw new NotificationAspectException("Failed to send success notification", e);
        }
    }

    @AfterThrowing(
            pointcut = "@annotation(notify)",
            throwing = "ex"
    )
    public void sendErrorNotification(JoinPoint joinPoint, Notify notify, Exception ex) {
        try {
            logCurrentContext("Processing error notification");
            sendNotification(joinPoint, notify, null, ex, notify.errorTemplate());
        } catch (Exception e) {
            logError("Failed to send error notification", e);
            throw new NotificationAspectException("Failed to send error notification", e);
        }
    }

    private void sendNotification(JoinPoint joinPoint,
                                  Notify notify,
                                  Object result,
                                  Exception error,
                                  String templateName) {
        // Validate template
        if (templateName.isEmpty()) {
            throw new NotificationAspectException("Template name is required for @Notify annotation");
        }

        // Get method details
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        List<String> recipients;
        Map<String, Object> templateData;

        if (!notify.name().isEmpty()) {
            // Use provider
            NotificationDataProvider provider = getProvider(notify.name());
            recipients = provider.getRecipients(result, joinPoint.getArgs());
            templateData = provider.getTemplateData(result, joinPoint.getArgs());

            // Add error information if available
            if (error != null) {
                templateData.put("error", error);
                templateData.put("errorMessage", error.getMessage());
                templateData.put("errorType", error.getClass().getSimpleName());
            }

            addCommonTemplateData(templateData);
        } else {
            // Use SpEL expressions
            EvaluationContext context = createEvaluationContext(joinPoint, result, error);

            // Check condition if specified
            if (!notify.condition().isEmpty()) {
                Expression condition = expressionParser.parseExpression(notify.condition());
                Boolean shouldNotify = condition.getValue(context, Boolean.class);
                if (shouldNotify == null || !shouldNotify) {
                    logCurrentContext("Notification condition not met for method: " + method.getName());
                    return;
                }
            }

            recipients = evaluateRecipients(notify.recipients(), context);
            templateData = evaluateTemplateData(notify.templateData(), context);
        }

        if (recipients.isEmpty()) {
            logCurrentContext("No recipients found for notification from method: " + method.getName());
            return;
        }

        // Build and send notification
        NotificationRequest request = buildNotificationRequest(notify, recipients, templateData, templateName);
        String notificationId = notificationService.sendNotification(request);

        logNotificationSent(templateName, notificationId, recipients.size());
    }

    private NotificationDataProvider getProvider(String name) {
        return providers.computeIfAbsent(name, key -> {
            Map<String, NotificationDataProvider> beans =
                    applicationContext.getBeansOfType(NotificationDataProvider.class);

            return beans.values().stream()
                    .filter(provider -> provider.getName().equals(key))
                    .findFirst()
                    .orElseThrow(() -> new NotificationAspectException(
                            "No NotificationDataProvider found with name: " + key));
        });
    }

    private void addCommonTemplateData(Map<String, Object> templateData) {
        templateData.put("timestamp", userContext.getCurrentTimestamp());
        templateData.put("currentUser", userContext.getCurrentUser());
    }

    private EvaluationContext createEvaluationContext(JoinPoint joinPoint,
                                                      Object result,
                                                      Exception error) {
        StandardEvaluationContext context = new StandardEvaluationContext();

        // Add method parameters to context
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        for (int i = 0; i < paramNames.length; i++) {
            context.setVariable(paramNames[i], args[i]);
        }

        // Add method result or error to context
        if (result != null) {
            context.setVariable("result", result);
        }
        if (error != null) {
            context.setVariable("error", error);
            context.setVariable("errorMessage", error.getMessage());
            context.setVariable("errorType", error.getClass().getSimpleName());
        }

        // Add common variables
        context.setVariable("currentUser", userContext.getCurrentUser());
        context.setVariable("timestamp", userContext.getCurrentTimestamp());
        context.setVariable("method", signature.getMethod().getName());

        return context;
    }

    private List<String> evaluateRecipients(String recipientsExpression,
                                            EvaluationContext context) {
        if (recipientsExpression.isEmpty()) {
            return Collections.emptyList();
        }

        try {
            Expression expression = expressionParser.parseExpression(recipientsExpression);
            Object value = expression.getValue(context);

            if (value instanceof String) {
                return Collections.singletonList((String) value);
            } else if (value instanceof String[]) {
                return Arrays.asList((String[]) value);
            } else if (value instanceof Collection) {
                return new ArrayList<>((Collection<String>) value);
            }

            logCurrentContext("Invalid recipients expression result type: " +
                    (value != null ? value.getClass() : "null"));
            return Collections.emptyList();

        } catch (Exception e) {
            logError("Failed to evaluate recipients expression", e);
            throw new NotificationAspectException("Failed to evaluate recipients", e);
        }
    }

    private Map<String, Object> evaluateTemplateData(String templateDataExpression,
                                                     EvaluationContext context) {
        Map<String, Object> templateData = new HashMap<>();
        addCommonTemplateData(templateData);

        if (!templateDataExpression.isEmpty()) {
            try {
                String[] pairs = templateDataExpression.split(",");
                for (String pair : pairs) {
                    String[] keyValue = pair.trim().split("=");
                    if (keyValue.length == 2) {
                        String key = keyValue[0].trim();
                        Expression valueExpr = expressionParser.parseExpression(keyValue[1].trim());
                        Object value = valueExpr.getValue(context);
                        templateData.put(key, value);
                    }
                }
            } catch (Exception e) {
                logError("Failed to evaluate template data", e);
                throw new NotificationAspectException("Failed to evaluate template data", e);
            }
        }

        return templateData;
    }

    private NotificationRequest buildNotificationRequest(Notify notify,
                                                         List<String> recipients,
                                                         Map<String, Object> templateData,
                                                         String templateName) {
        return NotificationRequest.builder()
                .setType(notify.type())
                .addChannels(notify.channels())
                .forGroupWithTemplate(recipients, templateName, templateData, notify.priority())
                .build();
    }

    private void logCurrentContext(String message) {
        logger.info("Current Date and Time (UTC - YYYY-MM-DD HH:MM:SS formatted): {}\n" +
                        "Current User's Login: {}\n" +
                        "Message: {}",
                userContext.getCurrentTimestamp().format(UTC_FORMATTER),
                userContext.getCurrentUser(),
                message);
    }

    private void logError(String message, Exception e) {
        logger.error("Current Date and Time (UTC - YYYY-MM-DD HH:MM:SS formatted): {}\n" +
                        "Current User's Login: {}\n" +
                        "Error: {} - {}",
                userContext.getCurrentTimestamp().format(UTC_FORMATTER),
                userContext.getCurrentUser(),
                message,
                e.getMessage(),
                e);
    }

    private void logNotificationSent(String templateName, String notificationId, int recipientCount) {
        logger.info("Current Date and Time (UTC - YYYY-MM-DD HH:MM:SS formatted): {}\n" +
                        "Current User's Login: {}\n" +
                        "Notification sent successfully:\n" +
                        "Template: {}\n" +
                        "Notification ID: {}\n" +
                        "Recipients: {}",
                userContext.getCurrentTimestamp().format(UTC_FORMATTER),
                userContext.getCurrentUser(),
                templateName,
                notificationId,
                recipientCount);
    }
}