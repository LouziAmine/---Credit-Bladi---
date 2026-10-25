package com.credit.bladi.unittest.service.mortgage;

import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_1000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_10000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_500000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_001;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_01;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_015;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_40;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_90;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_4_5;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_5_25;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_6_0;
import static com.credit.bladi.unittest.mock.data.MockAmounts.TEXT_1000000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.TEXT_20000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.TEXT_4000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.TEXT_400000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.TEXT_5000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.TEXT_500000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.TEXT_6000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.TEXT_600000;
import static com.credit.bladi.unittest.mock.data.MockContacts.EMAIL_K_AT_X_MA;
import static com.credit.bladi.unittest.mock.data.MockContacts.PERSON_KARIM_BENALI;
import static com.credit.bladi.unittest.mock.data.MockContacts.PHONE_0600000000;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_DEM_MTG1;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.exception.BAMException;
import com.credit.bladi.exception.MortgageCreditException;
import com.credit.bladi.model.bam.BamParametersModel;
import com.credit.bladi.model.creditrate.CreditRateModel;
import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.model.enums.DemandType;
import com.credit.bladi.model.enums.ProductType;
import com.credit.bladi.model.enums.RateFormula;
import com.credit.bladi.model.vo.DurationRange;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;
import com.credit.bladi.model.vo.Ratio;
import com.credit.bladi.model.workflow.DemandModel;
import com.credit.bladi.repository.bam.IBamParametersRepository;
import com.credit.bladi.repository.creditrate.ICreditRateRepository;
import com.credit.bladi.service.calculation.RateCalculationEngine;
import com.credit.bladi.service.calculation.TaegCalculationEngine;
import com.credit.bladi.service.mortgage.MortgageDemandEligibilityValidator;
import com.credit.bladi.service.mortgage.conventional.ConventionalFinancingStrategy;
import com.credit.bladi.service.mortgage.conventional.MortgageCreditCalculator;
import com.credit.bladi.service.mortgage.strategy.FinancingCalculationStrategy;
import com.credit.bladi.service.mortgage.strategy.FinancingStrategyFactory;
import com.credit.bladi.unittest.mock.workflow.MockDemand;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MortgageDemandEligibilityValidatorTest {
    private static final Rate RATE = Rate.of(DECIMAL_5_25);

    @Mock
    private IBamParametersRepository bamParametersRepository;

    @Mock
    private ICreditRateRepository creditRateRepository;

    private FinancingCalculationStrategy mourabahaStrategy;

    private MortgageDemandEligibilityValidator validator;

    private BamParametersModel bamParameters;

    @BeforeEach
    void setUp() {
        ConventionalFinancingStrategy conventional = new ConventionalFinancingStrategy(creditRateRepository,
            new MortgageCreditCalculator(new RateCalculationEngine(), new TaegCalculationEngine()));
        mourabahaStrategy = mock(FinancingCalculationStrategy.class);
        when(mourabahaStrategy.getProductType()).thenReturn(ProductType.MOURABAHA);
        validator = new MortgageDemandEligibilityValidator(bamParametersRepository,
            new FinancingStrategyFactory(List.of(conventional, mourabahaStrategy)));
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
            .maxAgeAtMaturity(70)
            .adiMonthlyRate(Ratio.of(DECIMAL_0_001))
            .active(true)
            .build();
    }

    private void givenSalaRateGrid() {
        when(creditRateRepository.findActiveByCspCode(CspCode.SALA)).thenReturn(Optional.of(
            CreditRateModel.getBuilder()
                .id(1L)
                .cspCode(CspCode.SALA)
                .rateMin(Rate.of(DECIMAL_4_5))
                .rateMax(Rate.of(DECIMAL_6_0))
                .rateFormula(RateFormula.AVERAGE)
                .durationRange(DurationRange.of(12, 240))
                .active(true)
                .build()));
    }

    private static DemandModel mortgageDemand(ProductType productType, Integer age, Money propertyValue,
            Money monthlyIncome, Boolean coBorrower, Money coBorrowerMonthlyIncome) {
        return MockDemand.mortgage(1L, UUID.randomUUID(), LABEL_DEM_MTG1, 1L, PERSON_KARIM_BENALI,
            Money.of(AMOUNT_500000), 240, Instant.now(), null, null, EMAIL_K_AT_X_MA, PHONE_0600000000, null,
            productType, CspCode.SALA, propertyValue, monthlyIncome, age, coBorrower, coBorrowerMonthlyIncome);
    }

    private static DemandModel mortgageDemand(Integer age, Money propertyValue, Money monthlyIncome,
            Boolean coBorrower, Money coBorrowerMonthlyIncome) {
        return mortgageDemand(ProductType.CONVENTIONAL, age, propertyValue, monthlyIncome, coBorrower,
            coBorrowerMonthlyIncome);
    }

    private static Money mad(String amount) {
        return Money.of(new BigDecimal(amount));
    }

    @Test
    void shouldSupportMortgageDemandType() {
        assertEquals(DemandType.MORTGAGE, validator.supportedType());
    }

    @Test
    void shouldThrowWhenBamParametersNotConfigured() {
        DemandModel demand = mortgageDemand(40, null, null, false, null);
        when(bamParametersRepository.getActiveParameters()).thenReturn(Optional.empty());

        MortgageCreditException exception = assertThrows(MortgageCreditException.class,
            () -> validator.assertEligible(demand, mad(TEXT_500000), RATE, 240));

        assertEquals(MortgageCreditException.MortgageCreditExceptionType.BAM_PARAMETERS_NOT_FOUND,
            exception.getType());
    }

    @Test
    void shouldThrowDurationLimitExceededWhenOutsideCspRange() {
        DemandModel demand = mortgageDemand(40, null, null, false, null);
        when(bamParametersRepository.getActiveParameters()).thenReturn(Optional.of(bamParameters));
        givenSalaRateGrid();

        BAMException exception = assertThrows(BAMException.class,
            () -> validator.assertEligible(demand, mad(TEXT_500000), RATE, 300));

        assertEquals(BAMException.BAMExceptionType.DURATION_LIMIT_EXCEEDED, exception.getType());
    }

    @Test
    void shouldThrowAgeLimitExceededWhenAgeAtMaturityTooHigh() {
        DemandModel demand = mortgageDemand(60, null, null, false, null);
        when(bamParametersRepository.getActiveParameters()).thenReturn(Optional.of(bamParameters));
        givenSalaRateGrid();

        BAMException exception = assertThrows(BAMException.class,
            () -> validator.assertEligible(demand, mad(TEXT_500000), RATE, 240));

        assertEquals(BAMException.BAMExceptionType.AGE_LIMIT_EXCEEDED, exception.getType());
    }

    @Test
    void shouldThrowDepositInsufficientWhenLoanExceedsLtv() {
        DemandModel demand = mortgageDemand(null, mad(TEXT_600000), null, false, null);
        when(bamParametersRepository.getActiveParameters()).thenReturn(Optional.of(bamParameters));
        givenSalaRateGrid();

        BAMException exception = assertThrows(BAMException.class,
            () -> validator.assertEligible(demand, mad(TEXT_600000), RATE, 240));

        assertEquals(BAMException.BAMExceptionType.DEPOSIT_INSUFFICIENT, exception.getType());
    }

    @Test
    void shouldThrowDebtRatioExceededWhenAllInPaymentTooHigh() {
        DemandModel demand = mortgageDemand(null, mad(TEXT_1000000), mad(TEXT_5000), false, null);
        when(bamParametersRepository.getActiveParameters()).thenReturn(Optional.of(bamParameters));
        givenSalaRateGrid();

        BAMException exception = assertThrows(BAMException.class,
            () -> validator.assertEligible(demand, mad(TEXT_500000), RATE, 240));

        assertEquals(BAMException.BAMExceptionType.DEBT_RATIO_EXCEEDED, exception.getType());
    }

    @Test
    void shouldPassOnlyThanksToCombinedCoBorrowerIncome() {
        DemandModel alone = mortgageDemand(40, mad(TEXT_1000000), mad(TEXT_6000), false, null);
        DemandModel withCoBorrower = mortgageDemand(40, mad(TEXT_1000000), mad(TEXT_6000), true, mad(TEXT_4000));
        when(bamParametersRepository.getActiveParameters()).thenReturn(Optional.of(bamParameters));
        givenSalaRateGrid();

        assertThrows(BAMException.class, () -> validator.assertEligible(alone, mad(TEXT_400000), RATE, 240));
        assertDoesNotThrow(() -> validator.assertEligible(withCoBorrower, mad(TEXT_400000), RATE, 240));
    }

    @Test
    void shouldSkipOptionalChecksWhenDetailFieldsAreNull() {
        DemandModel demand = mortgageDemand(null, null, null, false, null);
        when(bamParametersRepository.getActiveParameters()).thenReturn(Optional.of(bamParameters));
        givenSalaRateGrid();

        assertDoesNotThrow(() -> validator.assertEligible(demand, mad(TEXT_500000), RATE, 240));
    }

    @Test
    void shouldDelegateMourabahaDemandToMourabahaStrategy() {
        DemandModel demand = mortgageDemand(ProductType.MOURABAHA, 40, mad(TEXT_1000000), mad(TEXT_20000), false, null);
        when(bamParametersRepository.getActiveParameters()).thenReturn(Optional.of(bamParameters));

        validator.assertEligible(demand, mad(TEXT_500000), RATE, 240);

        verify(mourabahaStrategy).assertEligible(any(), any(), any(), anyInt(), any());
    }
}
