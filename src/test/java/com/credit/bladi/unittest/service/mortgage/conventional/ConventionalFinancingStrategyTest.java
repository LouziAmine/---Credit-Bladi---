package com.credit.bladi.unittest.service.mortgage.conventional;

import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_1000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_10000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_20000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_22500;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_5000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_500000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_580000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_600000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_7500;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_01;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_015;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_40;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_90;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_1138_79;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_2291_67;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_323310_40;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_3430_46;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_498861_21;
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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import com.credit.bladi.exception.BAMException;
import com.credit.bladi.exception.MortgageCreditException;
import com.credit.bladi.model.bam.BamParametersModel;
import com.credit.bladi.model.creditrate.CreditRateModel;
import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.model.enums.ProductType;
import com.credit.bladi.model.enums.RateFormula;
import com.credit.bladi.model.mortgage.FinancialSnapshot;
import com.credit.bladi.model.mortgage.MortgageAmortizationSnapshot;
import com.credit.bladi.model.mortgage.RateBoundsSnapshot;
import com.credit.bladi.model.mortgage.request.MortgageCreditRequest;
import com.credit.bladi.model.vo.AmortizationRow;
import com.credit.bladi.model.vo.DurationRange;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;
import com.credit.bladi.model.vo.Ratio;
import com.credit.bladi.model.workflow.DemandModel;
import com.credit.bladi.repository.creditrate.ICreditRateRepository;
import com.credit.bladi.service.mortgage.conventional.ConventionalFinancingStrategy;
import com.credit.bladi.service.mortgage.conventional.MortgageCreditCalculator;
import com.credit.bladi.service.mortgage.strategy.ConventionalFinancingResult;
import com.credit.bladi.service.mortgage.strategy.FinancingResult;
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
class ConventionalFinancingStrategyTest {
    @Mock
    private ICreditRateRepository creditRateRepository;

    @Mock
    private MortgageCreditCalculator calculator;

    private ConventionalFinancingStrategy strategy;

    private CreditRateModel creditRate;
    private BamParametersModel bamParameters;
    private MortgageCreditRequest request;

    @BeforeEach
    void setUp() {
        strategy = new ConventionalFinancingStrategy(creditRateRepository, calculator);

        creditRate = CreditRateModel.getBuilder()
            .id(1L)
            .cspCode(CspCode.SALA)
            .rateMin(Rate.of(DECIMAL_4_5))
            .rateMax(Rate.of(DECIMAL_6_0))
            .rateFormula(RateFormula.AVERAGE)
            .durationRange(DurationRange.of(60, 300))
            .active(true)
            .build();

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
            .age(35)
            .build();
    }

    @Test
    void getProductTypeShouldReturnConventional() {
        assertEquals(ProductType.CONVENTIONAL, strategy.getProductType());
    }

    @Test
    void shouldReturnFinancingResultForEligibleRequest() {
        Money monthlyPayment = Money.of(DECIMAL_3430_46);
        FinancialSnapshot snapshot = new FinancialSnapshot(
            monthlyPayment, Money.of(BigDecimal.ZERO), monthlyPayment,
            Money.of(DECIMAL_323310_40), Money.of(BigDecimal.ZERO),
            Money.of(DECIMAL_823310_40), Money.of(AMOUNT_5000),
            Money.of(AMOUNT_7500), Money.of(AMOUNT_5000),
            Money.of(AMOUNT_5000), Money.of(AMOUNT_22500));
        RateBoundsSnapshot bounds = new RateBoundsSnapshot(snapshot, snapshot);
        List<AmortizationRow> schedule = List.of(new AmortizationRow(
            1, Money.of(AMOUNT_500000), Money.of(DECIMAL_2291_67),
            Money.of(DECIMAL_1138_79), monthlyPayment, Money.of(DECIMAL_498861_21)));

        when(creditRateRepository.findActiveByCspCode(CspCode.SALA)).thenReturn(Optional.of(creditRate));
        when(calculator.computeBounds(any(), any(), any(), anyInt(), any(), anyBoolean())).thenReturn(bounds);
        when(calculator.computeAtRate(any(), any(), any(), anyInt(), any(), anyBoolean())).thenReturn(snapshot);
        when(calculator.calculateTaeg(any(), any(), anyInt())).thenReturn(DECIMAL_5_74);
        when(calculator.computeSchedule(any(), any(), any(), anyInt())).thenReturn(schedule);

        FinancingResult financingResult = strategy.simulate(request, bamParameters, Money.of(AMOUNT_20000));

        ConventionalFinancingResult result = assertInstanceOf(ConventionalFinancingResult.class, financingResult);
        assertEquals(0, result.applicableRate().value().compareTo(DECIMAL_5_25));
        assertEquals(0, result.rateMin().value().compareTo(DECIMAL_4_5));
        assertEquals(0, result.rateMax().value().compareTo(DECIMAL_6_0));
        assertEquals(snapshot, result.applicableSnapshot());
        assertEquals(bounds, result.bounds());
        assertEquals(schedule, result.amortizationSchedule());
        assertEquals(0, result.taeg().compareTo(DECIMAL_5_74));
    }

