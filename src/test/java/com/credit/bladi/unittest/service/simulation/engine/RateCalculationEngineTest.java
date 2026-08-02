package com.credit.bladi.unittest.service.simulation.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.model.bam.BamParametersModel;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;
import com.credit.bladi.model.vo.Ratio;
import com.credit.bladi.service.simulation.engine.RateCalculationEngine;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RateCalculationEngineTest {

    private RateCalculationEngine engine;
    private BamParametersModel params;

    @BeforeEach
    void setUp() {
        engine = new RateCalculationEngine();
        params = BamParametersModel.getBuilder()
            .processingFeesRatio(Ratio.of(new BigDecimal("0.01")))
            .minProcessingFees(Money.of(new BigDecimal("1000")))
            .maxProcessingFees(Money.of(new BigDecimal("10000")))
            .registrationFeeRatio(Ratio.of(new BigDecimal("0.01")))
            .landConservationRatio(Ratio.of(new BigDecimal("0.015")))
            .notaryFeeRatio(Ratio.of(new BigDecimal("0.01")))
            .build();
    }

    @Test
    void shouldCalculateMonthlyPaymentUsingBamAnnuityFormula() {
        Money capital = Money.of(new BigDecimal("500000"));
        Rate annualRate = Rate.of(new BigDecimal("5.5"));
        int months = 240;

        BigDecimal result = engine.calculateMonthlyPayment(capital, annualRate, months).amount();

        assertTrue(result.subtract(new BigDecimal("3439.44")).abs().compareTo(new BigDecimal("1.00")) <= 0);
    }

    @Test
    void shouldCalculateTotalInterestCostCorrectly() {
        Money monthly = Money.of(new BigDecimal("3439.44"));
        Money capital = Money.of(new BigDecimal("500000"));
        int months = 240;

        BigDecimal result = engine.calculateTotalInterestCost(monthly, months, capital).amount();

        assertTrue(result.subtract(new BigDecimal("325465.60")).abs().compareTo(new BigDecimal("1.00")) <= 0);
    }

    @Test
    void shouldCalculateTotalCostAsMonthlyTimesMonths() {
        Money monthly = Money.of(new BigDecimal("3439.44"));
        int months = 240;

        BigDecimal result = engine.calculateTotalCost(monthly, months).amount();

        assertEquals(0, result.compareTo(new BigDecimal("825465.60")));
    }

    @Test
    void shouldCalculateRegistrationFeesUsingRatio() {
        Money capital = Money.of(new BigDecimal("500000"));

        BigDecimal result = engine.calculateRegistrationFees(capital, params).amount();

        assertEquals(0, result.compareTo(new BigDecimal("5000.00")));
    }

    @Test
    void shouldCalculateLandConservationFeesUsingRatio() {
        Money capital = Money.of(new BigDecimal("500000"));

        BigDecimal result = engine.calculateLandConservationFees(capital, params).amount();

        assertEquals(0, result.compareTo(new BigDecimal("7500.00")));
    }

    @Test
    void shouldClampProcessingFeesToMinWhenTooLow() {
        Money tinyCapital = Money.of(new BigDecimal("10000"));

        BigDecimal result = engine.calculateProcessingFees(tinyCapital, params).amount();

        assertEquals(0, result.compareTo(new BigDecimal("1000")));
    }

    @Test
    void shouldClampProcessingFeesToMaxWhenTooHigh() {
        Money hugeCapital = Money.of(new BigDecimal("5000000"));

        BigDecimal result = engine.calculateProcessingFees(hugeCapital, params).amount();

        assertEquals(0, result.compareTo(new BigDecimal("10000")));
    }

    @Test
    void shouldReturnComputedProcessingFeesWhenWithinRange() {
        Money capital = Money.of(new BigDecimal("500000"));

        BigDecimal result = engine.calculateProcessingFees(capital, params).amount();

        assertEquals(0, result.compareTo(new BigDecimal("5000.00")));
    }

    @Test
    void shouldCalculateAdiMonthlyPremiumForSingleBorrower() {
        Money capital = Money.of(new BigDecimal("500000"));
        BamParametersModel withAdi = BamParametersModel.getBuilder()
            .adiMonthlyRate(Ratio.of(new BigDecimal("0.0004")))
            .build();

        BigDecimal result = engine.calculateAdiMonthlyPremium(capital, withAdi, false).amount();

        assertEquals(0, result.compareTo(new BigDecimal("200.00")));
    }

    @Test
    void shouldDoubleAdiMonthlyPremiumForCoBorrower() {
        Money capital = Money.of(new BigDecimal("500000"));
        BamParametersModel withAdi = BamParametersModel.getBuilder()
            .adiMonthlyRate(Ratio.of(new BigDecimal("0.0004")))
            .build();

        BigDecimal result = engine.calculateAdiMonthlyPremium(capital, withAdi, true).amount();

        assertEquals(0, result.compareTo(new BigDecimal("400.00")));
    }

    @Test
    void shouldCalculateTotalAdiCostAsMonthlyTimesMonths() {
        Money adiMonthlyPremium = Money.of(new BigDecimal("200.00"));

        BigDecimal result = engine.calculateTotalAdiCost(adiMonthlyPremium, 240).amount();

        assertEquals(0, result.compareTo(new BigDecimal("48000.00")));
    }
}
