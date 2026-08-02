package com.credit.bladi.unittest.model.vo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.model.vo.Rate;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class RateTest {

    @Test
    void shouldCreateWithPositiveValue() {
        Rate rate = Rate.of(new BigDecimal("5.25"));
        assertEquals(0, rate.value().compareTo(new BigDecimal("5.25")));
    }

    @Test
    void shouldRejectZeroValue() {
        assertThrows(IllegalArgumentException.class, () -> Rate.of(BigDecimal.ZERO));
    }

    @Test
    void shouldRejectNegativeValue() {
        assertThrows(IllegalArgumentException.class, () -> Rate.of(new BigDecimal("-1")));
    }

    @Test
    void shouldRejectValueAboveMax() {
        assertThrows(IllegalArgumentException.class, () -> Rate.of(new BigDecimal("100.01")));
    }

    @Test
    void shouldAcceptValueAtMax() {
        Rate rate = Rate.of(new BigDecimal("100"));
        assertEquals(0, rate.value().compareTo(new BigDecimal("100")));
    }

    @Test
    void shouldRejectNullValue() {
        assertThrows(NullPointerException.class, () -> new Rate(null));
    }

    @Test
    void shouldAverageTwoRates() {
        Rate result = Rate.of(new BigDecimal("3.50")).average(Rate.of(new BigDecimal("5.50")));
        assertEquals(0, result.value().compareTo(new BigDecimal("4.50")));
    }

    @Test
    void shouldBeGreaterThan() {
        assertTrue(Rate.of(new BigDecimal("5.50")).isGreaterThan(Rate.of(new BigDecimal("3.50"))));
        assertFalse(Rate.of(new BigDecimal("3.50")).isGreaterThan(Rate.of(new BigDecimal("5.50"))));
    }

    @Test
    void shouldBeLessThan() {
        assertTrue(Rate.of(new BigDecimal("3.50")).isLessThan(Rate.of(new BigDecimal("5.50"))));
        assertFalse(Rate.of(new BigDecimal("5.50")).isLessThan(Rate.of(new BigDecimal("3.50"))));
    }

    @Test
    void shouldBeEqualValueWhenSameValue() {
        assertTrue(Rate.of(new BigDecimal("5.25")).equalsValue(Rate.of(new BigDecimal("5.250"))));
    }

    @Test
    void shouldNotBeEqualValueWhenOtherIsNull() {
        assertFalse(Rate.of(new BigDecimal("5.25")).equalsValue(null));
    }

    @Test
    void shouldNotBeEqualValueWhenDifferentValue() {
        assertFalse(Rate.of(new BigDecimal("5.25")).equalsValue(Rate.of(new BigDecimal("6.00"))));
    }

    @Test
    void shouldHaveSameValueHashCodeForScaleDifferentValues() {
        assertEquals(
            Rate.of(new BigDecimal("5.25")).valueHashCode(),
            Rate.of(new BigDecimal("5.250")).valueHashCode());
    }

    @Test
    void shouldToStringWithPercentSuffix() {
        assertEquals("5.25%", Rate.of(new BigDecimal("5.25")).toString());
    }
}