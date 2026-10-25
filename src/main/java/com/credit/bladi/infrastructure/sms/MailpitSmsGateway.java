package com.credit.bladi.infrastructure.sms;

import static com.credit.bladi.common.PiiMasker.maskPhone;

import com.credit.bladi.service.notification.sms.ISmsGateway;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "sms.gateway", name = "provider", havingValue = "mailpit", matchIfMissing = true)
public class MailpitSmsGateway implements ISmsGateway {
    private static final Logger LOGGER = LogManager.getLogger(MailpitSmsGateway.class);

    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    private final String fromAddress;

    private final String fakeCatcherAddress;

    public MailpitSmsGateway(ObjectProvider<JavaMailSender> mailSenderProvider,
            @Value("${notification.email.from:no-reply@bladi-credit.ma}") String fromAddress,
            @Value("${notification.sms.fakeCatcherAddress:sms-catcher@bladi-credit.ma}") String fakeCatcherAddress) {
        this.mailSenderProvider = mailSenderProvider;
        this.fromAddress = fromAddress;
        this.fakeCatcherAddress = fakeCatcherAddress;
    }

    @Override
    public void send(String phoneNumber, String text) {
        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            LOGGER.warn("SMS_GATEWAY_FAILURE provider=mailpit to={} reason=no-mail-sender-configured",
                maskPhone(phoneNumber));
            return;
        }
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setFrom(fromAddress);
            mail.setTo(fakeCatcherAddress);
            mail.setSubject("[SMS] to=" + phoneNumber);
            mail.setText(text);
            mailSender.send(mail);
        } catch (MailException e) {
            LOGGER.error("SMS_GATEWAY_FAILURE provider=mailpit to={}", maskPhone(phoneNumber), e);
        }
    }
}
