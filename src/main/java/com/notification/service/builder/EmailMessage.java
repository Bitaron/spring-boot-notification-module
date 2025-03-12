package com.notification.service.builder;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class EmailMessage {
    private final String subject;
    private final boolean isHtml;
    private final String rawMessage;
    private final Set<String> attachmentUrls;

    private EmailMessage(String subject, boolean isHtml, String rawMessage, Set<String> attachmentUrls) {
        this.subject = subject;
        this.isHtml = isHtml;
        this.rawMessage = rawMessage;
        this.attachmentUrls = attachmentUrls;
    }


    /*public static class Builder {
        private String subject;
        private String htmlContent;
        private String plainTextContent;

        public Builder setSubject(String subject) {
            this.subject = subject;
            return this;
        }

        public Builder setHtmlContent(String htmlContent) {
            this.htmlContent = htmlContent;
            return this;
        }

        public Builder setPlainTextContent(String plainTextContent) {
            this.plainTextContent = plainTextContent;
            return this;
        }

        public EmailMessage build() {
            if (subject == null || subject.trim().isEmpty()) {
                throw new IllegalStateException("Email subject is required");
            }
            if ((htmlContent == null || htmlContent.trim().isEmpty())
                    && (plainTextContent == null || plainTextContent.trim().isEmpty())) {
                throw new IllegalStateException("Either HTML or plain text content is required");
            }
            return new EmailMessage(subject, htmlContent, plainTextContent);
        }
    }*/
}