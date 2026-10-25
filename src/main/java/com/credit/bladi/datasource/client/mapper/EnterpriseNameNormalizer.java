package com.credit.bladi.datasource.client.mapper;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

public final class EnterpriseNameNormalizer {
    private static final Set<String> LEGAL_SUFFIXES = Set.of(
        "SARLAU", "SARL AU", "SARL", "SA", "SAS", "SASU", "EURL", "SNC", "GIE", "ETS");

    private static final Pattern DIACRITICS = Pattern.compile("\\p{M}+");

    private static final Pattern NON_ALPHANUMERIC = Pattern.compile("[^A-Z0-9]+");

    private static final Pattern WHITESPACE = Pattern.compile("\\s+");

    private EnterpriseNameNormalizer() {
    }

    public static String normalize(String rawName) {
        if (rawName == null || rawName.isBlank()) {
            return "";
        }

        String withoutAccents = DIACRITICS.matcher(Normalizer.normalize(rawName, Normalizer.Form.NFD)).replaceAll("");
        String cleaned = WHITESPACE.matcher(
            NON_ALPHANUMERIC.matcher(withoutAccents.toUpperCase(Locale.ROOT)).replaceAll(" ")).replaceAll(" ").trim();

        String previous;
        do {
            previous = cleaned;
            for (String suffix : LEGAL_SUFFIXES) {
                if (cleaned.endsWith(" " + suffix)) {
                    cleaned = cleaned.substring(0, cleaned.length() - suffix.length() - 1).trim();
                    break;
                }
            }
        } while (!cleaned.equals(previous));

        return cleaned;
    }
}
