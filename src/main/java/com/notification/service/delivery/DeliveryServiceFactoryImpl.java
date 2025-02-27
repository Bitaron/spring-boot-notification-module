package com.notification.service.delivery;

import com.notification.domain.notification.Notification;
import com.notification.service.delivery.web.DeliveryServiceFactory;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Factory that provides the appropriate delivery service for a notification channel.
 */
@Service
public class DeliveryServiceFactoryImpl implements DeliveryServiceFactory {

    private static final Logger logger = LoggerFactory.getLogger(DeliveryServiceFactoryImpl.class);


    public List<DeliveryService> deliveryServices;

    public DeliveryServiceFactoryImpl(List<DeliveryService> deliveryServices) {
        this.deliveryServices = deliveryServices;
    }

    /**
     * Gets the delivery service for a specific notification.
     *
     * @param notification The notification to be delivered
     * @return The appropriate delivery service
     * @throws IllegalArgumentException if no service is found for the channel
     */
    @Override
    public DeliveryService getDeliveryService(Notification notification) {
        if (notification.getChannel() == null) {
            throw new IllegalArgumentException("Notification channel cannot be null");
        }

        Optional<DeliveryService> service = deliveryServices.stream()
                .filter(s -> s.isSupported() && s.getChannel() == notification.getChannel())
                .findFirst();

        if (service.isEmpty()) {
            throw new IllegalArgumentException("No delivery service found for channel: " + notification.getChannel() +
                    ". Make sure the channel is enabled in configuration and a provider is implemented.");
        }

        return service.get();
    }
}