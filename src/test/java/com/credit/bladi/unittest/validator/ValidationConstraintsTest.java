package com.credit.bladi.unittest.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.credit.bladi.validator.ValidationConstraints;
import java.lang.reflect.Constructor;
import org.junit.jupiter.api.Test;

class ValidationConstraintsTest {

    @Test
    void shouldHaveCorrectAmountMin() {
        assertEquals("70000", ValidationConstraints.AMOUNT_MIN);
    }

    @Test
    void shouldHaveCorrectAmountMax() {
        assertEquals("30000000", ValidationConstraints.AMOUNT_MAX);
    }

    @Test
    void shouldHaveCorrectDurationMin() {
        assertEquals(12, ValidationConstraints.DURATION_MIN);
    }

    @Test
    void shouldHaveCorrectDurationMax() {
        assertEquals(300, ValidationConstraints.DURATION_MAX);
    }

    @Test
    void shouldHaveCorrectCspCodeRegex() {
        assertNotNull(ValidationConstraints.CSP_CODE_REGEX);
        assertEquals("^(RETR|SALA|FONC|PROF|INDE)$", ValidationConstraints.CSP_CODE_REGEX);
    }

    @Test
    void shouldHaveCorrectNationalityCodeRegex() {
        assertNotNull(ValidationConstraints.NATIONALITY_CODE_REGEX);
        assertEquals("^[A-Z]{2}$", ValidationConstraints.NATIONALITY_CODE_REGEX);
    }

    @Test
    void shouldCoverPrivateConstructor() throws Exception {
        Constructor<ValidationConstraints> constructor =
            ValidationConstraints.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        assertNotNull(constructor.newInstance());
    }
}