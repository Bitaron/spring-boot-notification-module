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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

                    Set<String> attachmentUrls = notificationMessage.getAttachmentUrls();
                    List<File> attachments = new ArrayList<>();

                    if (attachmentUrls != null && !attachmentUrls.isEmpty()) {
                        for (String attachmentUrl : attachmentUrls) {
                            attachments.add(new File(attachmentUrl));
                        }
                    }

                    log.info("Sending email to {} with subject: {}", notificationRecipient.getRecipientId(), subject);

                    sendEmail(fromAddress, recipient, subject, content, isHtml, attachments);
                }
            }

        } catch (Exception e) {
            throw new DeliveryException("Failed to deliver email notification", e);
        }
    }

    private void sendEmail(String from, String to, String subject, String content, boolean isHtml, List<File> attachments)
            throws Exception {

        if (from == null || from.isEmpty()) {
            from = emailProperties.getFromAddress();
        }

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, !attachments.isEmpty());

        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);

        helper.setText(content, isHtml);

        if (!attachments.isEmpty()) {
            for (File attachment : attachments) {
                helper.addAttachment(attachment.getName(), attachment);
            }
        }

        mailSender.send(message);
    }

    @Override
    public boolean isSupported() {
        return emailProperties.isEnabled();
    }
} 