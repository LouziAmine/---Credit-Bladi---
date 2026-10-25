package com.credit.bladi.unittest.model;

import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_3_00;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_3_50;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_4_0000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_4_50;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_5_00;
import static com.credit.bladi.unittest.mock.data.MockAmounts.MINUS_DECIMAL_0_01;
import static com.credit.bladi.unittest.mock.data.MockAmounts.TEXT_4_50;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.model.vo.Rate;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class RateTest {
    @Test
    void shouldCreateRateWithOf() {
        Rate rate = Rate.of(DECIMAL_4_50);
        assertEquals(DECIMAL_4_50, rate.value());
    }

    @Test
    void shouldComputeAverageOfTwoRates() {
        Rate r1 = Rate.of(DECIMAL_3_00);
        Rate r2 = Rate.of(DECIMAL_5_00);

        Rate avg = r1.average(r2);

        assertEquals(0, avg.value().compareTo(DECIMAL_4_0000));
    }

    @Test
    void shouldThrowForZeroRate() {
        assertThrows(IllegalArgumentException.class,
            () -> Rate.of(BigDecimal.ZERO));
    }

    @Test
    void shouldThrowForNegativeRate() {
        assertThrows(IllegalArgumentException.class,
            () -> Rate.of(MINUS_DECIMAL_0_01));
    }

    @Test
    void shouldThrowForNullValue() {
        assertThrows(NullPointerException.class,
            () -> Rate.of(null));
    }

    @Test
    void shouldHaveToStringWithPercent() {
        Rate rate = Rate.of(DECIMAL_4_50);
        assertTrue(rate.toString().contains("%"));
        assertTrue(rate.toString().contains(TEXT_4_50));
    }

    @Test
    void shouldBeEqualWhenSameValue() {
        Rate r1 = Rate.of(DECIMAL_3_50);
        Rate r2 = Rate.of(DECIMAL_3_50);
        assertEquals(r1, r2);
    }
}
