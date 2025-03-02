package com.notification.service.delivery.email;

import java.io.File;

import com.notification.service.delivery.DeliveryException;
import com.notification.service.delivery.DeliveryService;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.notification.config.EmailProperties;
import com.notification.domain.notification.NotificationChannel;
import com.notification.domain.notification.Notification;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for delivering notifications via email.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailDeliveryService implements DeliveryService {

    private final JavaMailSender mailSender;
    private final EmailProperties emailProperties;


    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.EMAIL;
    }

    @Override
    public void deliver(Notification notification) throws DeliveryException {
        if (!isSupported()) {
            throw new DeliveryException("Email delivery is not configured properly");
        }

        try {
            String fromAddress = emailProperties.getFromAddress();
            String recipient = notification.getRecipient();

            // Use notification subject or default
            String subject = notification.getSubject();
            if (subject == null || subject.isEmpty()) {
                subject = emailProperties.getDefaultSubject();
            }

            String content = notification.getContent();
            boolean isHtml = notification.getHtmlEnabled();

            String attachmentUrl = notification.getAttachmentUrl();
            File attachment = null;

            if (attachmentUrl != null && !attachmentUrl.isEmpty()) {
                attachment = new File(attachmentUrl);
            }

            log.info("Sending email to {} with subject: {}", notification.getRecipient(), notification.getSubject());

            sendEmail(fromAddress, recipient, subject, content, isHtml, attachment);

        } catch (Exception e) {
            throw new DeliveryException("Failed to deliver email notification", e);
        }
    }

    private void sendEmail(String from, String to, String subject, String content, boolean isHtml, File attachment)
            throws Exception {

        if (from == null || from.isEmpty()) {
            from = emailProperties.getFromAddress();
        }

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, attachment != null);

        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, isHtml);

        if (attachment != null) {
            helper.addAttachment(attachment.getName(), attachment);
        }

        mailSender.send(message);
    }

    @Override
    public boolean isSupported() {
        return emailProperties != null &&
                emailProperties.getFromAddress() != null &&
                !emailProperties.getFromAddress().isEmpty();
    }
} 