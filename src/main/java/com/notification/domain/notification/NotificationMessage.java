package com.notification.domain.notification;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    @Column(name = "attachment_url" )
    private String attachmentUrl;

    @ElementCollection
    @CollectionTable(
            name = "notification_template_data",
            joinColumns = @JoinColumn(name = "message_id")
    )
    @MapKeyColumn(name = "data_key")
    @Column(name = "data_value")
    private Map<String, Object> templateData = new HashMap<>();

    // Getters and Setters
}