package com.credit.bladi.unittest.service.notification.channel;

import static com.credit.bladi.unittest.mock.data.MockContacts.EMAIL_JDOE_AT_EXAMPLE_COM;
import static com.credit.bladi.unittest.mock.data.MockContacts.EMAIL_NO_REPLY_AT_BLADI_CREDIT_MA;
import static com.credit.bladi.unittest.mock.data.MockContacts.PHONE_0612345678;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_DOSSIER_FINALISE;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_VOTRE_DOSSIER_A_ETE_FINALISE;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_JDOE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.infrastructure.notification.EmailNotificationChannel;
import com.credit.bladi.model.enums.NotificationType;
import com.credit.bladi.model.notification.RenderedNotification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@ExtendWith(MockitoExtension.class)
class EmailNotificationChannelTest {
    private static final String FROM_ADDRESS = EMAIL_NO_REPLY_AT_BLADI_CREDIT_MA;

    @Mock
    private ObjectProvider<JavaMailSender> mailSenderProvider;

    @Mock
    private JavaMailSender mailSender;

    private EmailNotificationChannel newChannel(boolean enabled) {
        return new EmailNotificationChannel(mailSenderProvider, enabled, FROM_ADDRESS);
    }

    private RenderedNotification message(String email) {
        return new RenderedNotification(USER_JDOE, email, PHONE_0612345678, NotificationType.DEMAND_COMPLETED,
            LABEL_DOSSIER_FINALISE, LABEL_VOTRE_DOSSIER_A_ETE_FINALISE, "/portal");
    }

    @Test
    void shouldSkipWhenRecipientHasNoEmailOnFile() {
        EmailNotificationChannel channel = newChannel(true);

        channel.send(message(null));
        channel.send(message("  "));

        verify(mailSenderProvider, never()).getIfAvailable();
    }

    @Test
    void shouldNotSendWhenChannelIsDisabled() {
        EmailNotificationChannel channel = newChannel(false);

        channel.send(message(EMAIL_JDOE_AT_EXAMPLE_COM));

        verify(mailSenderProvider, never()).getIfAvailable();
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void shouldSkipWhenEnabledButNoMailSenderConfigured() {
        EmailNotificationChannel channel = newChannel(true);
        when(mailSenderProvider.getIfAvailable()).thenReturn(null);

        channel.send(message(EMAIL_JDOE_AT_EXAMPLE_COM));

        verify(mailSenderProvider).getIfAvailable();
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void shouldSendMailWithExpectedFieldsWhenEnabledAndSenderAvailable() {
        EmailNotificationChannel channel = newChannel(true);
        when(mailSenderProvider.getIfAvailable()).thenReturn(mailSender);

        channel.send(message(EMAIL_JDOE_AT_EXAMPLE_COM));

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());
        SimpleMailMessage sent = captor.getValue();
        assertEquals(FROM_ADDRESS, sent.getFrom());
        assertEquals(EMAIL_JDOE_AT_EXAMPLE_COM, sent.getTo()[0]);
        assertEquals(LABEL_DOSSIER_FINALISE, sent.getSubject());
        assertEquals(LABEL_VOTRE_DOSSIER_A_ETE_FINALISE, sent.getText());
    }

    @Test
    void shouldSwallowExceptionWhenMailSenderThrows() {
        EmailNotificationChannel channel = newChannel(true);
        when(mailSenderProvider.getIfAvailable()).thenReturn(mailSender);
        doThrow(new MailSendException("boom")).when(mailSender).send(any(SimpleMailMessage.class));

        channel.send(message(EMAIL_JDOE_AT_EXAMPLE_COM));

        verify(mailSender).send(any(SimpleMailMessage.class));
    }
}
