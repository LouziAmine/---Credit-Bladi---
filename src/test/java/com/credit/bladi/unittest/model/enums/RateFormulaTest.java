package com.credit.bladi.unittest.model.enums;

import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_3_50;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_4_5000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_5_50;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.credit.bladi.model.enums.RateFormula;
import com.credit.bladi.model.vo.Rate;
import org.junit.jupiter.api.Test;

class RateFormulaTest {
    private final Rate rateMin = Rate.of(DECIMAL_3_50);
    private final Rate rateMax = Rate.of(DECIMAL_5_50);

    @Test
    void minReturnsRateMin() {
        Rate result = RateFormula.MIN.apply(rateMin, rateMax);
        assertEquals(0, result.value().compareTo(DECIMAL_3_50));
    }

    @Test
    void maxReturnsRateMax() {
        Rate result = RateFormula.MAX.apply(rateMin, rateMax);
        assertEquals(0, result.value().compareTo(DECIMAL_5_50));
    }

    @Test
    void averageReturnsMidpoint() {
        Rate result = RateFormula.AVERAGE.apply(rateMin, rateMax);
        assertEquals(0, result.value().compareTo(DECIMAL_4_5000));
    }
}
