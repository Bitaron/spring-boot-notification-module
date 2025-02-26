# Notification Module

A comprehensive, multi-channel notification delivery system for Java applications, built with Spring Boot.

## Table of Contents

- [Overview](#overview)
- [Requirements](#requirements)
- [Installation](#installation)
- [Database Setup](#database-setup)
- [Configuration Properties](#configuration-properties)
- [Usage Examples](#usage-examples)
- [Advanced Features](#advanced-features)
- [API Reference](#api-reference)
- [Troubleshooting](#troubleshooting)

## Overview

Notification Module is a flexible and extensible framework for sending notifications through various channels. It supports email, SMS, web notifications, and mobile push notifications, with a template-based content generation system and comprehensive delivery management.

### Key Features

- Multi-channel notification delivery
- Template-based content generation with FreeMarker
- Retry mechanisms for failed notifications
- Priority-based delivery
- Message queuing
- Notification status tracking
- Internationalization support
- Metadata support for custom attributes
- WebSocket support for real-time web notifications

## Requirements

- Java 17 or higher
- Spring Boot 3.4.3 or higher
- PostgreSQL 13 or higher
- Maven 3.6 or higher

## Installation

### Maven Dependency

Add the following dependency to your Maven project:

```xml
<dependency>
    <groupId>com.notification</groupId>
    <artifactId>notification-module</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Local JAR Installation

Alternatively, you can build and install the module locally:

```bash
cd /path/to/notification-module
mvn clean install
```

Then add it as a system dependency:

```xml
<dependency>
    <groupId>com.notification</groupId>
    <artifactId>notification-module</artifactId>
    <version>1.0.0</version>
    <scope>system</scope>
    <systemPath>${project.basedir}/path/to/notification-module-1.0.0.jar</systemPath>
</dependency>
```

## Database Setup

The module requires a PostgreSQL database with the schema defined in:
`/schema/notification_module_schema.sql`

Execute this script to create the required tables, indexes, and default templates.

## Configuration Properties

The module can be configured through the following properties in your `application.yml` or `application.properties` file:

### Core Properties

| Property                                  | Type      | Default          | Description                                           |
|-------------------------------------------|-----------|------------------|-------------------------------------------------------|
| `notification.use-queue`                  | boolean   | false            | Enable/disable message queuing                        |
| `notification.default-locale`             | string    | en               | Default locale for templates                          |
| `notification.retention-days`             | integer   | 30               | Number of days to keep notifications before cleanup   |
| `notification.max-batch-size`             | integer   | 100              | Maximum number of notifications processed in one batch|
| `notification.enable-throttling`          | boolean   | false            | Enable/disable notification rate limiting             |
| `notification.max-notifications-per-second` | integer | 50               | Maximum notifications processed per second if throttling is enabled |

### Email Delivery Properties

| Property                                  | Type      | Default          | Description                                           |
|-------------------------------------------|-----------|------------------|-------------------------------------------------------|
| `notification.email.from-address`         | string    | noreply@example.com | Default sender email address                       |
| `notification.email.default-subject`      | string    | Notification     | Default subject line for emails                       |
| `notification.email.max-attachment-size-mb` | integer | 10               | Maximum attachment size in MB                         |
| `notification.email.html-enabled-by-default` | boolean | true            | Whether HTML is enabled by default for emails         |

### SMS Delivery Properties

| Property                                  | Type      | Default          | Description                                           |
|-------------------------------------------|-----------|------------------|-------------------------------------------------------|
| `notification.sms.provider`               | string    | mock             | SMS provider name (mock, twilio, etc.)               |
| `notification.sms.provider-url`           | string    | -                | API URL for the SMS provider                          |
| `notification.sms.max-length`             | integer   | 160              | Maximum SMS length in characters                      |
| `notification.sms.split-long-messages`    | boolean   | true             | Whether to split long messages into multiple SMS      |

### Push Notification Properties

| Property                                  | Type      | Default          | Description                                           |
|-------------------------------------------|-----------|------------------|-------------------------------------------------------|
| `notification.push.default-android-icon`  | string    | ic_notification  | Default icon for Android push notifications           |
| `notification.push.default-sound`         | string    | default          | Default sound for push notifications                  |
| `notification.push.time-to-live`          | integer   | 2419200          | Time to live for push notifications in seconds        |

### WebSocket Properties

| Property                                  | Type      | Default          | Description                                           |
|-------------------------------------------|-----------|------------------|-------------------------------------------------------|
| `notification.websocket.endpoint`         | string    | /ws              | WebSocket connection endpoint                         |
| `notification.websocket.topic-prefix`     | string    | /topic           | Prefix for broadcast topics                           |
| `notification.websocket.user-destination-prefix` | string | /user        | Prefix for user-specific destinations                 |
| `notification.websocket.notification-topic` | string  | notifications    | Topic name for notifications                          |
| `notification.websocket.allowed-origins`  | string    | *                | Allowed origins for WebSocket connections             |

### Retry Properties

| Property                                  | Type      | Default          | Description                                           |
|-------------------------------------------|-----------|------------------|-------------------------------------------------------|
| `notification.retry.enabled`              | boolean   | true             | Enable/disable retry mechanism                        |
| `notification.retry.max-attempts`         | integer   | 3                | Maximum retry attempts for failed notifications        |
| `notification.retry.base-delay-seconds`   | integer   | 60               | Base delay between retry attempts in seconds          |
| `notification.retry.min-delay-seconds`    | integer   | 5                | Minimum delay between retries in seconds              |
| `notification.retry.max-delay-seconds`    | integer   | 3600             | Maximum delay between retries in seconds              |
| `notification.retry.exponential-backoff`  | boolean   | true             | Whether to use exponential backoff for retries        |
| `notification.retry.backoff-multiplier`   | float     | 2.0              | Multiplier for exponential backoff                    |
| `notification.retry.add-jitter`           | boolean   | true             | Add random jitter to retry delays                     |

### Scheduler Properties

| Property                                  | Type      | Default          | Description                                           |
|-------------------------------------------|-----------|------------------|-------------------------------------------------------|
| `notification.scheduler.enabled`          | boolean   | true             | Enable/disable the notification scheduler             |
| `notification.scheduler.scheduled-interval-ms` | integer | 60000         | Interval for processing scheduled notifications (ms)  |
| `notification.scheduler.retry-interval-ms` | integer | 60000            | Interval for processing retry attempts (ms)           |

### Channel Configuration

The notification module implements a flexible yet strict channel configuration system. Each channel (SMS, Email, Web) can be independently enabled or disabled, and when a channel is enabled, the appropriate provider interface must be implemented.

#### Configuration Properties

Channel configuration is managed through the `notification.channels` property namespace:

| Property                                        | Type      | Default           | Description                                           |
|------------------------------------------------|-----------|-------------------|-------------------------------------------------------|
| `notification.channels.email.enabled`           | boolean   | false             | Enable/disable email notifications                    |
| `notification.channels.email.from-address`      | string    | noreply@example.com | Default sender email address                        |
| `notification.channels.email.default-subject`   | string    | Notification      | Default subject for email notifications               |
| `notification.channels.email.max-attachment-size-mb` | integer | 10            | Maximum attachment size in MB                        |
| `notification.channels.email.html-enabled-by-default` | boolean | true        | Whether HTML is enabled by default for emails         |
| `notification.channels.sms.enabled`             | boolean   | false             | Enable/disable SMS notifications                      |
| `notification.channels.sms.max-length`          | integer   | 160               | Maximum SMS length in characters                      |
| `notification.channels.sms.split-long-messages` | boolean   | true              | Whether to split long messages into multiple SMS      |
| `notification.channels.web.enabled`             | boolean   | true              | Enable/disable web notifications                      |

#### Channel Implementation Requirements

For each enabled channel, clients must implement the corresponding provider interface:

1. **Email Channel**: If `notification.channels.email.enabled=true`, an implementation of `EmailDeliveryProvider` must be provided.
2. **SMS Channel**: If `notification.channels.sms.enabled=true`, an implementation of `SmsDeliveryProvider` must be provided.
3. **Web Channel**: Web notifications are always enabled by default and don't require any external provider.

Example of implementing an SMS provider:

```java
@Service
@ConditionalOnProperty(prefix = "notification.channels", name = "sms.enabled", havingValue = "true")
public class MySmsProvider implements SmsDeliveryProvider {
    
    @Override
    public boolean sendSms(String phoneNumber, String message) {
        // Implementation to send SMS via your preferred gateway
        return true; // return success/failure
    }
    
    @Override
    public boolean isConfigured() {
        // Check if all required config is available
        return true;
    }
}
```

#### Bean Validation

The notification module includes compile-time and runtime checks:

1. **Compile-time**: If a channel is enabled but no provider implementation is found, the application will fail to start.
2. **Runtime**: If a channel is enabled but the provider reports it's not properly configured, the application will throw an exception during startup.

This approach ensures that notifications are only attempted when the channel is properly configured, preventing silent failures.

## Usage Examples

### Basic Configuration

```yaml
notification:
  use-queue: false
  default-locale: en
  max-batch-size: 100
  
  email:
    from-address: notifications@mycompany.com
    default-subject: Important Notification
  
  sms:
    provider: twilio
    provider-url: https://api.twilio.com/2010-04-01
  
  retry:
    enabled: true
    max-attempts: 5
    base-delay-seconds: 30
```

### Core Components

#### 1. Spring Boot Application Configuration

Enable the notification module in your Spring Boot application:

```java
@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = {"com.myapp", "com.notification"})
@EntityScan(basePackages = {"com.myapp.domain", "com.notification.domain"})
@EnableJpaRepositories(basePackages = {"com.myapp.repository", "com.notification.repository"})
public class MyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}
```

#### 2. Sending a Simple Email Notification

```java
@Service
public class MyService {
    private final NotificationService notificationService;
    
    @Autowired
    public MyService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
    
    public void sendEmailNotification(String recipient, String subject, String content) {
        Notification notification = Notification.builder()
                .recipient(recipient)
                .subject(subject)
                .content(content)
                .type(NotificationType.INFO)
                .channel(DeliveryChannel.EMAIL)
                .priority(NotificationPriority.NORMAL)
                .status(NotificationStatus.PENDING)
                .htmlEnabled(true)
                .build();
        
        notificationService.send(notification);
    }
}
```

#### 3. Sending an SMS Notification

```java
public void sendSmsNotification(String phoneNumber, String message) {
    Notification notification = Notification.builder()
            .recipient(phoneNumber)
            .content(message)
            .type(NotificationType.INFO)
            .channel(DeliveryChannel.SMS)
            .priority(NotificationPriority.HIGH)
            .status(NotificationStatus.PENDING)
            .build();
    
    notificationService.send(notification);
}
```

#### 4. Using Templates

```java
public void sendWelcomeEmail(String email, String name) {
    Map<String, Object> data = new HashMap<>();
    data.put("name", name);
    data.put("loginUrl", "https://myapp.com/login");
    
    notificationService.sendWithTemplate("welcome_email", email, data);
}
```

#### 5. Creating a Template

```java
public void createWelcomeTemplate() {
    Template template = Template.builder()
            .code("welcome_email")
            .name("Welcome Email")
            .description("Email sent to new users")
            .content("<html><body><h1>Welcome, ${name}!</h1><p>Thank you for joining us.</p></body></html>")
            .subject("Welcome to Our Service")
            .channel(DeliveryChannel.EMAIL)
            .locale("en")
            .htmlEnabled(true)
            .active(true)
            .build();
    
    templateService.saveTemplate(template);
}
```

#### 6. Using the Notification Annotation

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @PostMapping("/{userId}/deactivate")
    @Notify(
        recipient = "#userId + '@example.com'",
        subject = "Account Deactivated",
        content = "Your account has been deactivated",
        channel = "EMAIL"
    )
    public ResponseEntity<String> deactivateUser(@PathVariable String userId) {
        // Deactivation logic here
        return ResponseEntity.ok("User deactivated");
    }
}
```

## Advanced Features

### Custom Delivery Channel Implementation

To implement a custom delivery channel:

1. Create a class that implements the appropriate interface:

```java
@Component
public class MyCustomEmailSender implements EmailSender {
    
    @Override
    public boolean sendEmail(String to, String subject, String content, boolean isHtml) {
        // Custom implementation
        return true;
    }
    
    @Override
    public String getName() {
        return "MyCustomEmailSender";
    }
    
    @Override
    public boolean isDefault() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return true;
    }
}
```

### Custom Template Resolver

```java
@Component
public class MyCustomTemplateResolver implements TemplateResolver {
    
    @Override
    public String processTemplate(String templateCode, Map<String, Object> data) {
        // Custom template resolving logic
        return processedContent;
    }
    
    @Override
    public String getName() {
        return "MyCustomTemplateResolver";
    }
    
    @Override
    public boolean isDefault() {
        return false;
    }
}
```

### Using Metadata

```java
Notification notification = Notification.builder()
        .recipient("user@example.com")
        .subject("Your Order")
        .content("Your order has been processed")
        .type(NotificationType.INFO)
        .channel(DeliveryChannel.EMAIL)
        .priority(NotificationPriority.NORMAL)
        .metadata(Map.of(
            "orderId", "12345",
            "product", "Premium Plan",
            "amount", "99.99"
        ))
        .build();
```

## API Reference

### Core Services

#### NotificationService

- `Notification send(Notification notification)` - Send a notification
- `Notification sendWithTemplate(String templateCode, String recipient, Map<String, Object> data)` - Send a notification using a template
- `List<Notification> sendBatch(List<Notification> notifications)` - Send multiple notifications in a batch
- `void cancelNotification(UUID notificationId)` - Cancel a pending notification
- `Notification getNotification(UUID notificationId)` - Get a notification by ID

#### TemplateService

- `Template saveTemplate(Template template)` - Create or update a template
- `Template getTemplate(UUID templateId)` - Get a template by ID
- `Template getTemplateByCode(String code)` - Get a template by code
- `List<Template> getTemplatesByChannel(DeliveryChannel channel)` - Get templates by channel
- `void deleteTemplate(UUID templateId)` - Delete a template

#### RetryService

- `void scheduleRetry(Notification notification)` - Schedule a retry for a failed notification
- `boolean shouldRetry(Notification notification)` - Check if a notification should be retried
- `int calculateNextRetryDelay(Notification notification)` - Calculate delay for next retry attempt

### Domain Objects

#### Notification

```java
Notification.builder()
    .id(UUID.randomUUID())  // Optional, auto-generated if not provided
    .recipient(String)      // Required: Email, phone number, user ID, etc.
    .subject(String)        // Optional: Subject line for email notifications
    .content(String)        // Required: Notification content
    .type(NotificationType) // Optional: INFO, WARNING, ERROR, SUCCESS
    .channel(DeliveryChannel) // Required: EMAIL, SMS, WEB, PUSH
    .priority(NotificationPriority) // Optional: LOW, NORMAL, HIGH, URGENT
    .status(NotificationStatus)   // Optional: PENDING, SENT, DELIVERED, FAILED
    .htmlEnabled(boolean)   // Optional: Whether HTML is enabled (for EMAIL)
    .scheduledFor(LocalDateTime) // Optional: Schedule for future delivery
    .expiresAt(LocalDateTime)    // Optional: Expiration time
    .metadata(Map<String, Object>) // Optional: Custom metadata
    .build();
```

#### Template

```java
Template.builder()
    .id(UUID.randomUUID())  // Optional, auto-generated if not provided
    .code(String)           // Required: Unique template code
    .name(String)           // Required: Template name
    .description(String)    // Optional: Template description
    .content(String)        // Required: Template content (FreeMarker format)
    .subject(String)        // Optional: Subject template for email notifications
    .channel(DeliveryChannel) // Required: EMAIL, SMS, WEB, PUSH
    .locale(String)         // Optional: Locale code (e.g., "en", "fr")
    .htmlEnabled(boolean)   // Optional: Whether HTML is enabled
    .active(boolean)        // Optional: Whether template is active
    .build();
```

### Enums

#### DeliveryChannel

- `EMAIL` - Email notifications
- `SMS` - SMS messages
- `WEB` - Web notifications (WebSocket)
- `PUSH` - Mobile push notifications

#### NotificationPriority

- `LOW` - Low priority
- `NORMAL` - Normal priority (default)
- `HIGH` - High priority
- `URGENT` - Urgent priority

#### NotificationType

- `INFO` - Informational notification
- `WARNING` - Warning notification
- `ERROR` - Error notification
- `SUCCESS` - Success notification

#### NotificationStatus

- `CREATED` - Initial state
- `PENDING` - Awaiting delivery
- `PROCESSING` - Currently being processed
- `SENT` - Sent to delivery provider
- `DELIVERED` - Successfully delivered
- `FAILED` - Delivery failed
- `CANCELLED` - Cancelled before delivery
- `EXPIRED` - Expired before delivery

## Troubleshooting

### Common Issues

#### Database Connectivity

If notifications are not being saved or retrieved:

1. Verify database connection properties
2. Ensure the database schema is properly created
3. Check database permissions

#### Email Not Sending

1. Verify SMTP settings in your Spring configuration
2. Check the email provider's logs
3. Ensure the `notification.email.from-address` is valid

#### SMS Not Sending

1. Verify SMS provider configuration
2. Check API credentials
3. Ensure recipient phone numbers are in the correct format (e.g., +1234567890)

#### Template Errors

1. Check template syntax for FreeMarker errors
2. Verify the template exists in the database
3. Ensure all required template variables are provided in the data map

### Logging

To enable detailed logging, add the following to your `application.yml`:

```yaml
logging:
  level:
    com.notification: DEBUG
```

### Performance Tuning

For high-volume notification systems:

1. Enable queuing with `notification.use-queue: true`
2. Increase batch size with `notification.max-batch-size: 250`
3. Consider adding database indexes on frequently queried columns
4. Use throttling for rate-limited providers: `notification.enable-throttling: true`
