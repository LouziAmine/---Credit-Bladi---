package com.credit.bladi.unittest.model.vo;

import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_1000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_1500;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_2000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_500;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_155;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_1000_00;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_155_00;
import static com.credit.bladi.unittest.mock.data.MockAmounts.MINUS_AMOUNT_1;
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
        Money money = Money.of(AMOUNT_1000);
        assertEquals(0, money.amount().compareTo(AMOUNT_1000));
    }

    @Test
    void shouldCreateWithZeroAmount() {
        Money money = Money.of(BigDecimal.ZERO);
        assertEquals(0, money.amount().compareTo(BigDecimal.ZERO));
    }

    @Test
    void shouldRejectNegativeAmount() {
        assertThrows(IllegalArgumentException.class, () -> Money.of(MINUS_AMOUNT_1));
    }

    @Test
    void shouldRejectNullAmount() {
        assertThrows(NullPointerException.class, () -> new Money(null));
    }

    @Test
    void shouldAddAmounts() {
        Money result = Money.of(AMOUNT_1000).add(Money.of(AMOUNT_500));
        assertEquals(0, result.amount().compareTo(AMOUNT_1500));
    }

    @Test
    void shouldSubtractAmounts() {
        Money result = Money.of(AMOUNT_1000).subtract(Money.of(AMOUNT_500));
        assertEquals(0, result.amount().compareTo(AMOUNT_500));
    }

    @Test
    void shouldMultiplyByFactorAndRound() {
        Money result = Money.of(AMOUNT_1000).multiply(DECIMAL_0_155);
        assertEquals(0, result.amount().compareTo(DECIMAL_155_00));
    }

    @Test
    void shouldBeEqualValueWhenSameAmount() {
        assertTrue(Money.of(AMOUNT_1000).equalsValue(Money.of(DECIMAL_1000_00)));
    }

    @Test
    void shouldNotBeEqualValueWhenOtherIsNull() {
        assertFalse(Money.of(AMOUNT_1000).equalsValue(null));
    }

    @Test
    void shouldNotBeEqualValueWhenDifferentAmount() {
        assertFalse(Money.of(AMOUNT_1000).equalsValue(Money.of(AMOUNT_2000)));
    }

    @Test
    void shouldHaveSameValueHashCodeForScaleDifferentAmounts() {
        assertEquals(
            Money.of(AMOUNT_1000).valueHashCode(),
            Money.of(DECIMAL_1000_00).valueHashCode());
    }

    @Test
    void equalsAndHashCodeShouldBeScaleInsensitiveLikeEqualsValue() {
        assertEquals(Money.of(AMOUNT_1000), Money.of(DECIMAL_1000_00));
        assertEquals(
            Money.of(AMOUNT_1000).hashCode(),
            Money.of(DECIMAL_1000_00).hashCode());
    }

    @Test
    void shouldBeGreaterThan() {
        assertTrue(Money.of(AMOUNT_2000).isGreaterThan(Money.of(AMOUNT_1000)));
        assertFalse(Money.of(AMOUNT_1000).isGreaterThan(Money.of(AMOUNT_2000)));
    }

    @Test
    void shouldBeLessThan() {
        assertTrue(Money.of(AMOUNT_1000).isLessThan(Money.of(AMOUNT_2000)));
        assertFalse(Money.of(AMOUNT_2000).isLessThan(Money.of(AMOUNT_1000)));
    }

    @Test
    void shouldToStringWithCurrencySuffix() {
        assertEquals("1000 MAD", Money.of(AMOUNT_1000).toString());
    }
}
