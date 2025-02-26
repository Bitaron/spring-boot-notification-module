package com.notification.performance;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.notification.config.NotificationProperties;
import com.notification.domain.notification.DeliveryChannel;
import com.notification.domain.notification.Notification;
import com.notification.domain.notification.NotificationPriority;
import com.notification.domain.notification.NotificationType;
import com.notification.service.NotificationService;
import com.notification.service.delivery.sms.MockSmsSender;

@SpringBootTest
@ActiveProfiles("test")
class BatchProcessingTest {

    private static final Logger log = LoggerFactory.getLogger(BatchProcessingTest.class);
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private NotificationProperties properties;
    
    @Autowired
    private MockSmsSender mockSmsSender;
    
    @Test
    void testBatchProcessing() throws Exception {
        // Create a list of notifications
        List<Notification> notifications = new ArrayList<>();
        int batchSize = 100;
        
        for (int i = 0; i < batchSize; i++) {
            Notification notification = new Notification();
            notification.setRecipient("test" + i + "@example.com");
            notification.setSubject("Batch Test " + i);
            notification.setContent("This is batch test notification " + i);
            notification.setChannel(DeliveryChannel.EMAIL);
            notification.setType(NotificationType.INFO);
            notification.setPriority(NotificationPriority.NORMAL);
            
            notifications.add(notification);
        }
        
        // Setup a latch to wait for completion if the service is async
        final CountDownLatch latch = new CountDownLatch(1);
        
        // Start timing
        Instant startTime = Instant.now();
        
        // Process notifications
        List<Notification> sentNotifications = notificationService.sendBatch(notifications);
        
        // Release latch
        latch.countDown();
        
        // Wait for processing to complete (timeout after 10 seconds)
        boolean completed = latch.await(10, TimeUnit.SECONDS);
        assertTrue(completed, "Batch processing did not complete in time");
        
        // End timing
        Instant endTime = Instant.now();
        
        // Verify results
        assertEquals(batchSize, sentNotifications.size(), "All notifications should be processed");
        
        // Check that all notifications were processed
        for (Notification notification : sentNotifications) {
            assertNotNull(notification.getId(), "Notification should have an ID");
            assertNotNull(notification.getSentAt(), "Notification should have a sent timestamp");
        }
        
        // Log performance
        Duration duration = Duration.between(startTime, endTime);
        long processingTimeMs = duration.toMillis();
        double processingTimePerNotification = (double) processingTimeMs / batchSize;
        
        log.info("Batch processing: {} notifications processed in {} ms", batchSize, processingTimeMs);
        log.info("Average processing time per notification: {} ms", processingTimePerNotification);
        
        // Make a performance assertion (adjust threshold as needed)
        assertTrue(processingTimePerNotification < 50.0, 
                   "Average processing time per notification should be less than 50ms, but was " + processingTimePerNotification + "ms");
    }
}