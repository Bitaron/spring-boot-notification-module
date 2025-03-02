package com.notification.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.notification.domain.notification.NotificationChannel;
import com.notification.domain.notification.Notification;
import com.notification.domain.notification.NotificationStatus;
import com.notification.domain.notification.NotificationType;

/**
 * Repository for managing notifications.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID>, NotificationRepositoryCustom {
    
    /**
     * Finds notifications by recipient.
     *
     * @param recipient The recipient identifier
     * @param pageable Pagination information
     * @return Page of notifications for the recipient
     */
    Page<Notification> findByRecipient(String recipient, Pageable pageable);
    
    /**
     * Finds notifications by status.
     *
     * @param status The notification status
     * @param pageable Pagination information
     * @return Page of notifications with the status
     */
    Page<Notification> findByStatus(NotificationStatus status, Pageable pageable);
    
    /**
     * Finds notifications by type.
     *
     * @param type The notification type
     * @param pageable Pagination information
     * @return Page of notifications of the type
     */
    Page<Notification> findByType(NotificationType type, Pageable pageable);
    
    /**
     * Finds notifications by channel.
     *
     * @param channel The delivery channel
     * @param pageable Pagination information
     * @return Page of notifications for the channel
     */
    Page<Notification> findByChannel(NotificationChannel channel, Pageable pageable);
    
    /**
     * Finds notifications by group ID.
     *
     * @param groupId The group identifier
     * @param pageable Pagination information
     * @return Page of notifications in the group
     */
    Page<Notification> findByGroupId(String groupId, Pageable pageable);
    
    /**
     * Finds scheduled notifications that are due for delivery.
     *
     * @param status The notification status
     * @param dateTime The current time
     * @return List of scheduled notifications that are due
     */
    List<Notification> findByStatusAndScheduledForBefore(
            NotificationStatus status, 
            LocalDateTime dateTime);
    
    /**
     * Finds notifications that need retry attempts.
     *
     * @param status The retry status
     * @param now The current time
     * @return List of notifications that need retry
     */
    List<Notification> findByStatusAndNextRetryAtBefore(NotificationStatus status, LocalDateTime now);
    
    /**
     * Searches notifications by recipient, subject or content.
     *
     * @param searchTerm The search term
     * @param pageable Pagination information
     * @return Page of matching notifications
     */
    @Query(value = "SELECT n FROM Notification n WHERE " +
           "LOWER(n.recipient) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(n.subject) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(CAST(n.content AS string)) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Notification> search(String searchTerm, Pageable pageable);
    
    /**
     * Alternative search method for PostgreSQL. This can be used 
     * if the client has configured PostgreSQL specifically.
     *
     * @param searchTerm The search term
     * @param pageable Pagination information
     * @return Page of matching notifications
     */
    @Query(value = "SELECT * FROM notifications n WHERE " +
           "n.recipient ILIKE CONCAT('%', :searchTerm, '%') OR " +
           "n.subject ILIKE CONCAT('%', :searchTerm, '%') OR " +
           "n.content ILIKE CONCAT('%', :searchTerm, '%')", 
           nativeQuery = true)
    Page<Notification> searchPostgres(String searchTerm, Pageable pageable);
    
    /**
     * Alternative search method for H2 database. This can be used
     * if the client has configured H2 specifically.
     *
     * @param searchTerm The search term
     * @param pageable Pagination information
     * @return Page of matching notifications
     */
    @Query(value = "SELECT * FROM notifications n WHERE " +
           "LOWER(n.recipient) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(n.subject) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(CAST(n.content AS VARCHAR)) LIKE LOWER(CONCAT('%', :searchTerm, '%'))", 
           nativeQuery = true)
    Page<Notification> searchH2(String searchTerm, Pageable pageable);
} 