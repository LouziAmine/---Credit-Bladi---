package com.credit.bladi.unittest.service.mortgage.mourabaha;

import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_1000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_10000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_20000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_240000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_280000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_300000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_5000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_00;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_0004;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_01;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_015;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_10;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_2000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_40;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_90;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_33534_55;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_36888_00;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_4614_80;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_4710_80;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_4_75;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_5_25;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_6_00;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_96_00;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_MA;
import static com.credit.bladi.unittest.mock.data.MockCodes.REFERENCE_DEM_MOURABA1;
import static com.credit.bladi.unittest.mock.data.MockContacts.PERSON_KARIM_BENALI;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_DECIDER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.credit.bladi.exception.BAMException;
import com.credit.bladi.exception.MortgageCreditException;
import com.credit.bladi.model.bam.BamParametersModel;
import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.model.enums.ProductType;
import com.credit.bladi.model.mortgage.MortgageAmortizationSnapshot;
import com.credit.bladi.model.mortgage.request.MortgageCreditRequest;
import com.credit.bladi.model.mourabaha.MourabahaParametersModel;
import com.credit.bladi.model.pricing.AgreedPricing;
import com.credit.bladi.model.pricing.AgreedPricing.MourabahaTerms;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;
import com.credit.bladi.model.vo.Ratio;
import com.credit.bladi.model.workflow.DemandModel;
import com.credit.bladi.repository.mourabaha.IMourabahaParametersRepository;
import com.credit.bladi.repository.pricing.IAgreedPricingRepository;
import com.credit.bladi.service.calculation.RateCalculationEngine;
import com.credit.bladi.service.calculation.TaegCalculationEngine;
import com.credit.bladi.service.mortgage.mourabaha.MourabahaCalculationEngine;
import com.credit.bladi.service.mortgage.mourabaha.MourabahaFinancingStrategy;
import com.credit.bladi.service.mortgage.strategy.FinancingResult;
import com.credit.bladi.service.mortgage.strategy.MourabahaFinancingResult;
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
class MourabahaFinancingStrategyTest {
    @Mock
    private IMourabahaParametersRepository mourabahaParametersRepository;

    @Mock
    private IAgreedPricingRepository agreedPricingRepository;

    private MourabahaFinancingStrategy strategy;

    private BamParametersModel bamParameters;
    private MourabahaParametersModel mourabahaParameters;
    private MortgageCreditRequest request;

    @BeforeEach
    void setUp() {
        strategy = new MourabahaFinancingStrategy(mourabahaParametersRepository,
            new MourabahaCalculationEngine(new RateCalculationEngine()), new TaegCalculationEngine(),
            agreedPricingRepository);

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
            .maxAgeAtMaturity(75)
            .active(true)
            .build();

        mourabahaParameters = MourabahaParametersModel.getBuilder()
            .id(1L)
            .profitRate(Rate.of(DECIMAL_5_25))
            .vatRate(Ratio.of(DECIMAL_0_10))
            .takafulMonthlyRate(Rate.of(DECIMAL_0_0004))
            .active(true)
            .build();

        request = MortgageCreditRequest.getBuilder()
            .amount(Money.of(AMOUNT_240000))
            .duration(60)
            .cspCode(CspCode.SALA)
            .nationalityCode(CODE_MA)
            .moroccanResident(true)
            .propertyValue(Money.of(AMOUNT_300000))
            .monthlyIncome(Money.of(AMOUNT_20000))
            .age(40)
            .build();
    }

    @Test
    void getProductTypeShouldReturnMourabaha() {
        assertEquals(ProductType.MOURABAHA, strategy.getProductType());
    }

    @Test
    void shouldReturnFinancingResultMatchingScreenshot() {
        when(mourabahaParametersRepository.getActiveParameters()).thenReturn(Optional.of(mourabahaParameters));

        FinancingResult financingResult = strategy.simulate(request, bamParameters, Money.of(AMOUNT_20000));

        MourabahaFinancingResult result = assertInstanceOf(MourabahaFinancingResult.class, financingResult);
        assertEquals(0, result.applicableRate().value().compareTo(DECIMAL_5_25));
        assertEquals(0, result.mourabahaSnapshot().marginHt().amount().compareTo(DECIMAL_33534_55));
        assertEquals(0, result.mourabahaSnapshot().marginTtc().amount().compareTo(DECIMAL_36888_00));
        assertEquals(0,
            result.mourabahaSnapshot().monthlyPaymentTtc().amount().compareTo(DECIMAL_4614_80));
        assertEquals(0,
            result.mourabahaSnapshot().takafulMonthlyContribution().amount().compareTo(DECIMAL_96_00));
        assertEquals(0,
            result.mourabahaSnapshot().allInMonthlyPaymentTtc().amount().compareTo(DECIMAL_4710_80));
        assertEquals(60, result.mourabahaAmortizationSchedule().size());
        assertEquals(0, result.mourabahaAmortizationSchedule().get(59).endingBalance().amount()
            .compareTo(DECIMAL_0_00));
    }

