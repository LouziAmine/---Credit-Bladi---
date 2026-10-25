package com.credit.bladi.unittest.service.calculation;

import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_1000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_10000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_500000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_5000000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_0004;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_01;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_015;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_1_00;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_200_00;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_325465_60;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_3439_44;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_400_00;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_48000_00;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_5000_00;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_5_5;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_7500_00;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_825465_60;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.model.bam.BamParametersModel;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;
import com.credit.bladi.model.vo.Ratio;
import com.credit.bladi.service.calculation.RateCalculationEngine;
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
            .processingFeesRatio(Ratio.of(DECIMAL_0_01))
            .minProcessingFees(Money.of(AMOUNT_1000))
            .maxProcessingFees(Money.of(AMOUNT_10000))
            .registrationFeeRatio(Ratio.of(DECIMAL_0_01))
            .landConservationRatio(Ratio.of(DECIMAL_0_015))
            .notaryFeeRatio(Ratio.of(DECIMAL_0_01))
            .build();
    }

    @Test
    void shouldCalculateMonthlyPaymentUsingBamAnnuityFormula() {
        Money capital = Money.of(AMOUNT_500000);
        Rate annualRate = Rate.of(DECIMAL_5_5);
        int months = 240;

        BigDecimal result = engine.calculateMonthlyPayment(capital, annualRate, months).amount();

        assertTrue(result.subtract(DECIMAL_3439_44).abs().compareTo(DECIMAL_1_00) <= 0);
    }

    @Test
    void shouldCalculateTotalInterestCostCorrectly() {
        Money monthly = Money.of(DECIMAL_3439_44);
        Money capital = Money.of(AMOUNT_500000);
        int months = 240;

        BigDecimal result = engine.calculateTotalInterestCost(monthly, months, capital).amount();

        assertTrue(result.subtract(DECIMAL_325465_60).abs().compareTo(DECIMAL_1_00) <= 0);
    }

    @Test
    void shouldCalculateTotalCostAsMonthlyTimesMonths() {
        Money monthly = Money.of(DECIMAL_3439_44);
        int months = 240;

        BigDecimal result = engine.calculateTotalCost(monthly, months).amount();

        assertEquals(0, result.compareTo(DECIMAL_825465_60));
    }

    @Test
    void shouldCalculateRegistrationFeesUsingRatio() {
        Money capital = Money.of(AMOUNT_500000);

        BigDecimal result = engine.calculateRegistrationFees(capital, params).amount();

        assertEquals(0, result.compareTo(DECIMAL_5000_00));
    }

    @Test
    void shouldCalculateLandConservationFeesUsingRatio() {
        Money capital = Money.of(AMOUNT_500000);

        BigDecimal result = engine.calculateLandConservationFees(capital, params).amount();

        assertEquals(0, result.compareTo(DECIMAL_7500_00));
    }

    @Test
    void shouldClampProcessingFeesToMinWhenTooLow() {
        Money tinyCapital = Money.of(AMOUNT_10000);

        BigDecimal result = engine.calculateProcessingFees(tinyCapital, params).amount();

        assertEquals(0, result.compareTo(AMOUNT_1000));
    }

    @Test
    void shouldClampProcessingFeesToMaxWhenTooHigh() {
        Money hugeCapital = Money.of(AMOUNT_5000000);

        BigDecimal result = engine.calculateProcessingFees(hugeCapital, params).amount();

        assertEquals(0, result.compareTo(AMOUNT_10000));
    }

    @Test
    void shouldReturnComputedProcessingFeesWhenWithinRange() {
        Money capital = Money.of(AMOUNT_500000);

        BigDecimal result = engine.calculateProcessingFees(capital, params).amount();

        assertEquals(0, result.compareTo(DECIMAL_5000_00));
    }

    @Test
    void shouldCalculateAdiMonthlyPremiumForSingleBorrower() {
        Money capital = Money.of(AMOUNT_500000);
        BamParametersModel withAdi = BamParametersModel.getBuilder()
            .adiMonthlyRate(Ratio.of(DECIMAL_0_0004))
            .build();

        BigDecimal result = engine.calculateAdiMonthlyPremium(capital, withAdi, false).amount();

        assertEquals(0, result.compareTo(DECIMAL_200_00));
    }

    @Test
    void shouldDoubleAdiMonthlyPremiumForCoBorrower() {
        Money capital = Money.of(AMOUNT_500000);
        BamParametersModel withAdi = BamParametersModel.getBuilder()
            .adiMonthlyRate(Ratio.of(DECIMAL_0_0004))
            .build();

        BigDecimal result = engine.calculateAdiMonthlyPremium(capital, withAdi, true).amount();

        assertEquals(0, result.compareTo(DECIMAL_400_00));
    }

    @Test
    void shouldCalculateTotalAdiCostAsMonthlyTimesMonths() {
        Money adiMonthlyPremium = Money.of(DECIMAL_200_00);

        BigDecimal result = engine.calculateTotalAdiCost(adiMonthlyPremium, 240).amount();

        assertEquals(0, result.compareTo(DECIMAL_48000_00));
    }
}
