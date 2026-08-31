package com.credit.bladi.infrastructure.notification;

import static com.credit.bladi.common.PiiMasker.maskEmail;

import com.credit.bladi.model.notification.RenderedNotification;
import com.credit.bladi.service.notification.NotificationChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailNotificationChannel implements NotificationChannel {
    private static final Logger LOGGER = LogManager.getLogger(EmailNotificationChannel.class);

    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    private final boolean enabled;

    private final String fromAddress;

    public EmailNotificationChannel(ObjectProvider<JavaMailSender> mailSenderProvider,
            @Value("${notification.email.enabled:false}") boolean enabled,
            @Value("${notification.email.from:no-reply@bladi-credit.ma}") String fromAddress) {
        this.mailSenderProvider = mailSenderProvider;
        this.enabled = enabled;
        this.fromAddress = fromAddress;
    }

    @Override
    public void send(RenderedNotification message) {
        if (message.recipientEmail() == null || message.recipientEmail().isBlank()) {
            LOGGER.debug("EMAIL_NOTIFICATION_SKIPPED recipient={} reason=no-email-on-file",
                message.recipientUsername());
            return;
        }
        if (!enabled) {
            LOGGER.info("EMAIL_NOTIFICATION_WOULD_SEND to={} subject={}", maskEmail(message.recipientEmail()),
                message.title());
            return;
        }
        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            LOGGER.warn("EMAIL_NOTIFICATION_FAILURE to={} reason=no-mail-sender-configured",
                maskEmail(message.recipientEmail()));
            return;
        }
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setFrom(fromAddress);
            mail.setTo(message.recipientEmail());
            mail.setSubject(message.title());
            mail.setText(message.body());
            mailSender.send(mail);
        } catch (MailException e) {
            LOGGER.error("EMAIL_NOTIFICATION_FAILURE to={}", maskEmail(message.recipientEmail()), e);
        }
    }
}
