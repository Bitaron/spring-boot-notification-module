package com.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.notification.domain.notification.DeliveryChannel;
import com.notification.domain.notification.Notification;
import com.notification.domain.notification.NotificationPriority;
import com.notification.domain.notification.NotificationType;
import com.notification.service.NotificationService;

@RestController
@RequestMapping("/test/notifications")
public class TestNotificationController {

    @Autowired
    private NotificationService notificationService;
    
    @PostMapping("/sms")
    public Notification sendTestSms(@RequestBody TestSmsRequest request) {
        Notification notification = new Notification();
        notification.setRecipient(request.getPhoneNumber());
        notification.setContent(request.getMessage());
        notification.setChannel(DeliveryChannel.SMS);
        notification.setType(NotificationType.INFO);
        notification.setPriority(NotificationPriority.NORMAL);
        
        return notificationService.sendNotificationImmediately(notification);
    }
    
    public static class TestSmsRequest {
        private String phoneNumber;
        private String message;
        
        // Getters and setters
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
} 