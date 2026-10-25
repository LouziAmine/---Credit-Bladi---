package com.credit.bladi.model.enterprise.event;

import com.credit.bladi.model.enums.AuditAction;
import com.credit.bladi.model.notification.NotificationMessage;
import java.util.List;

public record EnterpriseLinkStatusChangedEvent(
        String actor,
        AuditAction auditAction,
        String auditDetails,
        List<NotificationMessage> notifications) {
}
