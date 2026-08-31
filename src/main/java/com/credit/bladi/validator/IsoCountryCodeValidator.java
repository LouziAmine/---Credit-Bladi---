package com.credit.bladi.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

public class IsoCountryCodeValidator implements ConstraintValidator<IsoCountryCode, String> {
    private static final Set<String> ISO_COUNTRIES = Set.of(Locale.getISOCountries());

    private static final Pattern TWO_UPPERCASE_LETTERS = Pattern.compile("^[A-Z]{2}$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || !TWO_UPPERCASE_LETTERS.matcher(value).matches()) {
            return true;
        }
        return ISO_COUNTRIES.contains(value);
    }
}
