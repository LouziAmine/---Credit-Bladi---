package com.credit.bladi.service.notification;

import com.credit.bladi.model.enums.Language;
import com.credit.bladi.model.notification.LocalizedText;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
public class NotificationTexts {
    private final MessageSource messageSource;

    private final Locale defaultLocale;

    public NotificationTexts(MessageSource messageSource, @Value("${notification.locale:fr}") Locale defaultLocale) {
        this.messageSource = messageSource;
        this.defaultLocale = defaultLocale;
    }

    public String render(LocalizedText text, Language language) {
        return messageSource.getMessage(text.key(), text.args(), language != null ? language.locale() : defaultLocale);
    }
}
