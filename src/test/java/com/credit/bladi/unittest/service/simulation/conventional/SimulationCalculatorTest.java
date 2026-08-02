package com.credit.bladi.unittest.service.simulation.conventional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import com.credit.bladi.model.bam.BamParametersModel;
import com.credit.bladi.model.simulation.AmortizationRow;
import com.credit.bladi.model.simulation.CreditRateModel;
import com.credit.bladi.model.simulation.FinancialSnapshot;
import com.credit.bladi.model.simulation.RateBoundsSnapshot;
import com.credit.bladi.model.vo.DurationRange;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;
import com.credit.bladi.model.vo.Ratio;
import com.credit.bladi.service.simulation.conventional.SimulationCalculator;
import com.credit.bladi.service.simulation.engine.RateCalculationEngine;
import com.credit.bladi.service.simulation.engine.TaegCalculationEngine;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SimulationCalculatorTest {

    @Mock
    private RateCalculationEngine engine;

    @Mock
    private TaegCalculationEngine taegEngine;

    private SimulationCalculator calculator;

    private CreditRateModel creditRate;
    private BamParametersModel bamParams;
    private Money capital;
    private Money propertyValue;

    @BeforeEach
    void setUp() {
        calculator = new SimulationCalculator(engine, taegEngine);

        creditRate = CreditRateModel.getBuilder()
            .id(1L)
            .cspCode(com.credit.bladi.model.enums.CspCode.SALA)
            .rateMin(Rate.of(new BigDecimal("3.50")))
            .rateMax(Rate.of(new BigDecimal("5.50")))
            .durationRange(DurationRange.of(12, 300))
            .active(true)
            .build();

        bamParams = BamParametersModel.getBuilder()
            .id(1L)
            .processingFeesRatio(Ratio.of(new BigDecimal("0.01")))
            .minProcessingFees(Money.of(new BigDecimal("5000")))
            .maxProcessingFees(Money.of(new BigDecimal("20000")))
            .registrationFeeRatio(Ratio.of(new BigDecimal("0.015")))
            .landConservationRatio(Ratio.of(new BigDecimal("0.015")))
            .notaryFeeRatio(Ratio.of(new BigDecimal("0.01")))
            .active(true)
            .build();

        capital = Money.of(new BigDecimal("500000"));
        propertyValue = Money.of(new BigDecimal("600000"));
    }

    @Test
    void shouldComputeBoundsWithMinAndMaxRates() {
        Money mockPayment = Money.of(new BigDecimal("2800"));
        Money mockInterest = Money.of(new BigDecimal("72000"));
        Money mockCost = Money.of(new BigDecimal("672000"));
        Money mockFees = Money.of(new BigDecimal("5000"));

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
        Money monthlyPayment = Money.of(new BigDecimal("2800"));
        Rate applicableRate = Rate.of(new BigDecimal("4.80"));
        BigDecimal monthlyRate = new BigDecimal("0.004");
        Money m = Money.of(new BigDecimal("1000"));
        List<AmortizationRow> schedule = List.of(new AmortizationRow(1, m, m, m, m, m));
        when(engine.calculateMonthlyRate(applicableRate)).thenReturn(monthlyRate);
        when(engine.generateAmortizationSchedule(capital, monthlyRate, monthlyPayment, 240)).thenReturn(schedule);

        List<AmortizationRow> result = calculator.computeSchedule(capital, applicableRate, monthlyPayment, 240);

        assertEquals(1, result.size());
    }

    @Test
    void shouldCalculateTaegByDelegatingToTaegEngine() {
        Money m = Money.of(new BigDecimal("1000"));
        FinancialSnapshot snapshot = new FinancialSnapshot(m, m, m, m, m, m, m, m, m, m, m);
        BigDecimal expectedTaeg = new BigDecimal("5.50");
        when(taegEngine.calculateTaeg(capital, m, Money.of(new BigDecimal("2000")), 240)).thenReturn(expectedTaeg);

        BigDecimal result = calculator.calculateTaeg(capital, snapshot, 240);

        assertEquals(expectedTaeg, result);
    }
}