package com.credit.bladi.service.notification.channel;

import com.credit.bladi.model.notification.RenderedNotification;
import com.credit.bladi.repository.notification.INotificationRepository;
import com.credit.bladi.service.notification.NotificationChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class InAppNotificationChannel implements NotificationChannel {
    private static final Logger LOGGER = LogManager.getLogger(InAppNotificationChannel.class);

    private final INotificationRepository notificationRepository;

    public InAppNotificationChannel(INotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void send(RenderedNotification message) {
        if (message.recipientUsername() == null || message.recipientUsername().isBlank()) {
            LOGGER.debug("IN_APP_NOTIFICATION_SKIPPED reason=no-linked-account type={}", message.type());
            return;
        }
        try {
            notificationRepository.save(message);
        } catch (RuntimeException e) {
            LOGGER.error("IN_APP_NOTIFICATION_FAILURE recipient={} type={}", message.recipientUsername(),
                message.type(), e);
        }
    }
}
