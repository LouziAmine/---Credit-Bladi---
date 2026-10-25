package com.credit.bladi.unittest.service.investment;

import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_10;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_10000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_100000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_1200000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_1300000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_1500000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_2000000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_500000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_5000000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_8000000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_0010;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_0100;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_4000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_5000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_8000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_14_00;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_6_00;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_6_75;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_7_50;
import static com.credit.bladi.unittest.mock.data.MockCodes.REFERENCE_DEM_ABCDEF12;
import static com.credit.bladi.unittest.mock.data.MockContacts.COMPANY_ATLAS_TEXTILE_SARL;
import static com.credit.bladi.unittest.mock.data.MockContacts.COMPANY_STARTUP_SARL;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_TEXTILE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.exception.InvestmentCreditException;
import com.credit.bladi.exception.InvestmentCreditException.InvestmentCreditExceptionType;
import com.credit.bladi.model.enums.AuditAction;
import com.credit.bladi.model.enums.EnterpriseType;
import com.credit.bladi.model.enums.RateFormula;
import com.credit.bladi.model.investment.InvestmentCreditParametersModel;
import com.credit.bladi.model.investment.InvestmentFinancialSnapshot;
import com.credit.bladi.model.investment.request.InvestmentCreditRequest;
import com.credit.bladi.model.investment.response.InvestmentCreditResponse;
import com.credit.bladi.model.parameters.RegulatoryLimits;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;
import com.credit.bladi.model.vo.Ratio;
import com.credit.bladi.model.workflow.DemandModel;
import com.credit.bladi.model.workflow.DemandSnapshot;
import com.credit.bladi.repository.investment.IInvestmentCreditParametersRepository;
import com.credit.bladi.repository.pricing.IAgreedPricingRepository;
import com.credit.bladi.service.audit.IAuditService;
import com.credit.bladi.service.calculation.RateCalculationEngine;
import com.credit.bladi.service.calculation.TaegCalculationEngine;
import com.credit.bladi.service.investment.InvestmentCreditCalculator;
import com.credit.bladi.service.investment.InvestmentCreditService;
import com.credit.bladi.service.request.IRequestContext;
import com.credit.bladi.service.security.ICurrentActorProvider;
import com.credit.bladi.service.workflow.engine.IWorkflowEngineService;
import com.credit.bladi.unittest.mock.workflow.MockDemand;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InvestmentCreditServiceTest {
    @Mock
    private IInvestmentCreditParametersRepository parametersRepository;

    @Mock
    private IAgreedPricingRepository agreedPricingRepository;

    @Mock
    private IWorkflowEngineService workflowEngineService;

    @Mock
    private IAuditService auditService;

    @Mock
    private ICurrentActorProvider currentActorProvider;

    @Mock
    private IRequestContext requestContext;

    private InvestmentCreditService investmentCreditService;

    private InvestmentCreditCalculator calculator;

    private InvestmentCreditParametersModel pmeParameters;

    private InvestmentCreditRequest eligibleRequest;

    @BeforeEach
    void setUp() {
        calculator = new InvestmentCreditCalculator(new RateCalculationEngine(), new TaegCalculationEngine());

        investmentCreditService = new InvestmentCreditService(
            parametersRepository, calculator, workflowEngineService,
            auditService, currentActorProvider, requestContext, agreedPricingRepository,
            new RegulatoryLimits(Rate.of(DECIMAL_14_00)));

        pmeParameters = InvestmentCreditParametersModel.getBuilder()
            .id(1L)
            .enterpriseType(EnterpriseType.PME)
            .rateMin(Rate.of(DECIMAL_6_00))
            .rateMax(Rate.of(DECIMAL_7_50))
            .rateFormula(RateFormula.AVERAGE)
            .minCompanyAgeYears(2)
            .maxFinancingToRevenueRatio(Ratio.of(DECIMAL_0_5000))
            .minGuaranteeCoverageRatio(Ratio.of(DECIMAL_0_8000))
            .maxDebtServiceRatio(Ratio.of(DECIMAL_0_4000))
            .processingFeesRatio(Ratio.of(DECIMAL_0_0100))
            .insurancePremiumRatio(Ratio.of(DECIMAL_0_0010))
            .active(true)
            .build();

        eligibleRequest = InvestmentCreditRequest.getBuilder()
            .companyName(COMPANY_ATLAS_TEXTILE_SARL)
            .enterpriseType(EnterpriseType.PME)
            .sector(LABEL_TEXTILE)
            .amount(Money.of(AMOUNT_1500000))
            .duration(84)
            .annualRevenue(Money.of(AMOUNT_8000000))
            .companyAgeYears(5)
            .existingAnnualDebtService(Money.of(AMOUNT_100000))
            .netAnnualResult(Money.of(AMOUNT_1200000))
            .guaranteesValue(Money.of(AMOUNT_1300000))
            .build();
    }

    @Test
    void shouldReturnResponseAndCreateDemandWhenEligible() {
        when(parametersRepository.findActiveByEnterpriseType(EnterpriseType.PME))
            .thenReturn(Optional.of(pmeParameters));
        DemandModel demand = MockDemand.enterprise(
            1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L, COMPANY_ATLAS_TEXTILE_SARL, EnterpriseType.PME,
            eligibleRequest.getAmount(), 84, Instant.now(), null, null, null, null, null, null, null, null, null,
            null, null, null);
        when(workflowEngineService.openDemandFromSimulation(any(DemandSnapshot.class))).thenReturn(demand);

        InvestmentCreditResponse response = investmentCreditService.simulate(eligibleRequest);

        assertNotNull(response);
        assertEquals(EnterpriseType.PME, response.enterpriseType());
        assertEquals(REFERENCE_DEM_ABCDEF12, response.demandReference());
        assertNotNull(response.snapshot());
        verify(auditService).log(any(), eq(AuditAction.ENTERPRISE_SIMULATION), any());
        verify(auditService, never()).log(any(), eq(AuditAction.ENTERPRISE_SIMULATION_REJECTED), any());
    }

    @Test
    void shouldPassSectorAndCompanyAgeYearsIntoTheDemandSnapshot() {
        when(parametersRepository.findActiveByEnterpriseType(EnterpriseType.PME))
            .thenReturn(Optional.of(pmeParameters));
        DemandModel demand = MockDemand.enterprise(
            1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L, COMPANY_ATLAS_TEXTILE_SARL, EnterpriseType.PME,
            eligibleRequest.getAmount(), 84, Instant.now(), null, null, null, null, null, null, null, null, null,
            null, null, null);
        when(workflowEngineService.openDemandFromSimulation(any(DemandSnapshot.class))).thenReturn(demand);

        investmentCreditService.simulate(eligibleRequest);

        org.mockito.ArgumentCaptor<DemandSnapshot> captor = org.mockito.ArgumentCaptor.forClass(DemandSnapshot.class);
        verify(workflowEngineService).openDemandFromSimulation(captor.capture());
        com.credit.bladi.model.workflow.EnterpriseDemandSnapshotDetail detail =
            (com.credit.bladi.model.workflow.EnterpriseDemandSnapshotDetail) captor.getValue().detail();
        assertEquals(LABEL_TEXTILE, detail.sector());
        assertEquals(5, detail.companyAgeYears());
    }

    @Test
    void shouldRejectWhenCompanyTooYoung() {
        InvestmentCreditRequest tooYoung = InvestmentCreditRequest.getBuilder()
            .companyName(COMPANY_STARTUP_SARL)
            .enterpriseType(EnterpriseType.PME)
            .amount(Money.of(AMOUNT_500000))
            .duration(60)
            .annualRevenue(Money.of(AMOUNT_2000000))
            .companyAgeYears(1)
            .netAnnualResult(Money.of(AMOUNT_500000))
            .guaranteesValue(Money.of(AMOUNT_500000))
            .build();
        when(parametersRepository.findActiveByEnterpriseType(EnterpriseType.PME))
            .thenReturn(Optional.of(pmeParameters));

        InvestmentCreditException ex = assertThrows(InvestmentCreditException.class,
            () -> investmentCreditService.simulate(tooYoung));

        assertEquals(InvestmentCreditExceptionType.COMPANY_AGE_INSUFFICIENT, ex.getType());
        verify(auditService).log(any(), eq(AuditAction.ENTERPRISE_SIMULATION_REJECTED), any());
        verify(workflowEngineService, never()).openDemandFromSimulation(any());
    }

    @Test
    void shouldRejectWhenFinancingExceedsRevenueRatio() {
        InvestmentCreditRequest tooMuch = InvestmentCreditRequest.getBuilder()
            .companyName(COMPANY_ATLAS_TEXTILE_SARL)
            .enterpriseType(EnterpriseType.PME)
            .amount(Money.of(AMOUNT_5000000))
            .duration(84)
            .annualRevenue(Money.of(AMOUNT_8000000))
            .companyAgeYears(5)
            .netAnnualResult(Money.of(AMOUNT_1200000))
            .guaranteesValue(Money.of(AMOUNT_5000000))
            .build();
        when(parametersRepository.findActiveByEnterpriseType(EnterpriseType.PME))
            .thenReturn(Optional.of(pmeParameters));

        InvestmentCreditException ex = assertThrows(InvestmentCreditException.class,
            () -> investmentCreditService.simulate(tooMuch));

        assertEquals(InvestmentCreditExceptionType.FINANCING_RATIO_EXCEEDED, ex.getType());
    }

    @Test
    void shouldRejectWhenGuaranteeCoverageInsufficient() {
        InvestmentCreditRequest lowGuarantee = InvestmentCreditRequest.getBuilder()
            .companyName(COMPANY_ATLAS_TEXTILE_SARL)
            .enterpriseType(EnterpriseType.PME)
            .amount(Money.of(AMOUNT_1500000))
            .duration(84)
            .annualRevenue(Money.of(AMOUNT_8000000))
            .companyAgeYears(5)
            .netAnnualResult(Money.of(AMOUNT_1200000))
            .guaranteesValue(Money.of(AMOUNT_100000))
            .build();
        when(parametersRepository.findActiveByEnterpriseType(EnterpriseType.PME))
            .thenReturn(Optional.of(pmeParameters));

        InvestmentCreditException ex = assertThrows(InvestmentCreditException.class,
            () -> investmentCreditService.simulate(lowGuarantee));

        assertEquals(InvestmentCreditExceptionType.GUARANTEE_COVERAGE_INSUFFICIENT, ex.getType());
    }

    @Test
    void shouldRejectWhenDebtServiceRatioExceeded() {
        InvestmentCreditRequest lowResult = InvestmentCreditRequest.getBuilder()
            .companyName(COMPANY_ATLAS_TEXTILE_SARL)
            .enterpriseType(EnterpriseType.PME)
            .amount(Money.of(AMOUNT_1500000))
            .duration(84)
            .annualRevenue(Money.of(AMOUNT_8000000))
            .companyAgeYears(5)
            .netAnnualResult(Money.of(AMOUNT_10000))
            .guaranteesValue(Money.of(AMOUNT_1300000))
            .build();
        when(parametersRepository.findActiveByEnterpriseType(EnterpriseType.PME))
            .thenReturn(Optional.of(pmeParameters));

        InvestmentCreditException ex = assertThrows(InvestmentCreditException.class,
            () -> investmentCreditService.simulate(lowResult));

        assertEquals(InvestmentCreditExceptionType.DEBT_SERVICE_RATIO_EXCEEDED, ex.getType());
        verify(workflowEngineService, never()).openDemandFromSimulation(any());
    }

    @Test
    void shouldComputeAmortizationScheduleUsingDemandsCurrentValuesWhenNoOverrideGiven() {
        DemandModel demand = MockDemand.enterprise(
            1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L, COMPANY_ATLAS_TEXTILE_SARL, EnterpriseType.PME,
            Money.of(AMOUNT_1500000), 84, Instant.now(), null, Rate.of(DECIMAL_6_75),
            null, null, null, null, null, null, null, null, null, null);
        when(workflowEngineService.getDemand(1L)).thenReturn(demand);
        when(parametersRepository.findActiveByEnterpriseType(EnterpriseType.PME))
            .thenReturn(Optional.of(pmeParameters));

        InvestmentFinancialSnapshot snapshot = investmentCreditService.getAmortizationSchedule(1L, null, null, null);

        assertNotNull(snapshot);
        assertEquals(0, Rate.of(DECIMAL_6_75).value().compareTo(snapshot.applicableRate().value()));
    }

    @Test
    void shouldComputeAmortizationScheduleUsingGivenOverridesInsteadOfDemandsStoredValues() {
        DemandModel demand = MockDemand.enterprise(
            1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L, COMPANY_ATLAS_TEXTILE_SARL, EnterpriseType.PME,
            Money.of(AMOUNT_1500000), 84, Instant.now(), null, Rate.of(DECIMAL_6_75),
            null, null, null, null, null, null, null, null, null, null);
        when(workflowEngineService.getDemand(1L)).thenReturn(demand);
        when(parametersRepository.findActiveByEnterpriseType(EnterpriseType.PME))
            .thenReturn(Optional.of(pmeParameters));

        InvestmentFinancialSnapshot overridden = investmentCreditService.getAmortizationSchedule(1L,
            Money.of(AMOUNT_2000000), Rate.of(AMOUNT_10), 60);
        InvestmentFinancialSnapshot current = investmentCreditService.getAmortizationSchedule(1L, null, null, null);

        assertEquals(0, Rate.of(AMOUNT_10).value().compareTo(overridden.applicableRate().value()));
        assertTrue(overridden.monthlyPayment().isGreaterThan(current.monthlyPayment()));
    }

    @Test
    void shouldThrowWhenNoRateAvailableAndNoOverrideGiven() {
        DemandModel demandWithoutRate = MockDemand.enterprise(
            1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L, COMPANY_ATLAS_TEXTILE_SARL, EnterpriseType.PME,
            Money.of(AMOUNT_1500000), 84, Instant.now(), null, null, null, null, null, null, null, null,
            null, null, null, null);
        when(workflowEngineService.getDemand(1L)).thenReturn(demandWithoutRate);

        InvestmentCreditException ex = assertThrows(InvestmentCreditException.class,
            () -> investmentCreditService.getAmortizationSchedule(1L, null, null, null));

        assertEquals(InvestmentCreditExceptionType.DEMAND_RATE_NOT_SET, ex.getType());
    }
}
