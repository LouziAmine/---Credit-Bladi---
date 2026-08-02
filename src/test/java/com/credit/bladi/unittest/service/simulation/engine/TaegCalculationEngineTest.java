package com.credit.bladi.unittest.service.simulation.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.credit.bladi.model.vo.Money;
import com.credit.bladi.service.simulation.engine.TaegCalculationEngine;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class TaegCalculationEngineTest {

    private final TaegCalculationEngine engine = new TaegCalculationEngine();

    @Test
    void shouldCalculateTaegForTypicalLoan() {
        Money capital = Money.of(new BigDecimal("500000"));
        Money processingFees = Money.of(new BigDecimal("5000"));
        Money totalMonthlyPayment = Money.of(new BigDecimal("2800"));

        BigDecimal taeg = engine.calculateTaeg(capital, processingFees, totalMonthlyPayment, 240);

        assertNotNull(taeg);
        assertEquals(2, taeg.scale());
    }

    @Test
    void shouldStopAtMaxIterationsWhenToleranceNeverReached() throws Exception {
        java.lang.reflect.Constructor<TaegCalculationEngine> constructor =
            TaegCalculationEngine.class.getDeclaredConstructor(int.class, BigDecimal.class);
        constructor.setAccessible(true);
        TaegCalculationEngine boundedEngine = constructor.newInstance(1, BigDecimal.ZERO);

        Money capital = Money.of(new BigDecimal("500000"));
        Money processingFees = Money.of(new BigDecimal("5000"));
        Money totalMonthlyPayment = Money.of(new BigDecimal("2800"));

        BigDecimal taeg = boundedEngine.calculateTaeg(capital, processingFees, totalMonthlyPayment, 240);

        assertNotNull(taeg);
    }

    @Test
    void shouldComputePresentValueWithZeroRateAsFlatSum() throws Exception {
        Method method = TaegCalculationEngine.class.getDeclaredMethod(
            "presentValueOfPayments", BigDecimal.class, BigDecimal.class, int.class);
        method.setAccessible(true);

        BigDecimal result = (BigDecimal) method.invoke(
            engine, new BigDecimal("1000"), BigDecimal.ZERO, 12);

        assertEquals(0, result.compareTo(new BigDecimal("12000")));
    }
}
