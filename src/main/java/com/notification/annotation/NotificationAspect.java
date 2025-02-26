package com.notification.annotation;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.task.TaskExecutor;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import com.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Aspect to handle the @Notify annotation for declarative notification sending.
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationAspect {
    
    private final NotificationService notificationService;
    private final TaskExecutor taskExecutor;
    private final ExpressionParser expressionParser = new SpelExpressionParser();
    
    /**
     * Processes methods annotated with @Notify and sends notifications based on the annotation parameters.
     *
     * @param joinPoint The join point
     * @param notifyAnnotation The @Notify annotation
     * @return The result of the method execution
     * @throws Throwable If method execution fails
     */
    @Around("@annotation(notifyAnnotation)")
    public Object processNotification(ProceedingJoinPoint joinPoint, Notify notifyAnnotation) throws Throwable {
        // Execute the method first
        Object result = joinPoint.proceed();
        
        try {
            StandardEvaluationContext context = createEvaluationContext(joinPoint);
            context.setVariable("result", result);
            
            // Evaluate recipient
            String recipient = evaluateExpression(notifyAnnotation.recipient(), context, String.class);
            
            if (recipient == null || recipient.isEmpty()) {
                log.warn("Recipient evaluation returned null or empty, skipping notification");
                return result;
            }
            
            // Evaluate template parameters
            Map<String, Object> templateParams = evaluateTemplateParams(notifyAnnotation.templateParams(), context, joinPoint);
            
            // Evaluate group ID if specified
            String groupId = null;
            if (!notifyAnnotation.groupId().isEmpty()) {
                groupId = evaluateExpression(notifyAnnotation.groupId(), context, String.class);
            }
            
            // Prepare notification builder
            final String finalGroupId = groupId;
            Runnable sendTask = () -> {
                try {
                    notificationService.createNotificationBuilder()
                            .recipient(recipient)
                            .templateId(notifyAnnotation.templateCode())
                            .templateParams(templateParams)
                            .channel(notifyAnnotation.channel())
                            .type(notifyAnnotation.type())
                            .priority(notifyAnnotation.priority())
                            .groupId(finalGroupId)
                            .build();
                } catch (Exception e) {
                    log.error("Failed to send notification with template {}", 
                            notifyAnnotation.templateCode(), e);
                }
            };
            
            // Send asynchronously if configured
            if (notifyAnnotation.async()) {
                CompletableFuture.runAsync(sendTask, taskExecutor);
            } else {
                sendTask.run();
            }
            
            return result;
        } catch (Exception e) {
            log.error("Error processing @Notify annotation", e);
            return result;
        }
    }
    
    private StandardEvaluationContext createEvaluationContext(ProceedingJoinPoint joinPoint) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        
        // Add method parameters to context
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        
        for (int i = 0; i < paramNames.length; i++) {
            context.setVariable(paramNames[i], args[i]);
        }
        
        return context;
    }
    
    @SuppressWarnings("unchecked")
    private Map<String, Object> evaluateTemplateParams(String expression, StandardEvaluationContext context, 
            ProceedingJoinPoint joinPoint) {
        if ("#root".equals(expression)) {
            // Special case: use the entire context as parameters
            Map<String, Object> params = new HashMap<>();
            // Get parameter names from method signature
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            String[] paramNames = signature.getParameterNames();
            for (String name : paramNames) {
                params.put(name, context.lookupVariable(name));
            }
            return params;
        } else {
            // Evaluate the expression to get parameters
            Object value = evaluateExpression(expression, context, Object.class);
            if (value instanceof Map) {
                return (Map<String, Object>) value;
            } else {
                log.warn("Template params expression did not evaluate to a Map: {}", expression);
                return new HashMap<>();
            }
        }
    }
    
    private <T> T evaluateExpression(String expressionString, StandardEvaluationContext context, Class<T> resultType) {
        Expression expression = expressionParser.parseExpression(expressionString);
        return expression.getValue(context, resultType);
    }
}