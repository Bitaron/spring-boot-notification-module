package com.notification.service.builder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Recipient {
    private final String recipientId;
    private final Map<String, String> attributes;
    private final RecipientMessage message;

    private Recipient(String recipientId, Map<String, String> attributes, RecipientMessage message) {
        this.recipientId = recipientId;
        this.attributes = Collections.unmodifiableMap(new HashMap<>(attributes));
        this.message = message;
    }

    public static class Builder {
        private final String recipientId;
        private final Map<String, String> attributes = new HashMap<>();
        private RecipientMessage message;

        public Builder(String recipientId) {
            this.recipientId = recipientId;
        }

        public Builder addAttribute(String key, String value) {
            this.attributes.put(key, value);
            return this;
        }

        public Builder setMessage(RecipientMessage message) {
            this.message = message;
            return this;
        }

        public Recipient build() {
            return new Recipient(recipientId, attributes, message);
        }
    }

    public String getRecipientId() {
        return recipientId;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public RecipientMessage getMessage() {
        return message;
    }
}