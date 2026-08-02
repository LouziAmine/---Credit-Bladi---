package com.credit.bladi.unittest.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.model.vo.Money;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class MoneyTest {

    @Test
    void shouldCreateMoneyWithOf() {
        Money money = Money.of(new BigDecimal("1000.50"));
        assertEquals(new BigDecimal("1000.50"), money.amount());
    }

    @Test
    void shouldAddTwoMoneyValues() {
        Money m1 = Money.of(new BigDecimal("500"));
        Money m2 = Money.of(new BigDecimal("250.50"));

        Money result = m1.add(m2);

        assertEquals(0, result.amount().compareTo(new BigDecimal("750.50")));
    }

    @Test
    void shouldMultiplyByFactor() {
        Money money = Money.of(new BigDecimal("1000"));

        Money result = money.multiply(new BigDecimal("0.01"));

        assertEquals(0, result.amount().compareTo(new BigDecimal("10.00")));
    }

    @Test
    void shouldAllowZeroAmount() {
        Money money = Money.of(BigDecimal.ZERO);
        assertEquals(0, money.amount().compareTo(BigDecimal.ZERO));
    }

    @Test
    void shouldThrowForNegativeAmount() {
        assertThrows(IllegalArgumentException.class,
            () -> Money.of(new BigDecimal("-1")));
    }

    @Test
    void shouldThrowForNullAmount() {
        assertThrows(NullPointerException.class,
            () -> Money.of(null));
    }

    @Test
    void shouldHaveToStringWithMAD() {
        Money money = Money.of(new BigDecimal("500"));
        assertTrue(money.toString().contains("MAD"));
        assertTrue(money.toString().contains("500"));
    }

    @Test
    void shouldBeEqualWhenSameAmount() {
        Money m1 = Money.of(new BigDecimal("100"));
        Money m2 = Money.of(new BigDecimal("100"));
        assertEquals(m1, m2);
    }
}