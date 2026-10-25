package com.credit.bladi.model.notification;

import com.credit.bladi.model.enums.NotificationType;

public record RenderedNotification(
        String recipientUsername,
        String recipientEmail,
        String recipientPhone,
        NotificationType type,
        String title,
        String body,
        String link
) {
}
