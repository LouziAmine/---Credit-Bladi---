package com.credit.bladi.unittest.model;

import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_100;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_1000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_500;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_01;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_1000_50;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_10_00;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_250_50;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_750_50;
import static com.credit.bladi.unittest.mock.data.MockAmounts.MINUS_AMOUNT_1;
import static com.credit.bladi.unittest.mock.data.MockAmounts.TEXT_500;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_MAD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.model.vo.Money;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class MoneyTest {
    @Test
    void shouldCreateMoneyWithOf() {
        Money money = Money.of(DECIMAL_1000_50);
        assertEquals(DECIMAL_1000_50, money.amount());
    }

    @Test
    void shouldAddTwoMoneyValues() {
        Money m1 = Money.of(AMOUNT_500);
        Money m2 = Money.of(DECIMAL_250_50);

        Money result = m1.add(m2);

        assertEquals(0, result.amount().compareTo(DECIMAL_750_50));
    }

    @Test
    void shouldMultiplyByFactor() {
        Money money = Money.of(AMOUNT_1000);

        Money result = money.multiply(DECIMAL_0_01);

        assertEquals(0, result.amount().compareTo(DECIMAL_10_00));
    }

    @Test
    void shouldAllowZeroAmount() {
        Money money = Money.of(BigDecimal.ZERO);
        assertEquals(0, money.amount().compareTo(BigDecimal.ZERO));
    }

    @Test
    void shouldThrowForNegativeAmount() {
        assertThrows(IllegalArgumentException.class,
            () -> Money.of(MINUS_AMOUNT_1));
    }

    @Test
    void shouldThrowForNullAmount() {
        assertThrows(NullPointerException.class,
            () -> Money.of(null));
    }

    @Test
    void shouldHaveToStringWithMAD() {
        Money money = Money.of(AMOUNT_500);
        assertTrue(money.toString().contains(CODE_MAD));
        assertTrue(money.toString().contains(TEXT_500));
    }

    @Test
    void shouldBeEqualWhenSameAmount() {
        Money m1 = Money.of(AMOUNT_100);
        Money m2 = Money.of(AMOUNT_100);
        assertEquals(m1, m2);
    }
}
