package com.credit.bladi.unittest.service.notification.channel;

import static com.credit.bladi.unittest.mock.data.MockContacts.EMAIL_JDOE_AT_EXAMPLE_COM;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_DOSSIER_FINALISE;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_VOTRE_DOSSIER_A_ETE_FINALISE;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_JDOE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.credit.bladi.model.enums.NotificationType;
import com.credit.bladi.model.notification.RenderedNotification;
import com.credit.bladi.repository.notification.INotificationRepository;
import com.credit.bladi.service.notification.channel.InAppNotificationChannel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InAppNotificationChannelTest {
    @Mock
    private INotificationRepository notificationRepository;

    @Test
    void shouldSaveWhenRecipientUsernameIsPresent() {
        InAppNotificationChannel channel = new InAppNotificationChannel(notificationRepository);
        RenderedNotification message = message(USER_JDOE);

        channel.send(message);

        verify(notificationRepository).save(message);
    }

    @Test
    void shouldSkipWhenNoLinkedAccount() {
        InAppNotificationChannel channel = new InAppNotificationChannel(notificationRepository);

        channel.send(message(null));
        channel.send(message("  "));

        verify(notificationRepository, never()).save(any());
    }

    private RenderedNotification message(String username) {
        return new RenderedNotification(username, EMAIL_JDOE_AT_EXAMPLE_COM, null, NotificationType.DEMAND_COMPLETED,
            LABEL_DOSSIER_FINALISE, LABEL_VOTRE_DOSSIER_A_ETE_FINALISE, "/portal");
    }
}
