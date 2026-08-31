package com.credit.bladi.unittest.model.vo;

import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_100;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_100_01;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_3_50;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_4_50;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_5_25;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_5_250;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_5_50;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_6_00;
import static com.credit.bladi.unittest.mock.data.MockAmounts.MINUS_AMOUNT_1;
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
        Rate rate = Rate.of(DECIMAL_5_25);
        assertEquals(0, rate.value().compareTo(DECIMAL_5_25));
    }

    @Test
    void shouldRejectZeroValue() {
        assertThrows(IllegalArgumentException.class, () -> Rate.of(BigDecimal.ZERO));
    }

    @Test
    void shouldRejectNegativeValue() {
        assertThrows(IllegalArgumentException.class, () -> Rate.of(MINUS_AMOUNT_1));
    }

    @Test
    void shouldRejectValueAboveMax() {
        assertThrows(IllegalArgumentException.class, () -> Rate.of(DECIMAL_100_01));
    }

    @Test
    void shouldAcceptValueAtMax() {
        Rate rate = Rate.of(AMOUNT_100);
        assertEquals(0, rate.value().compareTo(AMOUNT_100));
    }

    @Test
    void shouldRejectNullValue() {
        assertThrows(NullPointerException.class, () -> new Rate(null));
    }

    @Test
    void shouldAverageTwoRates() {
        Rate result = Rate.of(DECIMAL_3_50).average(Rate.of(DECIMAL_5_50));
        assertEquals(0, result.value().compareTo(DECIMAL_4_50));
    }

    @Test
    void shouldBeGreaterThan() {
        assertTrue(Rate.of(DECIMAL_5_50).isGreaterThan(Rate.of(DECIMAL_3_50)));
        assertFalse(Rate.of(DECIMAL_3_50).isGreaterThan(Rate.of(DECIMAL_5_50)));
    }

    @Test
    void shouldBeLessThan() {
        assertTrue(Rate.of(DECIMAL_3_50).isLessThan(Rate.of(DECIMAL_5_50)));
        assertFalse(Rate.of(DECIMAL_5_50).isLessThan(Rate.of(DECIMAL_3_50)));
    }

    @Test
    void shouldBeEqualValueWhenSameValue() {
        assertTrue(Rate.of(DECIMAL_5_25).equals(Rate.of(DECIMAL_5_250)));
    }

    @Test
    void shouldNotBeEqualValueWhenOtherIsNull() {
        assertFalse(Rate.of(DECIMAL_5_25).equals(null));
    }

    @Test
    void shouldNotBeEqualValueWhenDifferentValue() {
        assertFalse(Rate.of(DECIMAL_5_25).equals(Rate.of(DECIMAL_6_00)));
    }

    @Test
    void shouldHaveSameValueHashCodeForScaleDifferentValues() {
        assertEquals(
            Rate.of(DECIMAL_5_25).hashCode(),
            Rate.of(DECIMAL_5_250).hashCode());
    }

    @Test
    void equalsAndHashCodeShouldBeScaleInsensitiveLikeEqualsValue() {
        assertEquals(Rate.of(DECIMAL_5_25), Rate.of(DECIMAL_5_250));
        assertEquals(
            Rate.of(DECIMAL_5_25).hashCode(),
            Rate.of(DECIMAL_5_250).hashCode());
    }

    @Test
    void shouldToStringWithPercentSuffix() {
        assertEquals("5.25%", Rate.of(DECIMAL_5_25).toString());
    }
}
