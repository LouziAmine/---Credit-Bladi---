package com.credit.bladi.unittest.service.notification.channel;

import static com.credit.bladi.unittest.mock.data.MockContacts.EMAIL_JDOE_AT_EXAMPLE_COM;
import static com.credit.bladi.unittest.mock.data.MockContacts.PHONE_0612345678;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_JDOE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.credit.bladi.model.enums.NotificationType;
import com.credit.bladi.model.notification.RenderedNotification;
import com.credit.bladi.service.notification.channel.SmsNotificationChannel;
import com.credit.bladi.service.notification.sms.ISmsGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SmsNotificationChannelTest {
    @Mock
    private ISmsGateway smsGateway;

    @Test
    void shouldSkipWhenRecipientHasNoPhoneOnFile() {
        SmsNotificationChannel channel = new SmsNotificationChannel(smsGateway, true);

        channel.send(message(null));
        channel.send(message("  "));

        verify(smsGateway, never()).send(any(), any());
    }

    @Test
    void shouldSkipDelegatingToGatewayWhenDisabled() {
        SmsNotificationChannel channel = new SmsNotificationChannel(smsGateway, false);

        channel.send(message(PHONE_0612345678));

        verify(smsGateway, never()).send(any(), any());
    }

    @Test
    void shouldDelegateToGatewayWithFormattedTextWhenEnabled() {
        SmsNotificationChannel channel = new SmsNotificationChannel(smsGateway, true);

        channel.send(message(PHONE_0612345678));

        verify(smsGateway).send(eq(PHONE_0612345678),
            eq("Dossier en cours de décision: Votre dossier DEM-1 est en cours d'examen."));
    }

    private RenderedNotification message(String phone) {
        return new RenderedNotification(USER_JDOE, EMAIL_JDOE_AT_EXAMPLE_COM, phone,
            NotificationType.DEMAND_IN_DECISION,
            "Dossier en cours de décision", "Votre dossier DEM-1 est en cours d'examen.", "/portal");
    }
}
