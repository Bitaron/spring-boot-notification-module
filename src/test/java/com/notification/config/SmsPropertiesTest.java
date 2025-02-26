package com.notification.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class SmsPropertiesTest {

    @Autowired
    private SmsProperties properties;
    
    @Test
    void testPropertiesLoaded() {
        assertNotNull(properties);
        assertEquals("default", properties.getProvider());
        assertEquals("test-api-key", properties.getApiKey());
        assertEquals(160, properties.getMaxLength());
        assertTrue(properties.isSplitLongMessages());
    }
} 