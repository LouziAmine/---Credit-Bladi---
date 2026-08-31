package com.credit.bladi.unittest.service.mortgage;

import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_1000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_10000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_20000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_22500;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_25000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_5000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_500000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_600000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_7500;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_01;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_015;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_40;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_90;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_14_00;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_323310_40;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_3430_46;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_4_5;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_5_25;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_5_74;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_6_0;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_823310_40;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_MA;
import static com.credit.bladi.unittest.mock.data.MockCodes.REFERENCE_DEM_MTG12345;
import static com.credit.bladi.unittest.mock.data.MockContacts.EMAIL_KARIM_AT_EXAMPLE_MA;
import static com.credit.bladi.unittest.mock.data.MockContacts.PERSON_KARIM_BENALI;
import static com.credit.bladi.unittest.mock.data.MockContacts.PHONE_0612345678;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.exception.BAMException;
import com.credit.bladi.exception.MortgageCreditException;
import com.credit.bladi.model.bam.BamParametersModel;
import com.credit.bladi.model.enums.AuditAction;
import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.model.enums.EnterpriseType;
import com.credit.bladi.model.enums.ProductType;
import com.credit.bladi.model.mortgage.FinancialSnapshot;
import com.credit.bladi.model.mortgage.MortgageAmortizationSnapshot;
import com.credit.bladi.model.mortgage.RateBoundsSnapshot;
import com.credit.bladi.model.mortgage.request.MortgageCreditRequest;
import com.credit.bladi.model.mortgage.response.MortgageCreditResponse;
import com.credit.bladi.model.parameters.RegulatoryLimits;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;
import com.credit.bladi.model.vo.Ratio;
import com.credit.bladi.model.workflow.DemandModel;
import com.credit.bladi.model.workflow.DemandSnapshot;
import com.credit.bladi.repository.bam.IBamParametersRepository;
import com.credit.bladi.repository.pricing.IAgreedPricingRepository;
import com.credit.bladi.service.audit.IAuditService;
import com.credit.bladi.service.mortgage.MortgageCreditService;
import com.credit.bladi.service.mortgage.strategy.ConventionalFinancingResult;
import com.credit.bladi.service.mortgage.strategy.FinancingCalculationStrategy;
import com.credit.bladi.service.mortgage.strategy.FinancingResult;
import com.credit.bladi.service.mortgage.strategy.FinancingStrategyFactory;
import com.credit.bladi.service.request.IRequestContext;
import com.credit.bladi.service.security.ICurrentActorProvider;
import com.credit.bladi.service.workflow.engine.IWorkflowEngineService;
import com.credit.bladi.unittest.mock.workflow.MockDemand;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MortgageCreditServiceTest {
    @Mock
    private IBamParametersRepository bamParametersRepository;

    @Mock
    private IAgreedPricingRepository agreedPricingRepository;

    @Mock
    private FinancingStrategyFactory financingStrategyFactory;

    @Mock
    private FinancingCalculationStrategy strategy;

    @Mock
    private IAuditService auditService;

    @Mock
    private ICurrentActorProvider currentActorProvider;

    @Mock
    private IWorkflowEngineService workflowEngineService;

    @Mock
    private IRequestContext requestContext;

    private MortgageCreditService simulationService;

    private BamParametersModel bamParameters;
    private MortgageCreditRequest request;

    @BeforeEach
    void setUp() {
        simulationService = new MortgageCreditService(bamParametersRepository, financingStrategyFactory,
            auditService, currentActorProvider, workflowEngineService, requestContext, agreedPricingRepository,
            new RegulatoryLimits(Rate.of(DECIMAL_14_00)));

        DemandModel demand = MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_MTG12345, 1L,
            PERSON_KARIM_BENALI,
            EnterpriseType.PME, Money.of(AMOUNT_500000), 240, Instant.now(), null,
            Rate.of(DECIMAL_5_25), null, null, null, null, null, null, null, null, null, null);
        lenient().when(workflowEngineService.openDemandFromSimulation(any(DemandSnapshot.class))).thenReturn(demand);

        bamParameters = BamParametersModel.getBuilder()
            .id(1L)
            .processingFeesRatio(Ratio.of(DECIMAL_0_01))
            .minProcessingFees(Money.of(AMOUNT_1000))
            .maxProcessingFees(Money.of(AMOUNT_10000))
            .registrationFeeRatio(Ratio.of(DECIMAL_0_01))
            .landConservationRatio(Ratio.of(DECIMAL_0_015))
            .notaryFeeRatio(Ratio.of(DECIMAL_0_01))
            .ltvRatio(Ratio.of(DECIMAL_0_90))
            .maxDebtRatio(Ratio.of(DECIMAL_0_40))
            .active(true)
            .build();

        request = MortgageCreditRequest.getBuilder()
            .amount(Money.of(AMOUNT_500000))
            .duration(240)
            .cspCode(CspCode.SALA)
            .nationalityCode(CODE_MA)
            .moroccanResident(true)
            .propertyValue(Money.of(AMOUNT_600000))
            .monthlyIncome(Money.of(AMOUNT_20000))
            .build();
    }

    @Test
    void shouldReturnValidResponseForConventionalRequest() {
        Money monthlyPayment = Money.of(DECIMAL_3430_46);
        FinancialSnapshot snapshot = new FinancialSnapshot(
            monthlyPayment,
            Money.of(BigDecimal.ZERO),
            monthlyPayment,
            Money.of(DECIMAL_323310_40),
            Money.of(BigDecimal.ZERO),
            Money.of(DECIMAL_823310_40),
            Money.of(AMOUNT_5000),
            Money.of(AMOUNT_7500),
            Money.of(AMOUNT_5000),
            Money.of(AMOUNT_5000),
            Money.of(AMOUNT_22500)
        );
        RateBoundsSnapshot bounds = new RateBoundsSnapshot(snapshot, snapshot);
        FinancingResult result = new ConventionalFinancingResult(
            Rate.of(DECIMAL_5_25), Rate.of(DECIMAL_4_5), Rate.of(DECIMAL_6_0),
            snapshot, bounds, null, DECIMAL_5_74);

        when(bamParametersRepository.getActiveParameters()).thenReturn(Optional.of(bamParameters));
        when(financingStrategyFactory.getStrategy(ProductType.CONVENTIONAL)).thenReturn(strategy);
        when(strategy.simulate(any(), any(), any())).thenReturn(result);

        MortgageCreditResponse response = simulationService.simulate(request);

        assertNotNull(response);
        assertEquals(ProductType.CONVENTIONAL, response.productType());
        assertEquals(CspCode.SALA, response.cspCode());
        assertEquals(0, response.applicableRate().value().compareTo(DECIMAL_5_25));
        assertEquals(0, response.amount().amount().compareTo(AMOUNT_500000));
        verify(auditService).log(any(), eq(AuditAction.SIMULATION), any());
    }

    @Test
    void shouldIncludeCoBorrowerIncomeWhenCoBorrowerPresent() {
        MortgageCreditRequest coBorrowerRequest = MortgageCreditRequest.getBuilder()
            .amount(Money.of(AMOUNT_500000))
            .duration(240)
            .cspCode(CspCode.SALA)
            .nationalityCode(CODE_MA)
            .moroccanResident(true)
            .propertyValue(Money.of(AMOUNT_600000))
            .monthlyIncome(Money.of(AMOUNT_20000))
            .coBorrower(true)
            .coBorrowerMonthlyIncome(Money.of(AMOUNT_5000))
            .build();
        Money monthlyPayment = Money.of(DECIMAL_3430_46);
        FinancialSnapshot snapshot = new FinancialSnapshot(
            monthlyPayment, Money.of(BigDecimal.ZERO), monthlyPayment,
            Money.of(DECIMAL_323310_40), Money.of(BigDecimal.ZERO),
            Money.of(DECIMAL_823310_40), Money.of(AMOUNT_5000),
            Money.of(AMOUNT_7500), Money.of(AMOUNT_5000),
            Money.of(AMOUNT_5000), Money.of(AMOUNT_22500));
        RateBoundsSnapshot bounds = new RateBoundsSnapshot(snapshot, snapshot);
        FinancingResult result = new ConventionalFinancingResult(
            Rate.of(DECIMAL_5_25), Rate.of(DECIMAL_4_5), Rate.of(DECIMAL_6_0),
            snapshot, bounds, null, DECIMAL_5_74);

        when(bamParametersRepository.getActiveParameters()).thenReturn(Optional.of(bamParameters));
        when(financingStrategyFactory.getStrategy(ProductType.CONVENTIONAL)).thenReturn(strategy);
        when(strategy.simulate(any(), any(), any())).thenReturn(result);

        MortgageCreditResponse response = simulationService.simulate(coBorrowerRequest);

        assertEquals(0, response.totalMonthlyIncome().amount().compareTo(AMOUNT_25000));
    }

    @Test
    void shouldThrowSimulationExceptionWhenBamParametersMissing() {
        when(bamParametersRepository.getActiveParameters()).thenReturn(Optional.empty());

        assertThrows(MortgageCreditException.class, () -> simulationService.simulate(request));
    }

    @Test
    void shouldAuditRejectionAndPropagateBAMExceptionFromStrategy() {
        when(bamParametersRepository.getActiveParameters()).thenReturn(Optional.of(bamParameters));
        when(financingStrategyFactory.getStrategy(ProductType.CONVENTIONAL)).thenReturn(strategy);
        when(strategy.simulate(any(), any(), any()))
            .thenThrow(BAMException.of(BAMException.BAMExceptionType.DEPOSIT_INSUFFICIENT, DECIMAL_0_90));

        BAMException ex = assertThrows(BAMException.class, () -> simulationService.simulate(request));

        assertEquals(BAMException.BAMExceptionType.DEPOSIT_INSUFFICIENT, ex.getType());
        verify(auditService).log(any(), eq(AuditAction.SIMULATION_REJECTED), any());
    }

    @Test
    void getAmortizationScheduleShouldResolveStrategyFromDemandsProductTypeAndDelegate() {
        DemandModel demand = MockDemand.mortgage(1L, UUID.randomUUID(), REFERENCE_DEM_MTG12345, 1L, PERSON_KARIM_BENALI,
            Money.of(AMOUNT_500000), 240, Instant.now(), null, Rate.of(DECIMAL_5_25),
            EMAIL_KARIM_AT_EXAMPLE_MA, PHONE_0612345678, null, ProductType.CONVENTIONAL, CspCode.SALA,
            Money.of(AMOUNT_600000), Money.of(AMOUNT_20000), 35, false, null);
        when(workflowEngineService.getDemand(1L)).thenReturn(demand);
        when(bamParametersRepository.getActiveParameters()).thenReturn(Optional.of(bamParameters));
        when(financingStrategyFactory.getStrategy(ProductType.CONVENTIONAL)).thenReturn(strategy);
        MortgageAmortizationSnapshot expected = new MortgageAmortizationSnapshot(ProductType.CONVENTIONAL,
            Rate.of(DECIMAL_5_25), DECIMAL_5_74, null, null, null, null);
        when(strategy.computeAmortizationSchedule(demand, Money.of(AMOUNT_500000), 240, null,
            Money.of(AMOUNT_600000), bamParameters, false)).thenReturn(expected);

        MortgageAmortizationSnapshot result = simulationService.getAmortizationSchedule(1L, null, null, null);

        assertEquals(expected, result);
    }

    @Test
    void getAmortizationScheduleShouldPropagateExceptionFromStrategy() {
        DemandModel demand = MockDemand.mortgage(1L, UUID.randomUUID(), REFERENCE_DEM_MTG12345, 1L, PERSON_KARIM_BENALI,
            Money.of(AMOUNT_500000), 240, Instant.now(), null, null, EMAIL_KARIM_AT_EXAMPLE_MA, PHONE_0612345678,
            null, ProductType.CONVENTIONAL, CspCode.SALA, Money.of(AMOUNT_600000),
            Money.of(AMOUNT_20000), 35, false, null);
        when(workflowEngineService.getDemand(1L)).thenReturn(demand);
        when(bamParametersRepository.getActiveParameters()).thenReturn(Optional.of(bamParameters));
        when(financingStrategyFactory.getStrategy(ProductType.CONVENTIONAL)).thenReturn(strategy);
        when(strategy.computeAmortizationSchedule(any(), any(), eq(240), any(), any(), any(), eq(false)))
            .thenThrow(MortgageCreditException.of(MortgageCreditException.MortgageCreditExceptionType
                .DEMAND_RATE_NOT_SET));

        MortgageCreditException ex = assertThrows(MortgageCreditException.class,
            () -> simulationService.getAmortizationSchedule(1L, null, null, null));

        assertEquals(MortgageCreditException.MortgageCreditExceptionType.DEMAND_RATE_NOT_SET, ex.getType());
    }
}
