package com.credit.bladi.unittest.service.simulation.conventional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import com.credit.bladi.exception.BAMException;
import com.credit.bladi.exception.SimulationException;
import com.credit.bladi.model.bam.BamParametersModel;
import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.model.enums.ProductType;
import com.credit.bladi.model.simulation.AmortizationRow;
import com.credit.bladi.model.simulation.CreditRateModel;
import com.credit.bladi.model.simulation.FinancialSnapshot;
import com.credit.bladi.model.simulation.RateBoundsSnapshot;
import com.credit.bladi.model.simulation.request.SimulationRequest;
import com.credit.bladi.model.vo.DurationRange;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;
import com.credit.bladi.model.vo.Ratio;
import com.credit.bladi.repository.creditrate.ICreditRateRepository;
import com.credit.bladi.service.simulation.conventional.ConventionalFinancingStrategy;
import com.credit.bladi.service.simulation.conventional.RateCalculationStrategy;
import com.credit.bladi.service.simulation.conventional.RateStrategyFactory;
import com.credit.bladi.service.simulation.conventional.SimulationCalculator;
import com.credit.bladi.service.simulation.strategy.FinancingResult;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConventionalFinancingStrategyTest {

    @Mock
    private ICreditRateRepository creditRateRepository;

    @Mock
    private RateStrategyFactory rateStrategyFactory;

    @Mock
    private SimulationCalculator calculator;

    @Mock
    private RateCalculationStrategy cspStrategy;

    private ConventionalFinancingStrategy strategy;

    private CreditRateModel creditRate;
    private BamParametersModel bamParameters;
    private SimulationRequest request;

    @BeforeEach
    void setUp() {
        strategy = new ConventionalFinancingStrategy(creditRateRepository, rateStrategyFactory, calculator);

        creditRate = CreditRateModel.getBuilder()
            .id(1L)
            .cspCode(CspCode.SALA)
            .rateMin(Rate.of(new BigDecimal("4.5")))
            .rateMax(Rate.of(new BigDecimal("6.0")))
            .durationRange(DurationRange.of(60, 300))
            .active(true)
            .build();

        bamParameters = BamParametersModel.getBuilder()
            .id(1L)
            .processingFeesRatio(Ratio.of(new BigDecimal("0.01")))
            .minProcessingFees(Money.of(new BigDecimal("1000")))
            .maxProcessingFees(Money.of(new BigDecimal("10000")))
            .registrationFeeRatio(Ratio.of(new BigDecimal("0.01")))
            .landConservationRatio(Ratio.of(new BigDecimal("0.015")))
            .notaryFeeRatio(Ratio.of(new BigDecimal("0.01")))
            .ltvRatio(Ratio.of(new BigDecimal("0.90")))
            .maxDebtRatio(Ratio.of(new BigDecimal("0.40")))
            .active(true)
            .build();

        request = SimulationRequest.getBuilder()
            .amount(Money.of(new BigDecimal("500000")))
            .duration(240)
            .cspCode(CspCode.SALA)
            .nationalityCode("MA")
            .moroccanResident(true)
            .propertyValue(Money.of(new BigDecimal("600000")))
            .monthlyIncome(Money.of(new BigDecimal("20000")))
            .build();
    }

    @Test
    void getProductTypeShouldReturnConventional() {
        assertEquals(ProductType.CONVENTIONAL, strategy.getProductType());
    }

    @Test
    void shouldReturnFinancingResultForEligibleRequest() {
        Money monthlyPayment = Money.of(new BigDecimal("3430.46"));
        FinancialSnapshot snapshot = new FinancialSnapshot(
            monthlyPayment, Money.of(BigDecimal.ZERO), monthlyPayment,
            Money.of(new BigDecimal("323310.40")), Money.of(BigDecimal.ZERO),
            Money.of(new BigDecimal("823310.40")), Money.of(new BigDecimal("5000")),
            Money.of(new BigDecimal("7500")), Money.of(new BigDecimal("5000")),
            Money.of(new BigDecimal("5000")), Money.of(new BigDecimal("22500")));
        RateBoundsSnapshot bounds = new RateBoundsSnapshot(snapshot, snapshot);
        List<AmortizationRow> schedule = List.of(new AmortizationRow(
            1, Money.of(new BigDecimal("500000")), Money.of(new BigDecimal("2291.67")),
            Money.of(new BigDecimal("1138.79")), monthlyPayment, Money.of(new BigDecimal("498861.21"))));

        when(creditRateRepository.findActiveByCspCode(CspCode.SALA)).thenReturn(Optional.of(creditRate));
        when(rateStrategyFactory.getStrategy(CspCode.SALA)).thenReturn(cspStrategy);
        when(cspStrategy.calculateRate(creditRate)).thenReturn(Rate.of(new BigDecimal("5.25")));
        when(calculator.computeBounds(any(), any(), any(), anyInt(), any(), anyBoolean())).thenReturn(bounds);
        when(calculator.computeAtRate(any(), any(), any(), anyInt(), any(), anyBoolean())).thenReturn(snapshot);
        when(calculator.calculateTaeg(any(), any(), anyInt())).thenReturn(new BigDecimal("5.74"));
        when(calculator.computeSchedule(any(), any(), any(), anyInt())).thenReturn(schedule);

        FinancingResult result = strategy.simulate(request, bamParameters, Money.of(new BigDecimal("20000")));

        assertEquals(0, result.applicableRate().value().compareTo(new BigDecimal("5.25")));
        assertEquals(0, result.rateMin().value().compareTo(new BigDecimal("4.5")));
        assertEquals(0, result.rateMax().value().compareTo(new BigDecimal("6.0")));
        assertEquals(snapshot, result.applicableSnapshot());
        assertEquals(bounds, result.bounds());
        assertNull(result.mourabahaSnapshot());
        assertEquals(schedule, result.amortizationSchedule());
        assertNull(result.mourabahaAmortizationSchedule());
        assertEquals(0, result.taeg().compareTo(new BigDecimal("5.74")));
    }

    @Test
    void shouldThrowSimulationExceptionWhenCspNotFound() {
        when(creditRateRepository.findActiveByCspCode(CspCode.SALA)).thenReturn(Optional.empty());

        assertThrows(SimulationException.class,
            () -> strategy.simulate(request, bamParameters, Money.of(new BigDecimal("20000"))));
    }

    @Test
    void shouldThrowBAMExceptionWhenDurationOutOfRange() {
        SimulationRequest shortRequest = SimulationRequest.getBuilder()
            .amount(Money.of(new BigDecimal("500000")))
            .duration(12)
            .cspCode(CspCode.SALA)
            .nationalityCode("MA")
            .moroccanResident(true)
            .propertyValue(Money.of(new BigDecimal("600000")))
            .monthlyIncome(Money.of(new BigDecimal("20000")))
            .build();

        when(creditRateRepository.findActiveByCspCode(CspCode.SALA)).thenReturn(Optional.of(creditRate));

        assertThrows(BAMException.class,
            () -> strategy.simulate(shortRequest, bamParameters, Money.of(new BigDecimal("20000"))));
    }

    @Test
    void shouldThrowBAMExceptionWhenDepositInsufficient() {
        SimulationRequest noDepositRequest = SimulationRequest.getBuilder()
            .amount(Money.of(new BigDecimal("580000")))
            .duration(240)
            .cspCode(CspCode.SALA)
            .nationalityCode("MA")
            .moroccanResident(true)
            .propertyValue(Money.of(new BigDecimal("600000")))
            .monthlyIncome(Money.of(new BigDecimal("20000")))
            .build();

        when(creditRateRepository.findActiveByCspCode(CspCode.SALA)).thenReturn(Optional.of(creditRate));

        BAMException ex = assertThrows(BAMException.class,
            () -> strategy.simulate(noDepositRequest, bamParameters, Money.of(new BigDecimal("20000"))));
        assertEquals(BAMException.BAMExceptionType.DEPOSIT_INSUFFICIENT, ex.getType());
    }

    @Test
    void shouldThrowBAMExceptionWhenDebtRatioExceeded() {
        Money monthlyPayment = Money.of(new BigDecimal("3430.46"));
        FinancialSnapshot snapshot = new FinancialSnapshot(
            monthlyPayment, Money.of(BigDecimal.ZERO), monthlyPayment,
            Money.of(new BigDecimal("323310.40")), Money.of(BigDecimal.ZERO),
            Money.of(new BigDecimal("823310.40")), Money.of(new BigDecimal("5000")),
            Money.of(new BigDecimal("7500")), Money.of(new BigDecimal("5000")),
            Money.of(new BigDecimal("5000")), Money.of(new BigDecimal("22500")));
        RateBoundsSnapshot bounds = new RateBoundsSnapshot(snapshot, snapshot);

        when(creditRateRepository.findActiveByCspCode(CspCode.SALA)).thenReturn(Optional.of(creditRate));
        when(rateStrategyFactory.getStrategy(CspCode.SALA)).thenReturn(cspStrategy);
        when(cspStrategy.calculateRate(creditRate)).thenReturn(Rate.of(new BigDecimal("5.25")));
        when(calculator.computeBounds(any(), any(), any(), anyInt(), any(), anyBoolean())).thenReturn(bounds);
        when(calculator.computeAtRate(any(), any(), any(), anyInt(), any(), anyBoolean())).thenReturn(snapshot);

        BAMException ex = assertThrows(BAMException.class,
            () -> strategy.simulate(request, bamParameters, Money.of(new BigDecimal("5000"))));
        assertEquals(BAMException.BAMExceptionType.DEBT_RATIO_EXCEEDED, ex.getType());
    }
}
