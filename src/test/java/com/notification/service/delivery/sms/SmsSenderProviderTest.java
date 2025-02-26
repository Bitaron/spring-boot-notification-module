package com.notification.service.delivery.sms;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import com.notification.config.SmsProperties;

@SpringBootTest
@DirtiesContext
@TestPropertySource(properties = {"notification.sms.provider=mock"})
public class SmsSenderProviderTest {

    @Autowired
    private ApplicationContext context;
    
    @Autowired
    private SmsProperties smsProperties;
    
    @Test
    void testDefaultSmsSenderIsUsedByDefault() {
        SmsSender smsSender = context.getBean(SmsSender.class);
        assertTrue(smsSender instanceof MockSmsSender, 
                "Expected MockSmsSender to be used due to TestPropertySource configuration");
    }
    
    // Testing that the correct bean is activated based on properties
    @Test
    void testMockSmsSenderActivatedWithProperty() {
        // Verify the property is set properly
        assertEquals("mock", smsProperties.getProvider(), 
                "SmsProperties should have 'mock' as the provider value");
        
        // Get the SmsSender bean and verify it's a MockSmsSender
        SmsSender smsSender = context.getBean(SmsSender.class);
        assertTrue(smsSender instanceof MockSmsSender, 
                "Expected MockSmsSender to be used based on properties configuration");
        
        // Verify additional bean properties if needed
        assertNotNull(smsSender, "SMS sender should not be null");
        assertTrue(smsSender.isConfigured(), "SMS sender should be configured");
    }

    @Test
    void testProviderSelection() {
        // Test that we have both implementations available as separate beans
        assertTrue(context.containsBean("defaultSmsSender"), 
                "DefaultSmsSender bean should be available in the application context");
        assertTrue(context.containsBean("mockSmsSender"), 
                "MockSmsSender bean should be available in the application context");
        
        // Verify primary bean is the mock sender due to properties configuration
        SmsSender primarySender = context.getBean(SmsSender.class);
        assertTrue(primarySender instanceof MockSmsSender,
                "Primary SmsSender should be MockSmsSender based on properties");
        
        // Test that we can explicitly get the DefaultSmsSender bean if needed
        SmsSender defaultSender = context.getBean("defaultSmsSender", SmsSender.class);
        assertTrue(defaultSender instanceof DefaultSmsSender,
                "Should be able to explicitly get DefaultSmsSender by name");
        
        // Verify they are different instances
        assertNotSame(primarySender, defaultSender, 
                "Primary and default sender should be different instances");
    }
}