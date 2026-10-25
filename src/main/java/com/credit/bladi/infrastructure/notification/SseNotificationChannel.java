package com.credit.bladi.infrastructure.notification;

import com.credit.bladi.model.notification.RenderedNotification;
import com.credit.bladi.service.notification.NotificationChannel;
import org.springframework.stereotype.Component;

@Component
public class SseNotificationChannel implements NotificationChannel {
    private final SseEmitterRegistry registry;

    public SseNotificationChannel(SseEmitterRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void send(RenderedNotification message) {
        if (message.recipientUsername() == null || message.recipientUsername().isBlank()) {
            return;
        }
        registry.send(message.recipientUsername(), "notification");
    }
}
