package com.credit.bladi.unittest.service.investment;

import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_10;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_1000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_1500000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_2;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_2000000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_40000000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_5000000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_55555555;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_8000000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_80000000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_99999999;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_0006;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_0075;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_4500;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_6000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_7000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_5_00;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_5_75;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_6_50;
import static com.credit.bladi.unittest.mock.data.MockCodes.REFERENCE_DEM_ABCDEF12;
import static com.credit.bladi.unittest.mock.data.MockContacts.COMPANY_ATLAS_TEXTILE_SARL;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.credit.bladi.exception.InvestmentCreditException;
import com.credit.bladi.exception.InvestmentCreditException.InvestmentCreditExceptionType;
import com.credit.bladi.exception.WorkflowException;
import com.credit.bladi.exception.WorkflowException.WorkflowExceptionType;
import com.credit.bladi.model.enums.EnterpriseType;
import com.credit.bladi.model.enums.KeepOrManualOption;
import com.credit.bladi.model.investment.InvestmentCreditParametersModel;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;
import com.credit.bladi.model.vo.Ratio;
import com.credit.bladi.model.workflow.DemandModel;
import com.credit.bladi.model.workflow.StepClosureDetails;
import com.credit.bladi.repository.investment.IInvestmentCreditParametersRepository;
import com.credit.bladi.service.calculation.RateCalculationEngine;
import com.credit.bladi.service.calculation.TaegCalculationEngine;
import com.credit.bladi.service.investment.InvestmentCreditCalculator;
import com.credit.bladi.service.investment.InvestmentDemandEligibilityValidator;
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
class InvestmentDemandEligibilityValidatorTest {
    @Mock
    private IInvestmentCreditParametersRepository parametersRepository;

    private InvestmentDemandEligibilityValidator validator;

    private InvestmentCreditParametersModel geParameters;

    @BeforeEach
    void setUp() {
        InvestmentCreditCalculator calculator = new InvestmentCreditCalculator(
            new RateCalculationEngine(), new TaegCalculationEngine());
        validator = new InvestmentDemandEligibilityValidator(parametersRepository, calculator);

        geParameters = InvestmentCreditParametersModel.getBuilder()
            .id(2L)
            .enterpriseType(EnterpriseType.GE)
            .rateMin(Rate.of(DECIMAL_5_00))
            .rateMax(Rate.of(DECIMAL_6_50))
            .minCompanyAgeYears(3)
            .maxFinancingToRevenueRatio(Ratio.of(DECIMAL_0_7000))
            .minGuaranteeCoverageRatio(Ratio.of(DECIMAL_0_6000))
            .maxDebtServiceRatio(Ratio.of(DECIMAL_0_4500))
            .processingFeesRatio(Ratio.of(DECIMAL_0_0075))
            .insurancePremiumRatio(Ratio.of(DECIMAL_0_0006))
            .active(true)
            .build();
    }

