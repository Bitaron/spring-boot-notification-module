package com.notification.service.builder;

public class EmailMessage {
    private final String subject;
    private final String htmlContent;
    private final String plainTextContent;

    private EmailMessage(String subject, String htmlContent, String plainTextContent) {
        this.subject = subject;
        this.htmlContent = htmlContent;
        this.plainTextContent = plainTextContent;
    }

    public String getSubject() {
        return subject;
    }

    public String getHtmlContent() {
        return htmlContent;
    }

    public String getPlainTextContent() {
        return plainTextContent;
    }

    public static class Builder {
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
    }
}