package com.credit.bladi.unittest.service.simulation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.exception.BAMException;
import com.credit.bladi.exception.SimulationException;
import com.credit.bladi.model.bam.BamParametersModel;
import com.credit.bladi.model.enums.AuditAction;
import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.model.enums.ProductType;
import com.credit.bladi.model.simulation.FinancialSnapshot;
import com.credit.bladi.model.simulation.RateBoundsSnapshot;
import com.credit.bladi.model.simulation.request.SimulationRequest;
import com.credit.bladi.model.simulation.response.SimulationResponse;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;
import com.credit.bladi.model.vo.Ratio;
import com.credit.bladi.repository.bam.IBamParametersRepository;
import com.credit.bladi.service.audit.IAuditService;
import com.credit.bladi.service.security.ICurrentActorProvider;
import com.credit.bladi.service.simulation.SimulationService;
import com.credit.bladi.service.simulation.strategy.FinancingCalculationStrategy;
import com.credit.bladi.service.simulation.strategy.FinancingResult;
import com.credit.bladi.service.simulation.strategy.FinancingStrategyFactory;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SimulationServiceTest {

    @Mock
    private IBamParametersRepository bamParametersRepository;

    @Mock
    private FinancingStrategyFactory financingStrategyFactory;

    @Mock
    private FinancingCalculationStrategy strategy;

    @Mock
    private IAuditService auditService;

    @Mock
    private ICurrentActorProvider currentActorProvider;

    private SimulationService simulationService;

    private BamParametersModel bamParameters;
    private SimulationRequest request;

    @BeforeEach
    void setUp() {
        simulationService = new SimulationService(bamParametersRepository, financingStrategyFactory,
            auditService, currentActorProvider);

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
    void shouldReturnValidResponseForConventionalRequest() {
        Money monthlyPayment = Money.of(new BigDecimal("3430.46"));
        FinancialSnapshot snapshot = new FinancialSnapshot(
            monthlyPayment,
            Money.of(BigDecimal.ZERO),
            monthlyPayment,
            Money.of(new BigDecimal("323310.40")),
            Money.of(BigDecimal.ZERO),
            Money.of(new BigDecimal("823310.40")),
            Money.of(new BigDecimal("5000")),
            Money.of(new BigDecimal("7500")),
            Money.of(new BigDecimal("5000")),
            Money.of(new BigDecimal("5000")),
            Money.of(new BigDecimal("22500"))
        );
        RateBoundsSnapshot bounds = new RateBoundsSnapshot(snapshot, snapshot);
        FinancingResult result = new FinancingResult(
            Rate.of(new BigDecimal("5.25")), Rate.of(new BigDecimal("4.5")), Rate.of(new BigDecimal("6.0")),
            snapshot, bounds, null, null, null, new BigDecimal("5.74"));

        when(bamParametersRepository.getActiveParameters()).thenReturn(Optional.of(bamParameters));
        when(financingStrategyFactory.getStrategy(ProductType.CONVENTIONAL)).thenReturn(strategy);
        when(strategy.simulate(any(), any(), any())).thenReturn(result);

        SimulationResponse response = simulationService.simulate(request);

        assertNotNull(response);
        assertEquals(ProductType.CONVENTIONAL, response.getProductType());
        assertEquals(CspCode.SALA, response.getCspCode());
        assertEquals(0, response.getApplicableRate().value().compareTo(new BigDecimal("5.25")));
        assertEquals(0, response.getAmount().amount().compareTo(new BigDecimal("500000")));
        verify(auditService).log(any(), eq(AuditAction.SIMULATION), any());
    }

    @Test
    void shouldIncludeCoBorrowerIncomeWhenCoBorrowerPresent() {
        SimulationRequest coBorrowerRequest = SimulationRequest.getBuilder()
            .amount(Money.of(new BigDecimal("500000")))
            .duration(240)
            .cspCode(CspCode.SALA)
            .nationalityCode("MA")
            .moroccanResident(true)
            .propertyValue(Money.of(new BigDecimal("600000")))
            .monthlyIncome(Money.of(new BigDecimal("20000")))
            .coBorrower(true)
            .coBorrowerMonthlyIncome(Money.of(new BigDecimal("5000")))
            .build();
        Money monthlyPayment = Money.of(new BigDecimal("3430.46"));
        FinancialSnapshot snapshot = new FinancialSnapshot(
            monthlyPayment, Money.of(BigDecimal.ZERO), monthlyPayment,
            Money.of(new BigDecimal("323310.40")), Money.of(BigDecimal.ZERO),
            Money.of(new BigDecimal("823310.40")), Money.of(new BigDecimal("5000")),
            Money.of(new BigDecimal("7500")), Money.of(new BigDecimal("5000")),
            Money.of(new BigDecimal("5000")), Money.of(new BigDecimal("22500")));
        RateBoundsSnapshot bounds = new RateBoundsSnapshot(snapshot, snapshot);
        FinancingResult result = new FinancingResult(
            Rate.of(new BigDecimal("5.25")), Rate.of(new BigDecimal("4.5")), Rate.of(new BigDecimal("6.0")),
            snapshot, bounds, null, null, null, new BigDecimal("5.74"));

        when(bamParametersRepository.getActiveParameters()).thenReturn(Optional.of(bamParameters));
        when(financingStrategyFactory.getStrategy(ProductType.CONVENTIONAL)).thenReturn(strategy);
        when(strategy.simulate(any(), any(), any())).thenReturn(result);

        SimulationResponse response = simulationService.simulate(coBorrowerRequest);

        assertEquals(0, response.getTotalMonthlyIncome().amount().compareTo(new BigDecimal("25000")));
    }

    @Test
    void shouldThrowSimulationExceptionWhenBamParametersMissing() {
        when(bamParametersRepository.getActiveParameters()).thenReturn(Optional.empty());

        assertThrows(SimulationException.class, () -> simulationService.simulate(request));
    }

    @Test
    void shouldAuditRejectionAndPropagateBAMExceptionFromStrategy() {
        when(bamParametersRepository.getActiveParameters()).thenReturn(Optional.of(bamParameters));
        when(financingStrategyFactory.getStrategy(ProductType.CONVENTIONAL)).thenReturn(strategy);
        when(strategy.simulate(any(), any(), any()))
            .thenThrow(BAMException.of(BAMException.BAMExceptionType.DEPOSIT_INSUFFICIENT, new BigDecimal("0.90")));

        BAMException ex = assertThrows(BAMException.class, () -> simulationService.simulate(request));

        assertEquals(BAMException.BAMExceptionType.DEPOSIT_INSUFFICIENT, ex.getType());
        verify(auditService).log(any(), eq(AuditAction.SIMULATION_REJECTED), any());
    }
}
