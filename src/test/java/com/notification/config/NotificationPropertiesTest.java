package com.notification.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class NotificationPropertiesTest {

    @Autowired
    private NotificationProperties properties;
    
    @Test
    void testPropertiesLoaded() {
        assertNotNull(properties);
        assertEquals("en", properties.getDefaultLocale());
        assertEquals(30, properties.getRetentionDays());
        assertEquals(100, properties.getMaxBatchSize());
        assertFalse(properties.isEnableThrottling());
        assertEquals(50, properties.getMaxNotificationsPerSecond());
    }
} 