    @Test
    void shouldThrowSimulationExceptionWhenCspNotFound() {
        when(creditRateRepository.findActiveByCspCode(CspCode.SALA)).thenReturn(Optional.empty());

        assertThrows(MortgageCreditException.class,
            () -> strategy.simulate(request, bamParameters, Money.of(AMOUNT_20000)));
    }

    @Test
    void shouldThrowBAMExceptionWhenDurationOutOfRange() {
        MortgageCreditRequest shortRequest = MortgageCreditRequest.getBuilder()
            .amount(Money.of(AMOUNT_500000))
            .duration(12)
            .cspCode(CspCode.SALA)
            .nationalityCode(CODE_MA)
            .moroccanResident(true)
            .propertyValue(Money.of(AMOUNT_600000))
            .monthlyIncome(Money.of(AMOUNT_20000))
            .build();

        when(creditRateRepository.findActiveByCspCode(CspCode.SALA)).thenReturn(Optional.of(creditRate));

        assertThrows(BAMException.class,
            () -> strategy.simulate(shortRequest, bamParameters, Money.of(AMOUNT_20000)));
    }

    @Test
    void shouldThrowBAMExceptionWhenDepositInsufficient() {
        MortgageCreditRequest noDepositRequest = MortgageCreditRequest.getBuilder()
            .amount(Money.of(AMOUNT_580000))
            .duration(240)
            .cspCode(CspCode.SALA)
            .nationalityCode(CODE_MA)
            .moroccanResident(true)
            .propertyValue(Money.of(AMOUNT_600000))
            .monthlyIncome(Money.of(AMOUNT_20000))
            .build();

        when(creditRateRepository.findActiveByCspCode(CspCode.SALA)).thenReturn(Optional.of(creditRate));

        BAMException ex = assertThrows(BAMException.class,
            () -> strategy.simulate(noDepositRequest, bamParameters, Money.of(AMOUNT_20000)));
        assertEquals(BAMException.BAMExceptionType.DEPOSIT_INSUFFICIENT, ex.getType());
    }

    @Test
    void shouldThrowBAMExceptionWhenDebtRatioExceeded() {
        Money monthlyPayment = Money.of(DECIMAL_3430_46);
        FinancialSnapshot snapshot = new FinancialSnapshot(
            monthlyPayment, Money.of(BigDecimal.ZERO), monthlyPayment,
            Money.of(DECIMAL_323310_40), Money.of(BigDecimal.ZERO),
            Money.of(DECIMAL_823310_40), Money.of(AMOUNT_5000),
            Money.of(AMOUNT_7500), Money.of(AMOUNT_5000),
            Money.of(AMOUNT_5000), Money.of(AMOUNT_22500));
        RateBoundsSnapshot bounds = new RateBoundsSnapshot(snapshot, snapshot);

        when(creditRateRepository.findActiveByCspCode(CspCode.SALA)).thenReturn(Optional.of(creditRate));
        when(calculator.computeBounds(any(), any(), any(), anyInt(), any(), anyBoolean())).thenReturn(bounds);
        when(calculator.computeAtRate(any(), any(), any(), anyInt(), any(), anyBoolean())).thenReturn(snapshot);

        BAMException ex = assertThrows(BAMException.class,
            () -> strategy.simulate(request, bamParameters, Money.of(AMOUNT_5000)));
        assertEquals(BAMException.BAMExceptionType.DEBT_RATIO_EXCEEDED, ex.getType());
    }

