package com.notification.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.notification.domain.notification.Notification;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.dialect.PostgreSQLDialect;

/**
 * Implementation of the NotificationRepositoryCustom interface.
 * Provides database-specific implementations for search queries.
 */
@Repository
public class NotificationRepositoryImpl implements NotificationRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;
    
    @Autowired
    @Lazy
    private NotificationRepository notificationRepository;
    
    @Override
    public Page<Notification> searchWithDatabaseSpecifics(String searchTerm, Pageable pageable) {
        // Get the dialect being used by Hibernate
        Dialect dialect = getDialect();
        
        if (dialect instanceof PostgreSQLDialect) {
            // Use PostgreSQL-specific search
            return notificationRepository.searchPostgres(searchTerm, pageable);
        } else if (dialect instanceof H2Dialect) {
            // Use H2-specific search
            return notificationRepository.searchH2(searchTerm, pageable);
        } else {
            // Fallback to generic JPQL search
            return notificationRepository.search(searchTerm, pageable);
        }
    }
    
    /**
     * Gets the Hibernate dialect being used.
     * This is a simplified approach for demonstration purposes.
     * In a production environment, you would inject the dialect.
     */
    private Dialect getDialect() {
        // For demonstration, we'll infer the dialect from the database product name
        try {
            String dbProductName = entityManager.getEntityManagerFactory()
                    .getProperties()
                    .get("hibernate.dialect")
                    .toString();
            
            if (dbProductName.contains("PostgreSQL")) {
                return new PostgreSQLDialect();
            } else if (dbProductName.contains("H2")) {
                return new H2Dialect();
            }
        } catch (Exception e) {
            // Fallback to looking at JDBC metadata
            try {
                String dbProductName = entityManager.unwrap(java.sql.Connection.class)
                        .getMetaData()
                        .getDatabaseProductName();
                
                if (dbProductName.contains("PostgreSQL")) {
                    return new PostgreSQLDialect();
                } else if (dbProductName.contains("H2")) {
                    return new H2Dialect();
                }
            } catch (Exception nested) {
                // Silently fail and use default search
            }
        }
        
        // Default case - we couldn't determine the dialect
        return null;
    }
}
