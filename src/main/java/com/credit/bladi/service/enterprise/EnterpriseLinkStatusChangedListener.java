package com.credit.bladi.service.enterprise;

import com.credit.bladi.model.enterprise.event.EnterpriseLinkStatusChangedEvent;
import com.credit.bladi.model.notification.NotificationMessage;
import com.credit.bladi.service.audit.IAuditService;
import com.credit.bladi.service.notification.INotificationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class EnterpriseLinkStatusChangedListener {
    private static final Logger LOGGER = LogManager.getLogger(EnterpriseLinkStatusChangedListener.class);

    private final IAuditService auditService;

    private final INotificationService notificationService;

    private final TaskExecutor notificationExecutor;

    public EnterpriseLinkStatusChangedListener(IAuditService auditService, INotificationService notificationService,
            @Qualifier("notificationTaskExecutor") TaskExecutor notificationExecutor) {
        this.auditService = auditService;
        this.notificationService = notificationService;
        this.notificationExecutor = notificationExecutor;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onStatusChanged(EnterpriseLinkStatusChangedEvent event) {
        try {
            auditService.log(event.actor(), event.auditAction(), event.auditDetails());
        } catch (RuntimeException e) {
            LOGGER.error("ENTERPRISE_LINK_AUDIT_FAILED action={}, actor={}", event.auditAction(), event.actor(), e);
        }
        for (NotificationMessage notification : event.notifications()) {
            notificationExecutor.execute(() -> {
                try {
                    notificationService.notify(notification);
                } catch (RuntimeException e) {
                    LOGGER.error("ENTERPRISE_LINK_NOTIFY_FAILED action={}, recipient={}", event.auditAction(),
                        notification.recipientUsername(), e);
                }
            });
        }
    }
}
