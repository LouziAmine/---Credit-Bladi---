package com.credit.bladi.model.notification;

import com.credit.bladi.model.enums.NotificationType;
import java.time.Instant;
import java.util.UUID;

public record NotificationModel(
        Long id,
        UUID publicId,
        NotificationType type,
        String title,
        String body,
        String link,
        boolean read,
        Instant createdAt
) {
}
