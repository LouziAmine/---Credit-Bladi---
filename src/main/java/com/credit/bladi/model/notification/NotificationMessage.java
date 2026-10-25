package com.credit.bladi.model.notification;

import com.credit.bladi.model.enums.Language;
import com.credit.bladi.model.enums.NotificationType;

public record NotificationMessage(
        String recipientUsername,
        String recipientEmail,
        String recipientPhone,
        Language recipientLanguage,
        NotificationType type,
        LocalizedText title,
        LocalizedText body,
        String link
) {
}
