package com.notification.service.delivery.email;

import com.notification.config.EmailProperties;
import com.notification.domain.notification.Notification;
import com.notification.domain.notification.NotificationChannel;
import com.notification.domain.notification.NotificationMessage;
import com.notification.domain.notification.NotificationRecipient;
import com.notification.service.NotificationMessageResolver;
import com.notification.service.delivery.DeliveryException;
import com.notification.service.delivery.DeliveryService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * Service for delivering notifications via email.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailDeliveryService implements DeliveryService {

    private final JavaMailSender mailSender;
    private final EmailProperties emailProperties;
    private final NotificationMessageResolver notificationMessageResolver;


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
            for (NotificationRecipient notificationRecipient : notification.getRecipients()) {
                String recipient = notificationRecipient.getAddress().getOrDefault(getChannel(), "");
                if (!recipient.isEmpty()) {
                    NotificationMessage notificationMessage = notificationRecipient.getMessage();
                    // Use notification subject or default
                    String subject = notificationMessage.getSubject();
                    if (subject == null || subject.isEmpty()) {
                        subject = emailProperties.getDefaultSubject();
                    }

                    NotificationMessageResolver.NotificationContent notificationContent = notificationMessageResolver.resolveMessage(notificationMessage, getChannel());
                    String content = notificationContent.getContent();
                    boolean isHtml = notificationContent.getIsHtml();

                    String attachmentUrl = notificationMessage.getAttachmentUrl();
                    File attachment = null;

                    if (attachmentUrl != null && !attachmentUrl.isEmpty()) {
                        attachment = new File(attachmentUrl);
                    }

                    log.info("Sending email to {} with subject: {}", notificationRecipient.getRecipientId(), subject);

                    sendEmail(fromAddress, recipient, subject, content, isHtml, attachment);
                }
            }

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