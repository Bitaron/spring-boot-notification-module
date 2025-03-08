package com.notification.service.builder;

import com.notification.domain.notification.NotificationChannel;

import java.util.HashMap;
import java.util.Map;

public class Recipient {
    private final String recipientId;
    private final Map<NotificationChannel, String> address;
    private final RecipientMessage message;

    private Recipient(String recipientId, Map<NotificationChannel, String> address, RecipientMessage message) {
        this.recipientId = recipientId;
        this.address = Map.copyOf(address);
        this.message = message;
    }

    public static class Builder {
        private final String recipientId;
        private final Map<NotificationChannel, String> address = new HashMap<>();
        private RecipientMessage message;

        public Builder(String recipientId) {
            this.recipientId = recipientId;
        }

        public Builder(String recipientId, Map<NotificationChannel, String> address) {
            this.recipientId = recipientId;
            this.address.putAll(address);
        }

        public Builder addAddress(NotificationChannel key, String value) {
            this.address.put(key, value);
            return this;
        }

        public Builder setMessage(RecipientMessage message) {
            this.message = message;
            return this;
        }

        public Recipient build() {
            return new Recipient(recipientId, address, message);
        }
    }

    public String getRecipientId() {
        return recipientId;
    }

    public Map<NotificationChannel, String> getAddress() {
        return address;
    }

    public RecipientMessage getMessage() {
        return message;
    }
}