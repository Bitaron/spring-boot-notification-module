package com.notification.domain.notification;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Entity
@Table(name = "notification_messages")
public class NotificationMessage extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "template_name")
    private String templateName;

    @Column(name = "raw_message", columnDefinition = "TEXT")
    private String rawMessage;

    @Column(name = "subject")
    private String subject;

    @Column(name = "is_html")
    private Boolean isHtml;

    @Column(name = "attachment_url")
    private String attachmentUrl;

    @Type(JsonBinaryType.class)
    @Column(name = "template_data", columnDefinition = "jsonb")
    private Map<String, Object> templateData = new HashMap<>();

    // Getters and Setters
}