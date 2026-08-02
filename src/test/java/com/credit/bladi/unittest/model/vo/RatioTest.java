package com.credit.bladi.unittest.model.vo;

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
        Ratio ratio = Ratio.of(new BigDecimal("0.10"));
        assertEquals(0, ratio.value().compareTo(new BigDecimal("0.10")));
    }

    @Test
    void shouldRejectZeroValue() {
        assertThrows(IllegalArgumentException.class, () -> Ratio.of(BigDecimal.ZERO));
    }

    @Test
    void shouldRejectNegativeValue() {
        assertThrows(IllegalArgumentException.class, () -> Ratio.of(new BigDecimal("-0.10")));
    }

    @Test
    void shouldRejectValueAboveOne() {
        assertThrows(IllegalArgumentException.class, () -> Ratio.of(new BigDecimal("1.01")));
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
        assertTrue(Ratio.of(new BigDecimal("0.20")).isGreaterThan(Ratio.of(new BigDecimal("0.10"))));
        assertFalse(Ratio.of(new BigDecimal("0.10")).isGreaterThan(Ratio.of(new BigDecimal("0.20"))));
    }

    @Test
    void shouldBeLessThan() {
        assertTrue(Ratio.of(new BigDecimal("0.10")).isLessThan(Ratio.of(new BigDecimal("0.20"))));
        assertFalse(Ratio.of(new BigDecimal("0.20")).isLessThan(Ratio.of(new BigDecimal("0.10"))));
    }

    @Test
    void shouldBeEqualValueWhenSameValue() {
        assertTrue(Ratio.of(new BigDecimal("0.10")).equalsValue(Ratio.of(new BigDecimal("0.100"))));
    }

    @Test
    void shouldNotBeEqualValueWhenOtherIsNull() {
        assertFalse(Ratio.of(new BigDecimal("0.10")).equalsValue(null));
    }

    @Test
    void shouldNotBeEqualValueWhenDifferentValue() {
        assertFalse(Ratio.of(new BigDecimal("0.10")).equalsValue(Ratio.of(new BigDecimal("0.20"))));
    }

    @Test
    void shouldHaveSameValueHashCodeForScaleDifferentValues() {
        assertEquals(
            Ratio.of(new BigDecimal("0.10")).valueHashCode(),
            Ratio.of(new BigDecimal("0.100")).valueHashCode());
    }

    @Test
    void shouldToStringPlain() {
        assertEquals("0.10", Ratio.of(new BigDecimal("0.10")).toString());
    }
}