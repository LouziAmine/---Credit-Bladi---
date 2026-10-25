package com.credit.bladi.unittest.service.mortgage.mourabaha;

import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_1000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_10000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_240000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_360000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_00;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_0004;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_01;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_015;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_10;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_40;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_90;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_104310_55;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_10431_05;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_114741_60;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_11520_00;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_144_00;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_17280_00;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_192_00;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_1_00;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_33534_55;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_3353_45;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_36888_00;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_3956_18;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_4100_18;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_4614_80;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_4710_80;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_4806_80;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_5760_00;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_5_25;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_96_00;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.model.bam.BamParametersModel;
import com.credit.bladi.model.mourabaha.MourabahaAmortizationRow;
import com.credit.bladi.model.mourabaha.MourabahaFinancialSnapshot;
import com.credit.bladi.model.mourabaha.MourabahaParametersModel;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;
import com.credit.bladi.model.vo.Ratio;
import com.credit.bladi.service.calculation.RateCalculationEngine;
import com.credit.bladi.service.mortgage.mourabaha.MourabahaCalculationEngine;
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

        mourabahaParameters = MourabahaParametersModel.getBuilder()
            .id(1L)
            .profitRate(Rate.of(DECIMAL_5_25))
            .vatRate(Ratio.of(DECIMAL_0_10))
            .takafulMonthlyRate(Rate.of(DECIMAL_0_0004))
            .active(true)
            .build();
    }

    @Test
    void shouldMatchScreenshotFor240kOver60Months() {
        MourabahaFinancialSnapshot snapshot = engine.compute(
            Money.of(AMOUNT_240000), Money.of(AMOUNT_240000),
            mourabahaParameters, 60, bamParameters, false);

        assertEquals(0, snapshot.marginHt().amount().compareTo(DECIMAL_33534_55));
        assertEquals(0, snapshot.vatOnMargin().amount().compareTo(DECIMAL_3353_45));
        assertEquals(0, snapshot.marginTtc().amount().compareTo(DECIMAL_36888_00));
        assertEquals(0, snapshot.monthlyPaymentTtc().amount().compareTo(DECIMAL_4614_80));
        assertEquals(0, snapshot.takafulMonthlyContribution().amount().compareTo(DECIMAL_96_00));
        assertEquals(0, snapshot.allInMonthlyPaymentTtc().amount().compareTo(DECIMAL_4710_80));
        assertEquals(0, snapshot.totalTakafulCost().amount().compareTo(DECIMAL_5760_00));
    }

    @Test
    void shouldDoubleTakafulContributionForCoBorrower() {
        MourabahaFinancialSnapshot snapshot = engine.compute(
            Money.of(AMOUNT_240000), Money.of(AMOUNT_240000),
            mourabahaParameters, 60, bamParameters, true);

        assertEquals(0, snapshot.takafulMonthlyContribution().amount().compareTo(DECIMAL_192_00));
        assertEquals(0, snapshot.allInMonthlyPaymentTtc().amount().compareTo(DECIMAL_4806_80));
        assertEquals(0, snapshot.totalTakafulCost().amount().compareTo(DECIMAL_11520_00));
    }

    @Test
    void shouldMatchScreenshotFor360kOver120Months() {
        MourabahaFinancialSnapshot snapshot = engine.compute(
            Money.of(AMOUNT_360000), Money.of(AMOUNT_360000),
            mourabahaParameters, 120, bamParameters, false);

        assertEquals(0, snapshot.marginHt().amount().compareTo(DECIMAL_104310_55));
        assertEquals(0, snapshot.vatOnMargin().amount().compareTo(DECIMAL_10431_05));
        assertEquals(0, snapshot.marginTtc().amount().compareTo(DECIMAL_114741_60));
        assertEquals(0, snapshot.monthlyPaymentTtc().amount().compareTo(DECIMAL_3956_18));
        assertEquals(0, snapshot.takafulMonthlyContribution().amount().compareTo(DECIMAL_144_00));
        assertEquals(0, snapshot.allInMonthlyPaymentTtc().amount().compareTo(DECIMAL_4100_18));
        assertEquals(0, snapshot.totalTakafulCost().amount().compareTo(DECIMAL_17280_00));
    }

    @Test
    void shouldGenerateScheduleReconcilingWithScreenshotFor240kOver60Months() {
        Money capital = Money.of(AMOUNT_240000);

        List<MourabahaAmortizationRow> schedule = engine.computeSchedule(capital, mourabahaParameters, 60);

        assertScheduleReconciles(schedule, capital, 60,
            DECIMAL_33534_55, DECIMAL_3353_45, DECIMAL_36888_00);
    }

    @Test
    void shouldGenerateScheduleReconcilingWithScreenshotFor360kOver120Months() {
        Money capital = Money.of(AMOUNT_360000);

        List<MourabahaAmortizationRow> schedule = engine.computeSchedule(capital, mourabahaParameters, 120);

        assertScheduleReconciles(schedule, capital, 120,
            DECIMAL_104310_55, DECIMAL_10431_05, DECIMAL_114741_60);
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
        assertEquals(0, last.endingBalance().amount().compareTo(DECIMAL_0_00));

        BigDecimal tolerance = DECIMAL_1_00;
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
