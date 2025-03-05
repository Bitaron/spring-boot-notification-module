package com.notification.service.delivery;

import com.notification.domain.notification.NotificationChannel;

public interface DeliveryServiceFactory {
    DeliveryService getDeliveryService(NotificationChannel channel);
}
