package com.credit.bladi.unittest.service.simulation.mourabaha;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.model.bam.BamParametersModel;
import com.credit.bladi.model.mourabaha.MourabahaAmortizationRow;
import com.credit.bladi.model.mourabaha.MourabahaFinancialSnapshot;
import com.credit.bladi.model.mourabaha.MourabahaParametersModel;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;
import com.credit.bladi.model.vo.Ratio;
import com.credit.bladi.service.simulation.engine.RateCalculationEngine;
import com.credit.bladi.service.simulation.mourabaha.MourabahaCalculationEngine;
import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MourabahaCalculationEngineTest {

    private MourabahaCalculationEngine engine;
    private BamParametersModel bamParameters;
    private MourabahaParametersModel mourabahaParameters;

    @BeforeEach
    void setUp() {
        engine = new MourabahaCalculationEngine(new RateCalculationEngine());

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

        mourabahaParameters = MourabahaParametersModel.getBuilder()
            .id(1L)
            .profitRate(Rate.of(new BigDecimal("5.25")))
            .vatRate(Ratio.of(new BigDecimal("0.10")))
            .active(true)
            .build();
    }

    @Test
    void shouldMatchScreenshotFor240kOver60Months() {
        MourabahaFinancialSnapshot snapshot = engine.compute(
            Money.of(new BigDecimal("240000")), Money.of(new BigDecimal("240000")),
            mourabahaParameters, 60, bamParameters);

        assertEquals(0, snapshot.marginHt().amount().compareTo(new BigDecimal("33534.55")));
        assertEquals(0, snapshot.vatOnMargin().amount().compareTo(new BigDecimal("3353.45")));
        assertEquals(0, snapshot.marginTtc().amount().compareTo(new BigDecimal("36888.00")));
        assertEquals(0, snapshot.monthlyPaymentTtc().amount().compareTo(new BigDecimal("4614.80")));
    }

    @Test
    void shouldMatchScreenshotFor360kOver120Months() {
        MourabahaFinancialSnapshot snapshot = engine.compute(
            Money.of(new BigDecimal("360000")), Money.of(new BigDecimal("360000")),
            mourabahaParameters, 120, bamParameters);

        assertEquals(0, snapshot.marginHt().amount().compareTo(new BigDecimal("104310.55")));
        assertEquals(0, snapshot.vatOnMargin().amount().compareTo(new BigDecimal("10431.05")));
        assertEquals(0, snapshot.marginTtc().amount().compareTo(new BigDecimal("114741.60")));
        assertEquals(0, snapshot.monthlyPaymentTtc().amount().compareTo(new BigDecimal("3956.18")));
    }

    @Test
    void shouldGenerateScheduleReconcilingWithScreenshotFor240kOver60Months() {
        Money capital = Money.of(new BigDecimal("240000"));

        List<MourabahaAmortizationRow> schedule = engine.computeSchedule(capital, mourabahaParameters, 60);

        assertScheduleReconciles(schedule, capital, 60,
            new BigDecimal("33534.55"), new BigDecimal("3353.45"), new BigDecimal("36888.00"));
    }

    @Test
    void shouldGenerateScheduleReconcilingWithScreenshotFor360kOver120Months() {
        Money capital = Money.of(new BigDecimal("360000"));

        List<MourabahaAmortizationRow> schedule = engine.computeSchedule(capital, mourabahaParameters, 120);

        assertScheduleReconciles(schedule, capital, 120,
            new BigDecimal("104310.55"), new BigDecimal("10431.05"), new BigDecimal("114741.60"));
    }

    private void assertScheduleReconciles(List<MourabahaAmortizationRow> schedule, Money capital, int months,
                                          BigDecimal expectedMarginHt, BigDecimal expectedVatOnMargin,
                                          BigDecimal expectedMarginTtc) {
        assertEquals(months, schedule.size());

        MourabahaAmortizationRow first = schedule.get(0);
        assertEquals(1, first.installmentNumber());
        assertEquals(0, first.beginningBalance().amount().compareTo(capital.amount()));

        MourabahaAmortizationRow last = schedule.get(months - 1);
        assertEquals(months, last.installmentNumber());
        assertEquals(0, last.endingBalance().amount().compareTo(new BigDecimal("0.00")));

        BigDecimal tolerance = new BigDecimal("1.00");
        assertTrue(sumOf(schedule, MourabahaAmortizationRow::marginHt).subtract(expectedMarginHt)
            .abs().compareTo(tolerance) <= 0);
        assertTrue(sumOf(schedule, MourabahaAmortizationRow::vatOnMargin).subtract(expectedVatOnMargin)
            .abs().compareTo(tolerance) <= 0);
        assertTrue(sumOf(schedule, MourabahaAmortizationRow::marginTtc).subtract(expectedMarginTtc)
            .abs().compareTo(tolerance) <= 0);

        BigDecimal sumPrincipal = sumOf(schedule, MourabahaAmortizationRow::principal);
        assertEquals(0, sumPrincipal.compareTo(capital.amount()));
    }

    private static BigDecimal sumOf(List<MourabahaAmortizationRow> schedule,
                                    Function<MourabahaAmortizationRow, Money> extractor) {
        return schedule.stream()
            .map(extractor)
            .map(Money::amount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
