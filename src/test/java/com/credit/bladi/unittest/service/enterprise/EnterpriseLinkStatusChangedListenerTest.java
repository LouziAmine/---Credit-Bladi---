package com.credit.bladi.unittest.service.enterprise;

import static com.credit.bladi.unittest.mock.data.MockUsers.USER_JDUPONT;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_MANAGER1;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_MANAGER2;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_MDUPONT;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.credit.bladi.model.enterprise.event.EnterpriseLinkStatusChangedEvent;
import com.credit.bladi.model.enums.AuditAction;
import com.credit.bladi.model.enums.Language;
import com.credit.bladi.model.enums.NotificationType;
import com.credit.bladi.model.notification.LocalizedText;
import com.credit.bladi.model.notification.NotificationMessage;
import com.credit.bladi.service.audit.IAuditService;
import com.credit.bladi.service.enterprise.EnterpriseLinkStatusChangedListener;
import com.credit.bladi.service.notification.INotificationService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EnterpriseLinkStatusChangedListenerTest {
    @Mock
    private IAuditService auditService;

    @Mock
    private INotificationService notificationService;

    private EnterpriseLinkStatusChangedListener listener;

    @BeforeEach
    void setUp() {
        listener = new EnterpriseLinkStatusChangedListener(auditService, notificationService, Runnable::run);
    }

    private static NotificationMessage message(String username) {
        return new NotificationMessage(username, username + "@test.ma", null, Language.FR,
            NotificationType.ENTERPRISE_LINK_APPROVED, LocalizedText.of("title"), LocalizedText.of("body"),
                "/portfolio");
    }

    @Test
    void logsAuditEntryAndSendsEachNotification() {
        EnterpriseLinkStatusChangedEvent event = new EnterpriseLinkStatusChangedEvent(USER_MANAGER1,
            AuditAction.ENTERPRISE_USER_LINK_APPROVED, "linkId=10", List.of(message(USER_JDUPONT),
            message(USER_MDUPONT)));

        listener.onStatusChanged(event);

        verify(auditService).log(USER_MANAGER1, AuditAction.ENTERPRISE_USER_LINK_APPROVED, "linkId=10");
        verify(notificationService, times(2)).notify(any());
    }

    @Test
    void stillSendsNotificationsWhenAuditLoggingFails() {
        doThrow(new RuntimeException("boom")).when(auditService)
            .log(any(), any(), any());
        EnterpriseLinkStatusChangedEvent event = new EnterpriseLinkStatusChangedEvent(USER_MANAGER1,
            AuditAction.ENTERPRISE_USER_LINK_REVOKED, "linkId=10", List.of(message(USER_JDUPONT)));

        listener.onStatusChanged(event);

        verify(notificationService).notify(eq(message(USER_JDUPONT)));
    }

    @Test
    void stillLogsAuditEntryWhenNotificationDeliveryFails() {
        doThrow(new RuntimeException("boom")).when(notificationService).notify(any());
        EnterpriseLinkStatusChangedEvent event = new EnterpriseLinkStatusChangedEvent(USER_MANAGER1,
            AuditAction.ENTERPRISE_USER_LINK_REJECTED, "linkId=10", List.of(message(USER_JDUPONT)));

        listener.onStatusChanged(event);

        verify(auditService).log(USER_MANAGER1, AuditAction.ENTERPRISE_USER_LINK_REJECTED, "linkId=10");
    }

    @Test
    void deliversNotificationsThroughTheExecutorWithoutBlockingTheCaller() {
        List<Runnable> queued = new ArrayList<>();
        EnterpriseLinkStatusChangedListener deferred =
            new EnterpriseLinkStatusChangedListener(auditService, notificationService, queued::add);
        EnterpriseLinkStatusChangedEvent event = new EnterpriseLinkStatusChangedEvent(USER_JDUPONT,
            AuditAction.ENTERPRISE_USER_LINK_CLAIMED, "linkId=10", List.of(message(USER_MANAGER1),
            message(USER_MANAGER2)));

        deferred.onStatusChanged(event);

        verify(auditService).log(USER_JDUPONT, AuditAction.ENTERPRISE_USER_LINK_CLAIMED, "linkId=10");
        verify(notificationService, never()).notify(any());
        queued.forEach(Runnable::run);
        verify(notificationService, times(2)).notify(any());
    }
}