    @Test
    void shouldThrowSimulationExceptionWhenMourabahaParametersMissing() {
        when(mourabahaParametersRepository.getActiveParameters()).thenReturn(Optional.empty());

        assertThrows(MortgageCreditException.class,
            () -> strategy.simulate(request, bamParameters, Money.of(AMOUNT_20000)));
    }

    @Test
    void shouldThrowBAMExceptionWhenDepositInsufficient() {
        MortgageCreditRequest noDepositRequest = MortgageCreditRequest.getBuilder()
            .amount(Money.of(AMOUNT_280000))
            .duration(60)
            .cspCode(CspCode.SALA)
            .nationalityCode(CODE_MA)
            .moroccanResident(true)
            .propertyValue(Money.of(AMOUNT_300000))
            .monthlyIncome(Money.of(AMOUNT_20000))
            .age(40)
            .build();

        when(mourabahaParametersRepository.getActiveParameters()).thenReturn(Optional.of(mourabahaParameters));

        BAMException ex = assertThrows(BAMException.class,
            () -> strategy.simulate(noDepositRequest, bamParameters, Money.of(AMOUNT_20000)));
        assertEquals(BAMException.BAMExceptionType.DEPOSIT_INSUFFICIENT, ex.getType());
    }

    @Test
    void shouldThrowBAMExceptionWhenDebtRatioExceeded() {
        when(mourabahaParametersRepository.getActiveParameters()).thenReturn(Optional.of(mourabahaParameters));

        BAMException ex = assertThrows(BAMException.class,
            () -> strategy.simulate(request, bamParameters, Money.of(AMOUNT_5000)));
        assertEquals(BAMException.BAMExceptionType.DEBT_RATIO_EXCEEDED, ex.getType());
    }

    @Test
    void computeAmortizationScheduleShouldUseTheAgreedProfitRateOverTheCurrentActiveOne() {
        when(mourabahaParametersRepository.getActiveParameters()).thenReturn(Optional.of(mourabahaParameters));

        MortgageAmortizationSnapshot result = strategy.computeAmortizationSchedule(mourabahaDemand(),
            Money.of(AMOUNT_240000), 60, Rate.of(DECIMAL_6_00),
            Money.of(AMOUNT_300000), bamParameters, false);

        assertEquals(ProductType.MOURABAHA, result.productType());
        assertEquals(0, result.applicableRate().value().compareTo(DECIMAL_6_00));
        assertNull(result.conventional());
        assertNotNull(result.mourabaha());
    }

    @Test
    void computeAmortizationScheduleShouldFallBackToTheMarginFrozenOnTheDemand() {
        when(mourabahaParametersRepository.getActiveParameters()).thenReturn(Optional.of(mourabahaParameters));
        MortgageAmortizationSnapshot result = strategy.computeAmortizationSchedule(mourabahaDemand(),
            Money.of(AMOUNT_240000), 60, null, Money.of(AMOUNT_300000), bamParameters, false);

        assertEquals(0, result.applicableRate().value().compareTo(DECIMAL_4_75));
    }

    @Test
    void computeAmortizationScheduleShouldUseTheVatFrozenAtTheCommitteeDecision() {
        when(mourabahaParametersRepository.getActiveParameters()).thenReturn(Optional.of(mourabahaParameters));
        MortgageAmortizationSnapshot atDecision = strategy.computeAmortizationSchedule(mourabahaDemand(),
            Money.of(AMOUNT_240000), 60, null, Money.of(AMOUNT_300000), bamParameters, false);
        MourabahaParametersModel vatRaisedLater = MourabahaParametersModel.getBuilder()
            .id(mourabahaParameters.getId()).publicId(mourabahaParameters.getPublicId())
            .profitRate(mourabahaParameters.getProfitRate())
            .vatRate(Ratio.of(DECIMAL_0_2000))
            .takafulMonthlyRate(mourabahaParameters.getTakafulMonthlyRate())
            .active(true).build();
        when(mourabahaParametersRepository.getActiveParameters()).thenReturn(Optional.of(vatRaisedLater));
        when(agreedPricingRepository.findByDemandId(1L)).thenReturn(Optional.of(new AgreedPricing(1L, null,
            MourabahaTerms.of(mourabahaParameters), null, Instant.EPOCH, USER_DECIDER)));

        MortgageAmortizationSnapshot afterGridChange = strategy.computeAmortizationSchedule(mourabahaDemand(),
            Money.of(AMOUNT_240000), 60, null, Money.of(AMOUNT_300000), bamParameters, false);

        assertEquals(0, afterGridChange.mourabaha().allInMonthlyPaymentTtc().amount()
            .compareTo(atDecision.mourabaha().allInMonthlyPaymentTtc().amount()));
    }

    private static DemandModel mourabahaDemand() {
        return MockDemand.mortgage(1L, UUID.randomUUID(), REFERENCE_DEM_MOURABA1, 1L, PERSON_KARIM_BENALI,
            Money.of(AMOUNT_240000), 60, Instant.now(), null, Rate.of(DECIMAL_4_75), null,
            null, null, ProductType.MOURABAHA, CspCode.SALA, Money.of(AMOUNT_300000),
            Money.of(AMOUNT_20000), 40, false, null);
    }
}
