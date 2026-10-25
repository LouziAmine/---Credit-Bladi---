package com.credit.bladi.unittest.model.vo;

import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_10;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_100;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_20;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_1_01;
import static com.credit.bladi.unittest.mock.data.MockAmounts.MINUS_DECIMAL_0_10;
import static com.credit.bladi.unittest.mock.data.MockAmounts.TEXT_0_10;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.model.vo.Ratio;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class RatioTest {
    @Test
    void shouldCreateWithPositiveValue() {
        Ratio ratio = Ratio.of(DECIMAL_0_10);
        assertEquals(0, ratio.value().compareTo(DECIMAL_0_10));
    }

    @Test
    void shouldRejectZeroValue() {
        assertThrows(IllegalArgumentException.class, () -> Ratio.of(BigDecimal.ZERO));
    }

    @Test
    void shouldRejectNegativeValue() {
        assertThrows(IllegalArgumentException.class, () -> Ratio.of(MINUS_DECIMAL_0_10));
    }

    @Test
    void shouldRejectValueAboveOne() {
        assertThrows(IllegalArgumentException.class, () -> Ratio.of(DECIMAL_1_01));
    }

    @Test
    void shouldAcceptValueAtOne() {
        Ratio ratio = Ratio.of(BigDecimal.ONE);
        assertEquals(0, ratio.value().compareTo(BigDecimal.ONE));
    }

    @Test
    void shouldRejectNullValue() {
        assertThrows(NullPointerException.class, () -> new Ratio(null));
    }

    @Test
    void shouldBeGreaterThan() {
        assertTrue(Ratio.of(DECIMAL_0_20).isGreaterThan(Ratio.of(DECIMAL_0_10)));
        assertFalse(Ratio.of(DECIMAL_0_10).isGreaterThan(Ratio.of(DECIMAL_0_20)));
    }

    @Test
    void shouldBeLessThan() {
        assertTrue(Ratio.of(DECIMAL_0_10).isLessThan(Ratio.of(DECIMAL_0_20)));
        assertFalse(Ratio.of(DECIMAL_0_20).isLessThan(Ratio.of(DECIMAL_0_10)));
    }

    @Test
    void shouldBeEqualValueWhenSameValue() {
        assertTrue(Ratio.of(DECIMAL_0_10).equals(Ratio.of(DECIMAL_0_100)));
    }

    @Test
    void shouldNotBeEqualValueWhenOtherIsNull() {
        assertFalse(Ratio.of(DECIMAL_0_10).equals(null));
    }

    @Test
    void shouldNotBeEqualValueWhenDifferentValue() {
        assertFalse(Ratio.of(DECIMAL_0_10).equals(Ratio.of(DECIMAL_0_20)));
    }

    @Test
    void shouldHaveSameValueHashCodeForScaleDifferentValues() {
        assertEquals(
            Ratio.of(DECIMAL_0_10).hashCode(),
            Ratio.of(DECIMAL_0_100).hashCode());
    }

    @Test
    void equalsAndHashCodeShouldBeScaleInsensitiveLikeEqualsValue() {
        assertEquals(Ratio.of(DECIMAL_0_10), Ratio.of(DECIMAL_0_100));
        assertEquals(
            Ratio.of(DECIMAL_0_10).hashCode(),
            Ratio.of(DECIMAL_0_100).hashCode());
    }

    @Test
    void shouldToStringPlain() {
        assertEquals(TEXT_0_10, Ratio.of(DECIMAL_0_10).toString());
    }
}
