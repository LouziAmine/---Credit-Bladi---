package com.credit.bladi.unittest.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.model.vo.Rate;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class RateTest {

    @Test
    void shouldCreateRateWithOf() {
        Rate rate = Rate.of(new BigDecimal("4.50"));
        assertEquals(new BigDecimal("4.50"), rate.value());
    }

    @Test
    void shouldComputeAverageOfTwoRates() {
        Rate r1 = Rate.of(new BigDecimal("3.00"));
        Rate r2 = Rate.of(new BigDecimal("5.00"));

        Rate avg = r1.average(r2);

        assertEquals(0, avg.value().compareTo(new BigDecimal("4.0000")));
    }

    @Test
    void shouldThrowForZeroRate() {
        assertThrows(IllegalArgumentException.class,
            () -> Rate.of(BigDecimal.ZERO));
    }

    @Test
    void shouldThrowForNegativeRate() {
        assertThrows(IllegalArgumentException.class,
            () -> Rate.of(new BigDecimal("-0.01")));
    }

    @Test
    void shouldThrowForNullValue() {
        assertThrows(NullPointerException.class,
            () -> Rate.of(null));
    }

    @Test
    void shouldHaveToStringWithPercent() {
        Rate rate = Rate.of(new BigDecimal("4.50"));
        assertTrue(rate.toString().contains("%"));
        assertTrue(rate.toString().contains("4.50"));
    }

    @Test
    void shouldBeEqualWhenSameValue() {
        Rate r1 = Rate.of(new BigDecimal("3.50"));
        Rate r2 = Rate.of(new BigDecimal("3.50"));
        assertEquals(r1, r2);
    }
}