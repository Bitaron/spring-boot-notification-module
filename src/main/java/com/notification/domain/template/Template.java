package com.notification.domain.template;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.notification.domain.notification.DeliveryChannel;

/**
 * Represents a notification template stored in the database.
 */
@Entity
@Table(name = "notification_templates")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Template {
    
    @Id
    private UUID id;
    
    /**
     * Unique code to identify the template.
     */
    @Column(nullable = false, unique = true)
    private String code;
    
    /**
     * Human-readable name for the template.
     */
    @Column(nullable = false)
    private String name;
    
    /**
     * Description of the template's purpose.
     */
    private String description;
    
    /**
     * The actual template content.
     */
    @Lob
    @Column(nullable = false)
    private String content;
    
    /**
     * Subject template for channels that support it (e.g., email).
     */
    private String subject;
    
    /**
     * The delivery channel this template is designed for.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryChannel channel;
    
    /**
     * Language/locale code (e.g., 'en', 'fr', etc.).
     */
    @Column(nullable = false)
    private String locale;
    
    /**
     * Whether this template supports HTML formatting.
     */
    private boolean htmlEnabled;
    
    /**
     * When the template was created.
     */
    private LocalDateTime createdAt;
    
    /**
     * When the template was last updated.
     */
    private LocalDateTime updatedAt;
    
    /**
     * Whether the template is active and available for use.
     */
    private boolean active;
    
    /**
     * Pre-persist hook to set default values.
     */
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (active) {
            active = true;
        }
        updatedAt = LocalDateTime.now();
    }
} 