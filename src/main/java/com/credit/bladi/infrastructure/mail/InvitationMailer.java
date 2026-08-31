package com.credit.bladi.infrastructure.mail;

import static com.credit.bladi.common.PiiMasker.maskEmail;

import com.credit.bladi.model.enums.Language;
import com.credit.bladi.model.notification.LocalizedText;
import com.credit.bladi.service.invitation.IInvitationMailer;
import com.credit.bladi.service.notification.NotificationTexts;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class InvitationMailer implements IInvitationMailer {
    private static final Logger LOGGER = LogManager.getLogger(InvitationMailer.class);

    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    private final boolean enabled;

    private final String fromAddress;

    private final String frontendBaseUrl;

    private final NotificationTexts texts;

    public InvitationMailer(ObjectProvider<JavaMailSender> mailSenderProvider,
            @Value("${notification.email.enabled:false}") boolean enabled,
            @Value("${notification.email.from:no-reply@bladi-credit.ma}") String fromAddress,
            @Value("${security.cors.allowed-origin}") String frontendBaseUrl, NotificationTexts texts) {
        this.mailSenderProvider = mailSenderProvider;
        this.enabled = enabled;
        this.fromAddress = fromAddress;
        this.frontendBaseUrl = frontendBaseUrl;
        this.texts = texts;
    }

    @Override
    public void sendInvitation(String toEmail, String enterpriseName, String rawToken, Language language) {
        String link = frontendBaseUrl + "/invitations/" + rawToken;
        String subject = texts.render(LocalizedText.of("invitation.subject", enterpriseName), language);
        String body = texts.render(LocalizedText.of("invitation.body", enterpriseName, link), language);
        send(toEmail, subject, body);
    }

    @Override
    public void sendAccountConfirmation(String toEmail, String username, String enterpriseName,
            Language language) {
        String link = frontendBaseUrl + "/login";
        String subject = texts.render(LocalizedText.of("accountConfirmation.subject"), language);
        String body = texts.render(LocalizedText.of("accountConfirmation.body", username, enterpriseName, link),
            language);
        send(toEmail, subject, body);
    }

    @Override
    public void sendBorrowerInvitation(String toEmail, String demandReference, String rawToken, Language language) {
        String link = frontendBaseUrl + "/invitations/" + rawToken;
        String subject = texts.render(LocalizedText.of("invitation.borrower.subject", demandReference), language);
        String body = texts.render(LocalizedText.of("invitation.borrower.body", demandReference, link), language);
        send(toEmail, subject, body);
    }

    @Override
    public void sendBorrowerAccountConfirmation(String toEmail, String username, String demandReference,
            Language language) {
        String link = frontendBaseUrl + "/login";
        String subject = texts.render(LocalizedText.of("accountConfirmation.subject"), language);
        String body = texts.render(LocalizedText.of("accountConfirmation.borrower.body", username, demandReference,
            link), language);
        send(toEmail, subject, body);
    }

    private void send(String toEmail, String subject, String body) {
        if (!enabled) {
            LOGGER.info("INVITATION_EMAIL_WOULD_SEND to={} subject={}", maskEmail(toEmail), subject);
            return;
        }
        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            LOGGER.warn("INVITATION_EMAIL_FAILURE to={} reason=no-mail-sender-configured", maskEmail(toEmail));
            return;
        }
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setFrom(fromAddress);
            mail.setTo(toEmail);
            mail.setSubject(subject);
            mail.setText(body);
            mailSender.send(mail);
        } catch (MailException e) {
            LOGGER.error("INVITATION_EMAIL_FAILURE to={}", maskEmail(toEmail), e);
        }
    }
}
