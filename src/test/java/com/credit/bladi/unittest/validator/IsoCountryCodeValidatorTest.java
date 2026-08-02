package com.credit.bladi.unittest.validator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.validator.IsoCountryCodeValidator;
import org.junit.jupiter.api.Test;

class IsoCountryCodeValidatorTest {

    private final IsoCountryCodeValidator validator = new IsoCountryCodeValidator();

    @Test
    void shouldAcceptRealIsoCountryCode() {
        assertTrue(validator.isValid("MA", null));
        assertTrue(validator.isValid("FR", null));
    }

    @Test
    void shouldRejectWellFormedButNonExistentCountryCode() {
        assertFalse(validator.isValid("ZZ", null));
        assertFalse(validator.isValid("XX", null));
    }

    @Test
    void shouldDeferToPatternForWrongShapeInsteadOfFailingItself() {
        assertTrue(validator.isValid("ma", null));
        assertTrue(validator.isValid("FRANCE", null));
        assertTrue(validator.isValid("M", null));
    }

    @Test
    void shouldAcceptNull() {
        assertTrue(validator.isValid(null, null));
    }
}
