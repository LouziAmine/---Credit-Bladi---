package com.credit.bladi.unittest.service.mortgage.conventional;

import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_1000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_2000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_20000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_2800;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_5000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_500000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_600000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_672000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_72000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_004;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_01;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_015;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_3_50;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_4_80;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_5_50;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import com.credit.bladi.model.bam.BamParametersModel;
import com.credit.bladi.model.creditrate.CreditRateModel;
import com.credit.bladi.model.mortgage.FinancialSnapshot;
import com.credit.bladi.model.mortgage.RateBoundsSnapshot;
import com.credit.bladi.model.vo.AmortizationRow;
import com.credit.bladi.model.vo.DurationRange;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;
import com.credit.bladi.model.vo.Ratio;
import com.credit.bladi.service.calculation.RateCalculationEngine;
import com.credit.bladi.service.calculation.TaegCalculationEngine;
import com.credit.bladi.service.mortgage.conventional.MortgageCreditCalculator;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MortgageCreditCalculatorTest {
    @Mock
    private RateCalculationEngine engine;

    @Mock
    private TaegCalculationEngine taegEngine;

    private MortgageCreditCalculator calculator;

    private CreditRateModel creditRate;
    private BamParametersModel bamParams;
    private Money capital;
    private Money propertyValue;

    @BeforeEach
    void setUp() {
        calculator = new MortgageCreditCalculator(engine, taegEngine);

        creditRate = CreditRateModel.getBuilder()
            .id(1L)
            .cspCode(com.credit.bladi.model.enums.CspCode.SALA)
            .rateMin(Rate.of(DECIMAL_3_50))
            .rateMax(Rate.of(DECIMAL_5_50))
            .durationRange(DurationRange.of(12, 300))
            .active(true)
            .build();

        bamParams = BamParametersModel.getBuilder()
            .id(1L)
            .processingFeesRatio(Ratio.of(DECIMAL_0_01))
            .minProcessingFees(Money.of(AMOUNT_5000))
            .maxProcessingFees(Money.of(AMOUNT_20000))
            .registrationFeeRatio(Ratio.of(DECIMAL_0_015))
            .landConservationRatio(Ratio.of(DECIMAL_0_015))
            .notaryFeeRatio(Ratio.of(DECIMAL_0_01))
            .active(true)
            .build();

        capital = Money.of(AMOUNT_500000);
        propertyValue = Money.of(AMOUNT_600000);
    }

    @Test
    void shouldComputeBoundsWithMinAndMaxRates() {
        Money mockPayment = Money.of(AMOUNT_2800);
        Money mockInterest = Money.of(AMOUNT_72000);
        Money mockCost = Money.of(AMOUNT_672000);
        Money mockFees = Money.of(AMOUNT_5000);

        when(engine.calculateMonthlyPayment(any(Money.class), any(Rate.class), anyInt()))
            .thenReturn(mockPayment);
        when(engine.calculateTotalInterestCost(any(Money.class), anyInt(), any(Money.class)))
            .thenReturn(mockInterest);
        when(engine.calculateTotalCost(any(Money.class), anyInt()))
            .thenReturn(mockCost);
        when(engine.calculateRegistrationFees(any(Money.class), any(BamParametersModel.class)))
            .thenReturn(mockFees);
        when(engine.calculateLandConservationFees(any(Money.class), any(BamParametersModel.class)))
            .thenReturn(mockFees);
        when(engine.calculateNotaryFees(any(Money.class), any(BamParametersModel.class)))
            .thenReturn(mockFees);
        when(engine.calculateProcessingFees(any(Money.class), any(BamParametersModel.class)))
            .thenReturn(mockFees);
        when(engine.calculateAdiMonthlyPremium(any(Money.class), any(BamParametersModel.class), anyBoolean()))
            .thenReturn(Money.of(BigDecimal.ZERO));
        when(engine.calculateTotalAdiCost(any(Money.class), anyInt()))
            .thenReturn(Money.of(BigDecimal.ZERO));

        RateBoundsSnapshot bounds = calculator.computeBounds(
            capital, propertyValue, creditRate, 240, bamParams, false);

        assertNotNull(bounds);
        assertNotNull(bounds.min());
        assertNotNull(bounds.max());
        assertNotNull(bounds.min().monthlyPayment());
        assertNotNull(bounds.max().monthlyPayment());
    }

    @Test
    void shouldComputeScheduleByDelegatingToEngine() {
        Money monthlyPayment = Money.of(AMOUNT_2800);
        Rate applicableRate = Rate.of(DECIMAL_4_80);
        BigDecimal monthlyRate = DECIMAL_0_004;
        Money m = Money.of(AMOUNT_1000);
        List<AmortizationRow> schedule = List.of(new AmortizationRow(1, m, m, m, m, m));
        when(engine.calculateMonthlyRate(applicableRate)).thenReturn(monthlyRate);
        when(engine.generateAmortizationSchedule(capital, monthlyRate, monthlyPayment, 240)).thenReturn(schedule);

        List<AmortizationRow> result = calculator.computeSchedule(capital, applicableRate, monthlyPayment, 240);

        assertEquals(1, result.size());
    }

    @Test
    void shouldCalculateTaegByDelegatingToTaegEngine() {
        Money m = Money.of(AMOUNT_1000);
        FinancialSnapshot snapshot = new FinancialSnapshot(m, m, m, m, m, m, m, m, m, m, m);
        BigDecimal expectedTaeg = DECIMAL_5_50;
        when(taegEngine.calculateTaeg(capital, m, Money.of(AMOUNT_2000), 240)).thenReturn(expectedTaeg);

        BigDecimal result = calculator.calculateTaeg(capital, snapshot, 240);

        assertEquals(expectedTaeg, result);
    }
}
