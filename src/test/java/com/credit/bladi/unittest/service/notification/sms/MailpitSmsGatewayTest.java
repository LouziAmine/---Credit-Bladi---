package com.credit.bladi.unittest.service.notification.sms;

import static com.credit.bladi.unittest.mock.data.MockContacts.EMAIL_NO_REPLY_AT_BLADI_CREDIT_MA;
import static com.credit.bladi.unittest.mock.data.MockContacts.EMAIL_SMS_CATCHER_AT_BLADI_CREDIT_MA;
import static com.credit.bladi.unittest.mock.data.MockContacts.PHONE_0612345678;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.infrastructure.sms.MailpitSmsGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@ExtendWith(MockitoExtension.class)
class MailpitSmsGatewayTest {
    private static final String FROM = EMAIL_NO_REPLY_AT_BLADI_CREDIT_MA;

    private static final String CATCHER = EMAIL_SMS_CATCHER_AT_BLADI_CREDIT_MA;

    @Mock
    private ObjectProvider<JavaMailSender> mailSenderProvider;

    @Mock
    private JavaMailSender mailSender;

    @Test
    void shouldLogAndSkipWhenNoMailSenderConfigured() {
        when(mailSenderProvider.getIfAvailable()).thenReturn(null);
        MailpitSmsGateway gateway = new MailpitSmsGateway(mailSenderProvider, FROM, CATCHER);

        gateway.send(PHONE_0612345678, "text");

        verify(mailSenderProvider).getIfAvailable();
    }

    @Test
    void shouldRouteThroughTheFakeCatcherAddress() {
        when(mailSenderProvider.getIfAvailable()).thenReturn(mailSender);
        MailpitSmsGateway gateway = new MailpitSmsGateway(mailSenderProvider, FROM, CATCHER);

        gateway.send(PHONE_0612345678, "text");

        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void shouldSwallowMailSenderFailure() {
        when(mailSenderProvider.getIfAvailable()).thenReturn(mailSender);
        doThrow(new MailSendException("boom")).when(mailSender).send(any(SimpleMailMessage.class));
        MailpitSmsGateway gateway = new MailpitSmsGateway(mailSenderProvider, FROM, CATCHER);

        gateway.send(PHONE_0612345678, "text");
    }
}