    @Test
    void shouldThrowBAMExceptionWhenAgeAtMaturityExceedsLimit() {
        MortgageCreditRequest oldApplicantRequest = MortgageCreditRequest.getBuilder()
            .amount(Money.of(AMOUNT_500000))
            .duration(240)
            .cspCode(CspCode.SALA)
            .nationalityCode(CODE_MA)
            .moroccanResident(true)
            .propertyValue(Money.of(AMOUNT_600000))
            .monthlyIncome(Money.of(AMOUNT_20000))
            .age(60)
            .build();

        when(creditRateRepository.findActiveByCspCode(CspCode.SALA)).thenReturn(Optional.of(creditRate));

        BAMException ex = assertThrows(BAMException.class,
            () -> strategy.simulate(oldApplicantRequest, bamParameters, Money.of(AMOUNT_20000)));
        assertEquals(BAMException.BAMExceptionType.AGE_LIMIT_EXCEEDED, ex.getType());
    }

    @Test
    void computeAmortizationScheduleShouldUseDemandsApplicableRateWhenNoOverride() {
        DemandModel demand = MockDemand.mortgage(1L, UUID.randomUUID(), REFERENCE_DEM_MTG12345, 1L, PERSON_KARIM_BENALI,
            Money.of(AMOUNT_500000), 240, Instant.now(), null, Rate.of(DECIMAL_5_25),
            EMAIL_KARIM_AT_EXAMPLE_MA, PHONE_0612345678, null, ProductType.CONVENTIONAL, CspCode.SALA,
            Money.of(AMOUNT_600000), Money.of(AMOUNT_20000), 35, false, null);
        Money monthlyPayment = Money.of(DECIMAL_3430_46);
        FinancialSnapshot snapshot = new FinancialSnapshot(
            monthlyPayment, Money.of(BigDecimal.ZERO), monthlyPayment,
            Money.of(DECIMAL_323310_40), Money.of(BigDecimal.ZERO),
            Money.of(DECIMAL_823310_40), Money.of(AMOUNT_5000),
            Money.of(AMOUNT_7500), Money.of(AMOUNT_5000),
            Money.of(AMOUNT_5000), Money.of(AMOUNT_22500));
        when(calculator.computeAtRate(any(), any(), any(), anyInt(), any(), anyBoolean())).thenReturn(snapshot);
        when(calculator.computeSchedule(any(), any(), any(), anyInt())).thenReturn(List.of());
        when(calculator.calculateTaeg(any(), any(), anyInt())).thenReturn(DECIMAL_5_74);

        MortgageAmortizationSnapshot result = strategy.computeAmortizationSchedule(demand,
            Money.of(AMOUNT_500000), 240, null, Money.of(AMOUNT_600000), bamParameters, false);

        assertEquals(ProductType.CONVENTIONAL, result.productType());
        assertEquals(0, result.applicableRate().value().compareTo(DECIMAL_5_25));
        assertEquals(0, result.taeg().compareTo(DECIMAL_5_74));
    }

    @Test
    void computeAmortizationScheduleShouldRejectWhenDemandHasNoApplicableRateAndNoOverride() {
        DemandModel demand = MockDemand.mortgage(1L, UUID.randomUUID(), REFERENCE_DEM_MTG12345, 1L, PERSON_KARIM_BENALI,
            Money.of(AMOUNT_500000), 240, Instant.now(), null, null, EMAIL_KARIM_AT_EXAMPLE_MA, PHONE_0612345678,
            null, ProductType.CONVENTIONAL, CspCode.SALA, Money.of(AMOUNT_600000),
            Money.of(AMOUNT_20000), 35, false, null);

        MortgageCreditException ex = assertThrows(MortgageCreditException.class,
            () -> strategy.computeAmortizationSchedule(demand, Money.of(AMOUNT_500000), 240, null,
                Money.of(AMOUNT_600000), bamParameters, false));

        assertEquals(MortgageCreditException.MortgageCreditExceptionType.DEMAND_RATE_NOT_SET, ex.getType());
    }
}
