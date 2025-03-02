package com.notification.repository;

import com.notification.domain.notification.Notification;
import com.notification.domain.notification.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repository for managing notifications.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Modifying
    @Query("UPDATE Notification n SET n.status = :status, n.updatedAt = :updatedAt, " +
            "n.updatedBy = :updatedBy WHERE n.notificationId = :notificationId")
    void updateStatus(String notificationId, NotificationStatus status,
                      LocalDateTime updatedAt, String updatedBy);

    Optional<Notification> findByNotificationId(String notificationId);
}