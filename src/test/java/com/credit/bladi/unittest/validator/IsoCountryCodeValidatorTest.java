package com.credit.bladi.unittest.validator;

import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_FR;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_FRANCE;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_MA;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_XX;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_ZZ;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.validator.IsoCountryCodeValidator;
import org.junit.jupiter.api.Test;

class IsoCountryCodeValidatorTest {
    private final IsoCountryCodeValidator validator = new IsoCountryCodeValidator();

    @Test
    void shouldAcceptRealIsoCountryCode() {
        assertTrue(validator.isValid(CODE_MA, null));
        assertTrue(validator.isValid(CODE_FR, null));
    }

    @Test
    void shouldRejectWellFormedButNonExistentCountryCode() {
        assertFalse(validator.isValid(CODE_ZZ, null));
        assertFalse(validator.isValid(CODE_XX, null));
    }

    @Test
    void shouldDeferToPatternForWrongShapeInsteadOfFailingItself() {
        assertTrue(validator.isValid("ma", null));
        assertTrue(validator.isValid(CODE_FRANCE, null));
        assertTrue(validator.isValid("M", null));
    }

    @Test
    void shouldAcceptNull() {
        assertTrue(validator.isValid(null, null));
    }
}
