package com.credit.bladi.unittest.service.notification.channel;

import static com.credit.bladi.unittest.mock.data.MockContacts.EMAIL_JDOE_AT_EXAMPLE_COM;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_DOSSIER_FINALISE;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_VOTRE_DOSSIER_A_ETE_FINALISE;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_JDOE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.credit.bladi.infrastructure.notification.SseEmitterRegistry;
import com.credit.bladi.infrastructure.notification.SseNotificationChannel;
import com.credit.bladi.model.enums.NotificationType;
import com.credit.bladi.model.notification.RenderedNotification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SseNotificationChannelTest {
    @Mock
    private SseEmitterRegistry registry;

    @Test
    void shouldPushToRegistryWhenRecipientUsernameIsPresent() {
        SseNotificationChannel channel = new SseNotificationChannel(registry);

        channel.send(message(USER_JDOE));

        verify(registry).send(USER_JDOE, "notification");
    }

    @Test
    void shouldSkipWhenNoLinkedAccount() {
        SseNotificationChannel channel = new SseNotificationChannel(registry);

        channel.send(message(null));
        channel.send(message("  "));

        verify(registry, never()).send(any(), any());
    }

    private RenderedNotification message(String username) {
        return new RenderedNotification(username, EMAIL_JDOE_AT_EXAMPLE_COM, null, NotificationType.DEMAND_COMPLETED,
            LABEL_DOSSIER_FINALISE, LABEL_VOTRE_DOSSIER_A_ETE_FINALISE, "/portal");
    }
}
