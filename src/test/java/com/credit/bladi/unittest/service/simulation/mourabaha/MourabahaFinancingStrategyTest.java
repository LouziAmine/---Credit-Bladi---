package com.credit.bladi.unittest.service.simulation.mourabaha;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.credit.bladi.exception.BAMException;
import com.credit.bladi.exception.SimulationException;
import com.credit.bladi.model.bam.BamParametersModel;
import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.model.enums.ProductType;
import com.credit.bladi.model.mourabaha.MourabahaParametersModel;
import com.credit.bladi.model.simulation.request.SimulationRequest;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;
import com.credit.bladi.model.vo.Ratio;
import com.credit.bladi.repository.mourabaha.IMourabahaParametersRepository;
import com.credit.bladi.service.simulation.engine.RateCalculationEngine;
import com.credit.bladi.service.simulation.engine.TaegCalculationEngine;
import com.credit.bladi.service.simulation.mourabaha.MourabahaCalculationEngine;
import com.credit.bladi.service.simulation.mourabaha.MourabahaFinancingStrategy;
import com.credit.bladi.service.simulation.strategy.FinancingResult;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MourabahaFinancingStrategyTest {

    @Mock
    private IMourabahaParametersRepository mourabahaParametersRepository;

    private MourabahaFinancingStrategy strategy;

    private BamParametersModel bamParameters;
    private MourabahaParametersModel mourabahaParameters;
    private SimulationRequest request;

    @BeforeEach
    void setUp() {
        strategy = new MourabahaFinancingStrategy(mourabahaParametersRepository,
            new MourabahaCalculationEngine(new RateCalculationEngine()), new TaegCalculationEngine());

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
            .maxAgeAtMaturity(75)
            .active(true)
            .build();

        mourabahaParameters = MourabahaParametersModel.getBuilder()
            .id(1L)
            .profitRate(Rate.of(new BigDecimal("5.25")))
            .vatRate(Ratio.of(new BigDecimal("0.10")))
            .active(true)
            .build();

        request = SimulationRequest.getBuilder()
            .amount(Money.of(new BigDecimal("240000")))
            .duration(60)
            .cspCode(CspCode.SALA)
            .nationalityCode("MA")
            .moroccanResident(true)
            .propertyValue(Money.of(new BigDecimal("300000")))
            .monthlyIncome(Money.of(new BigDecimal("20000")))
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

        FinancingResult result = strategy.simulate(request, bamParameters, Money.of(new BigDecimal("20000")));

        assertEquals(0, result.applicableRate().value().compareTo(new BigDecimal("5.25")));
        assertNull(result.rateMin());
        assertNull(result.rateMax());
        assertNull(result.applicableSnapshot());
        assertNull(result.bounds());
        assertEquals(0, result.mourabahaSnapshot().marginHt().amount().compareTo(new BigDecimal("33534.55")));
        assertEquals(0, result.mourabahaSnapshot().marginTtc().amount().compareTo(new BigDecimal("36888.00")));
        assertEquals(0,
            result.mourabahaSnapshot().monthlyPaymentTtc().amount().compareTo(new BigDecimal("4614.80")));
        assertNull(result.amortizationSchedule());
        assertEquals(60, result.mourabahaAmortizationSchedule().size());
        assertEquals(0, result.mourabahaAmortizationSchedule().get(59).endingBalance().amount()
            .compareTo(new BigDecimal("0.00")));
    }

    @Test
    void shouldThrowSimulationExceptionWhenMourabahaParametersMissing() {
        when(mourabahaParametersRepository.getActiveParameters()).thenReturn(Optional.empty());

        assertThrows(SimulationException.class,
            () -> strategy.simulate(request, bamParameters, Money.of(new BigDecimal("20000"))));
    }

    @Test
    void shouldThrowBAMExceptionWhenDepositInsufficient() {
        SimulationRequest noDepositRequest = SimulationRequest.getBuilder()
            .amount(Money.of(new BigDecimal("280000")))
            .duration(60)
            .cspCode(CspCode.SALA)
            .nationalityCode("MA")
            .moroccanResident(true)
            .propertyValue(Money.of(new BigDecimal("300000")))
            .monthlyIncome(Money.of(new BigDecimal("20000")))
            .age(40)
            .build();

        when(mourabahaParametersRepository.getActiveParameters()).thenReturn(Optional.of(mourabahaParameters));

        BAMException ex = assertThrows(BAMException.class,
            () -> strategy.simulate(noDepositRequest, bamParameters, Money.of(new BigDecimal("20000"))));
        assertEquals(BAMException.BAMExceptionType.DEPOSIT_INSUFFICIENT, ex.getType());
    }

    @Test
    void shouldThrowBAMExceptionWhenDebtRatioExceeded() {
        when(mourabahaParametersRepository.getActiveParameters()).thenReturn(Optional.of(mourabahaParameters));

        BAMException ex = assertThrows(BAMException.class,
            () -> strategy.simulate(request, bamParameters, Money.of(new BigDecimal("5000"))));
        assertEquals(BAMException.BAMExceptionType.DEBT_RATIO_EXCEEDED, ex.getType());
    }
}