    private static DemandModel demandWithSnapshot(Money annualRevenue, Money existingAnnualDebtService,
            Money netAnnualResult, Money guaranteesValue) {
        return MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L, COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.GE,
            Money.of(AMOUNT_1500000), 84, Instant.now(), null, Rate.of(DECIMAL_5_75),
            null, null, annualRevenue, existingAnnualDebtService, netAnnualResult, guaranteesValue, null, null, null,
            null);
    }

    @Test
    void shouldSkipValidationWhenDemandHasNoEligibilitySnapshot() {
        DemandModel legacyDemand = demandWithSnapshot(null, null, null, null);
        when(parametersRepository.findActiveByEnterpriseType(EnterpriseType.GE))
            .thenReturn(Optional.of(geParameters));

        assertDoesNotThrow(() -> validator.assertEligible(legacyDemand, Money.of(AMOUNT_99999999),
            Rate.of(AMOUNT_2), 12));
    }

    @Test
    void shouldRejectManualAmountExceedingFinancingToRevenueRatio() {
        DemandModel demand = demandWithSnapshot(Money.of(AMOUNT_8000000),
            Money.of(BigDecimal.ZERO), Money.of(AMOUNT_5000000), Money.of(AMOUNT_40000000));
        when(parametersRepository.findActiveByEnterpriseType(EnterpriseType.GE))
            .thenReturn(Optional.of(geParameters));

        InvestmentCreditException ex = assertThrows(InvestmentCreditException.class,
            () -> validator.assertEligible(demand, Money.of(AMOUNT_55555555),
                Rate.of(AMOUNT_10), 40));

        assertEquals(InvestmentCreditExceptionType.FINANCING_RATIO_EXCEEDED, ex.getType());
    }

    @Test
    void shouldRejectManualAmountWithInsufficientGuaranteeCoverage() {
        DemandModel demand = demandWithSnapshot(Money.of(AMOUNT_80000000),
            Money.of(BigDecimal.ZERO), Money.of(AMOUNT_5000000), Money.of(AMOUNT_1000));
        when(parametersRepository.findActiveByEnterpriseType(EnterpriseType.GE))
            .thenReturn(Optional.of(geParameters));

        InvestmentCreditException ex = assertThrows(InvestmentCreditException.class,
            () -> validator.assertEligible(demand, Money.of(AMOUNT_2000000),
                Rate.of(DECIMAL_5_75), 60));

        assertEquals(InvestmentCreditExceptionType.GUARANTEE_COVERAGE_INSUFFICIENT, ex.getType());
    }

    @Test
    void shouldRejectManualAmountExceedingDebtServiceRatio() {
        DemandModel demand = demandWithSnapshot(Money.of(AMOUNT_80000000),
            Money.of(BigDecimal.ZERO), Money.of(AMOUNT_1000), Money.of(AMOUNT_40000000));
        when(parametersRepository.findActiveByEnterpriseType(EnterpriseType.GE))
            .thenReturn(Optional.of(geParameters));

        InvestmentCreditException ex = assertThrows(InvestmentCreditException.class,
            () -> validator.assertEligible(demand, Money.of(AMOUNT_2000000),
                Rate.of(DECIMAL_5_75), 60));

        assertEquals(InvestmentCreditExceptionType.DEBT_SERVICE_RATIO_EXCEEDED, ex.getType());
    }

    @Test
    void shouldAllowManualAmountWithinAllRatios() {
        DemandModel demand = demandWithSnapshot(Money.of(AMOUNT_8000000),
            Money.of(BigDecimal.ZERO), Money.of(AMOUNT_5000000), Money.of(AMOUNT_2000000));
        when(parametersRepository.findActiveByEnterpriseType(EnterpriseType.GE))
            .thenReturn(Optional.of(geParameters));

        assertDoesNotThrow(() -> validator.assertEligible(demand, Money.of(AMOUNT_2000000),
            Rate.of(DECIMAL_5_75), 60));
    }

    private static StepClosureDetails insuranceDetails(Boolean adiSubscribed, Boolean damageSubscribed) {
        return new StepClosureDetails(null, null, null, null, null, null, adiSubscribed, null, damageSubscribed,
            KeepOrManualOption.KEEP, null, KeepOrManualOption.KEEP, null, KeepOrManualOption.KEEP, null);
    }

    @Test
    void shouldRequireAdiInsuranceSubscription() {
        DemandModel demand = demandWithSnapshot(null, null, null, Money.of(AMOUNT_2000000));

        WorkflowException ex = assertThrows(WorkflowException.class,
            () -> validator.assertInsuranceValidated(insuranceDetails(null, null), demand));

        assertEquals(WorkflowExceptionType.INSURANCE_ADI_REQUIRED, ex.getType());
    }

    @Test
    void shouldRequireDamageInsuranceWhenGuaranteesAreReal() {
        DemandModel demand = demandWithSnapshot(null, null, null, Money.of(AMOUNT_2000000));

        WorkflowException ex = assertThrows(WorkflowException.class,
            () -> validator.assertInsuranceValidated(insuranceDetails(true, false), demand));

        assertEquals(WorkflowExceptionType.INSURANCE_DAMAGE_REQUIRED, ex.getType());
    }

    @Test
    void shouldNotRequireDamageInsuranceWhenThereAreNoRealGuarantees() {
        DemandModel demand = demandWithSnapshot(null, null, null, Money.of(BigDecimal.ZERO));

        assertDoesNotThrow(() -> validator.assertInsuranceValidated(insuranceDetails(true, false), demand));
    }

    @Test
    void shouldPassWhenBothAdiAndDamageInsuranceAreSubscribed() {
        DemandModel demand = demandWithSnapshot(null, null, null, Money.of(AMOUNT_2000000));

        assertDoesNotThrow(() -> validator.assertInsuranceValidated(insuranceDetails(true, true), demand));
    }
}
