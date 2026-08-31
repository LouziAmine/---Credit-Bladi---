package com.credit.bladi.unittest.service.notification;

import static com.credit.bladi.unittest.mock.data.MockContacts.EMAIL_JDOE_AT_EXAMPLE_COM;
import static com.credit.bladi.unittest.mock.data.MockContacts.PHONE_0612345678;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_CASE_ASSIGNED;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_DEM_1;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_DOSSIER_AFFECTE;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_JDOE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.exception.NotificationException;
import com.credit.bladi.exception.NotificationException.NotificationExceptionType;
import com.credit.bladi.model.enums.Language;
import com.credit.bladi.model.enums.NotificationType;
import com.credit.bladi.model.notification.LocalizedText;
import com.credit.bladi.model.notification.NotificationMessage;
import com.credit.bladi.model.notification.NotificationModel;
import com.credit.bladi.model.notification.RenderedNotification;
import com.credit.bladi.repository.notification.INotificationRepository;
import com.credit.bladi.service.notification.NotificationChannel;
import com.credit.bladi.service.notification.NotificationService;
import com.credit.bladi.service.notification.NotificationTexts;
import com.credit.bladi.service.security.ICurrentActorProvider;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.support.StaticMessageSource;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {
    @Mock
    private NotificationChannel emailChannel;

    @Mock
    private NotificationChannel smsChannel;

    @Mock
    private NotificationChannel inAppChannel;

    @Mock
    private INotificationRepository notificationRepository;

    @Mock
    private ICurrentActorProvider currentActorProvider;

    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        StaticMessageSource messageSource = new StaticMessageSource();
        messageSource.addMessage("notification.assigned.title", Locale.FRENCH, LABEL_DOSSIER_AFFECTE);
        messageSource.addMessage("notification.assigned.body", Locale.FRENCH, "Le dossier {0} vous a été affecté.");
        messageSource.addMessage("notification.assigned.title", Locale.ENGLISH, LABEL_CASE_ASSIGNED);
        messageSource.addMessage("notification.assigned.body", Locale.ENGLISH, "Case {0} was assigned to you.");
        notificationService = new NotificationService(List.of(emailChannel, smsChannel, inAppChannel),
            notificationRepository, currentActorProvider, new NotificationTexts(messageSource, Locale.FRENCH));
    }

    private NotificationMessage message(Language language) {
        return new NotificationMessage(USER_JDOE, EMAIL_JDOE_AT_EXAMPLE_COM, PHONE_0612345678, language,
            NotificationType.DEMAND_ASSIGNED, LocalizedText.of("notification.assigned.title"),
            LocalizedText.of("notification.assigned.body", LABEL_DEM_1), "/portal");
    }

    @Test
    void shouldRenderInRecipientLanguageAndFanOutToEveryRegisteredChannel() {
        notificationService.notify(message(Language.EN));

        RenderedNotification expected = new RenderedNotification(USER_JDOE, EMAIL_JDOE_AT_EXAMPLE_COM, PHONE_0612345678,
            NotificationType.DEMAND_ASSIGNED, LABEL_CASE_ASSIGNED, "Case DEM-1 was assigned to you.", "/portal");
        verify(emailChannel).send(expected);
        verify(smsChannel).send(expected);
        verify(inAppChannel).send(expected);
    }

    @Test
    void shouldRenderInDefaultLanguageWhenRecipientLanguageUnknown() {
        notificationService.notify(message(null));

        verify(emailChannel).send(new RenderedNotification(USER_JDOE, EMAIL_JDOE_AT_EXAMPLE_COM, PHONE_0612345678,
            NotificationType.DEMAND_ASSIGNED, LABEL_DOSSIER_AFFECTE, "Le dossier DEM-1 vous a été affecté.",
            "/portal"));
    }

    @Test
    void shouldDelegateGetMyNotificationsToRepositoryForCurrentUser() {
        CreditPage<NotificationModel> page = new CreditPage<>();
        page.setItems(List.of(new NotificationModel(1L, UUID.randomUUID(), NotificationType.DEMAND_ASSIGNED,
            LABEL_DOSSIER_AFFECTE, "body", "/portal", false, Instant.now())));
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_JDOE);
        when(notificationRepository.findByUsername(USER_JDOE, 1, 20)).thenReturn(page);

        CreditPage<NotificationModel> result = notificationService.getMyNotifications(1, 20);

        assertEquals(page, result);
        verify(notificationRepository).findByUsername(USER_JDOE, 1, 20);
    }

    @Test
    void shouldDelegateGetMyUnreadCountToRepositoryForCurrentUser() {
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_JDOE);
        when(notificationRepository.countUnread(USER_JDOE)).thenReturn(3L);

        long result = notificationService.getMyUnreadCount();

        assertEquals(3L, result);
    }

    @Test
    void shouldResolveNotificationIdWhenPublicIdExists() {
        UUID publicId = UUID.randomUUID();
        when(notificationRepository.findIdByPublicId(publicId)).thenReturn(Optional.of(42L));

        Long result = notificationService.resolveNotificationId(publicId);

        assertEquals(42L, result);
    }

    @Test
    void shouldThrowNotificationExceptionWhenPublicIdDoesNotResolve() {
        UUID publicId = UUID.randomUUID();
        when(notificationRepository.findIdByPublicId(publicId)).thenReturn(Optional.empty());

        NotificationException ex = assertThrows(NotificationException.class,
            () -> notificationService.resolveNotificationId(publicId));

        assertEquals(NotificationExceptionType.NOTIFICATION_NOT_FOUND, ex.getType());
    }

    @Test
    void shouldMarkAsReadForCurrentUser() {
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_JDOE);

        notificationService.markAsRead(42L);

        verify(notificationRepository).markAsRead(42L, USER_JDOE);
    }

    @Test
    void shouldMarkAllAsReadForCurrentUser() {
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_JDOE);

        notificationService.markAllAsRead();

        verify(notificationRepository).markAllAsRead(USER_JDOE);
        verify(notificationRepository, never()).markAsRead(anyLong(), anyString());
    }
}
