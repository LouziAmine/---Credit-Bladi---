package com.credit.bladi.unittest.model.vo;

import static com.credit.bladi.model.vo.ValueObjectMappers.toBigDecimal;
import static com.credit.bladi.model.vo.ValueObjectMappers.toMoney;
import static com.credit.bladi.model.vo.ValueObjectMappers.toRate;
import static com.credit.bladi.model.vo.ValueObjectMappers.toRatio;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_1000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_90;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_5_25;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;
import com.credit.bladi.model.vo.Ratio;
import org.junit.jupiter.api.Test;

class ValueObjectMappersTest {
    @Test
    void toRateShouldReturnNullWhenValueIsNull() {
        assertNull(toRate(null));
    }

    @Test
    void toRateShouldWrapNonNullValue() {
        assertEquals(Rate.of(DECIMAL_5_25), toRate(DECIMAL_5_25));
    }

    @Test
    void toRatioShouldReturnNullWhenValueIsNull() {
        assertNull(toRatio(null));
    }

    @Test
    void toRatioShouldWrapNonNullValue() {
        assertEquals(Ratio.of(DECIMAL_0_90), toRatio(DECIMAL_0_90));
    }

    @Test
    void toMoneyShouldReturnNullWhenValueIsNull() {
        assertNull(toMoney(null));
    }

    @Test
    void toMoneyShouldWrapNonNullValue() {
        assertEquals(Money.of(AMOUNT_1000), toMoney(AMOUNT_1000));
    }

    @Test
    void toBigDecimalShouldReturnNullForNullRate() {
        assertNull(toBigDecimal((Rate) null));
    }

    @Test
    void toBigDecimalShouldReturnNullForNullRatio() {
        assertNull(toBigDecimal((Ratio) null));
    }

    @Test
    void toBigDecimalShouldReturnNullForNullMoney() {
        assertNull(toBigDecimal((Money) null));
    }

    @Test
    void toBigDecimalShouldUnwrapRate() {
        assertEquals(0, DECIMAL_5_25.compareTo(toBigDecimal(Rate.of(DECIMAL_5_25))));
    }

    @Test
    void toBigDecimalShouldUnwrapRatio() {
        assertEquals(0, DECIMAL_0_90.compareTo(toBigDecimal(Ratio.of(DECIMAL_0_90))));
    }

    @Test
    void toBigDecimalShouldUnwrapMoney() {
        assertEquals(0, AMOUNT_1000.compareTo(toBigDecimal(Money.of(AMOUNT_1000))));
    }
}
