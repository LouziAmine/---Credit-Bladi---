package com.credit.bladi.model.enums;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

public enum Language {
    FR(Locale.FRENCH),
    EN(Locale.ENGLISH),
    AR(Locale.of("ar"));

    private final Locale locale;

    Language(Locale locale) {
        this.locale = locale;
    }

    public Locale locale() {
        return locale;
    }

    public static Optional<Language> of(Locale locale) {
        if (locale == null) {
            return Optional.empty();
        }
        return Arrays.stream(values())
            .filter(language -> language.locale.getLanguage().equals(locale.getLanguage()))
            .findFirst();
    }
}
