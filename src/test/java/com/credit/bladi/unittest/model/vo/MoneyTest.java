package com.credit.bladi.unittest.model.vo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.model.vo.Money;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class MoneyTest {

    @Test
    void shouldCreateWithPositiveAmount() {
        Money money = Money.of(new BigDecimal("1000"));
        assertEquals(0, money.amount().compareTo(new BigDecimal("1000")));
    }

    @Test
    void shouldCreateWithZeroAmount() {
        Money money = Money.of(BigDecimal.ZERO);
        assertEquals(0, money.amount().compareTo(BigDecimal.ZERO));
    }

    @Test
    void shouldRejectNegativeAmount() {
        assertThrows(IllegalArgumentException.class, () -> Money.of(new BigDecimal("-1")));
    }

    @Test
    void shouldRejectNullAmount() {
        assertThrows(NullPointerException.class, () -> new Money(null));
    }

    @Test
    void shouldAddAmounts() {
        Money result = Money.of(new BigDecimal("1000")).add(Money.of(new BigDecimal("500")));
        assertEquals(0, result.amount().compareTo(new BigDecimal("1500")));
    }

    @Test
    void shouldSubtractAmounts() {
        Money result = Money.of(new BigDecimal("1000")).subtract(Money.of(new BigDecimal("500")));
        assertEquals(0, result.amount().compareTo(new BigDecimal("500")));
    }

    @Test
    void shouldMultiplyByFactorAndRound() {
        Money result = Money.of(new BigDecimal("1000")).multiply(new BigDecimal("0.155"));
        assertEquals(0, result.amount().compareTo(new BigDecimal("155.00")));
    }

    @Test
    void shouldBeEqualValueWhenSameAmount() {
        assertTrue(Money.of(new BigDecimal("1000")).equalsValue(Money.of(new BigDecimal("1000.00"))));
    }

    @Test
    void shouldNotBeEqualValueWhenOtherIsNull() {
        assertFalse(Money.of(new BigDecimal("1000")).equalsValue(null));
    }

    @Test
    void shouldNotBeEqualValueWhenDifferentAmount() {
        assertFalse(Money.of(new BigDecimal("1000")).equalsValue(Money.of(new BigDecimal("2000"))));
    }

    @Test
    void shouldHaveSameValueHashCodeForScaleDifferentAmounts() {
        assertEquals(
            Money.of(new BigDecimal("1000")).valueHashCode(),
            Money.of(new BigDecimal("1000.00")).valueHashCode());
    }

    @Test
    void shouldBeGreaterThan() {
        assertTrue(Money.of(new BigDecimal("2000")).isGreaterThan(Money.of(new BigDecimal("1000"))));
        assertFalse(Money.of(new BigDecimal("1000")).isGreaterThan(Money.of(new BigDecimal("2000"))));
    }

    @Test
    void shouldBeLessThan() {
        assertTrue(Money.of(new BigDecimal("1000")).isLessThan(Money.of(new BigDecimal("2000"))));
        assertFalse(Money.of(new BigDecimal("2000")).isLessThan(Money.of(new BigDecimal("1000"))));
    }

    @Test
    void shouldToStringWithCurrencySuffix() {
        assertEquals("1000 MAD", Money.of(new BigDecimal("1000")).toString());
    }
}