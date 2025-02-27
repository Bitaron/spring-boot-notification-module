package com.notification.service.delivery.web;

import com.notification.domain.notification.Notification;
import com.notification.service.delivery.DeliveryService;

public interface DeliveryServiceFactory {
    DeliveryService getDeliveryService(Notification notification);
}
