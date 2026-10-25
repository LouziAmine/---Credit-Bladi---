package com.credit.bladi.unittest.model;

import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_20000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_500000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_3_00;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_3_50;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_4_80;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_5_00;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_5_10;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_5_50;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_6_00;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_9_99;
import static com.credit.bladi.unittest.mock.data.MockAmounts.TEXT_1000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.TEXT_2000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.TEXT_20000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.TEXT_25000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.TEXT_2800;
import static com.credit.bladi.unittest.mock.data.MockAmounts.TEXT_3200;
import static com.credit.bladi.unittest.mock.data.MockAmounts.TEXT_500000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.TEXT_600000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.TEXT_9999;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_FR;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_MA;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_OTHER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.model.enums.ProductType;
import com.credit.bladi.model.mortgage.FinancialSnapshot;
import com.credit.bladi.model.mortgage.RateBoundsSnapshot;
import com.credit.bladi.model.mortgage.response.MortgageCreditResponse;
import com.credit.bladi.model.mourabaha.MourabahaAmortizationRow;
import com.credit.bladi.model.mourabaha.MourabahaFinancialSnapshot;
import com.credit.bladi.model.vo.AmortizationRow;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class MortgageCreditResponseTest {
    private static Money money(String v) {
        return Money.of(new BigDecimal(v));
    }

    private static AmortizationRow amortizationRow(String amount) {
        Money m = money(amount);
        return new AmortizationRow(1, m, m, m, m, m);
    }

    private static MourabahaAmortizationRow mourabahaRow(String amount) {
        Money m = money(amount);
        return new MourabahaAmortizationRow(1, m, m, m, m, m, m, m);
    }

    private static FinancialSnapshot snapshot(String amount) {
        Money m = money(amount);
        return new FinancialSnapshot(m, m, m, m, m, m, m, m, m, m, m);
    }

    private static MourabahaFinancialSnapshot mourabahaSnapshot(String amount) {
        Money m = money(amount);
        return new MourabahaFinancialSnapshot(m, m, m, m, m, m, m, m, m, m, m, m, m);
    }

    private static MortgageCreditResponse.Builder fullBuilder() {
        return MortgageCreditResponse.getBuilder()
            .amount(money(TEXT_500000))
            .duration(240)
            .productType(ProductType.CONVENTIONAL)
            .cspCode(CspCode.SALA)
            .nationalityCode(CODE_MA)
            .moroccanResident(true)
            .age(35)
            .totalMonthlyIncome(money(TEXT_20000))
            .applicableRate(Rate.of(DECIMAL_4_80))
            .rateMin(Rate.of(DECIMAL_3_50))
            .rateMax(Rate.of(DECIMAL_5_50))
            .applicable(snapshot(TEXT_2800))
            .bounds(new RateBoundsSnapshot(snapshot(TEXT_2800), snapshot(TEXT_3200)))
            .mourabahaSnapshot(mourabahaSnapshot(TEXT_2800))
            .amortizationSchedule(List.of(amortizationRow(TEXT_1000)))
            .mourabahaAmortizationSchedule(List.of(mourabahaRow(TEXT_1000)))
            .taeg(DECIMAL_5_10);
    }

    @Test
    void shouldExposeAllGetters() {
        MortgageCreditResponse resp = fullBuilder().build();

        assertEquals(0, resp.amount().amount().compareTo(AMOUNT_500000));
        assertEquals(240, resp.duration());
        assertEquals(ProductType.CONVENTIONAL, resp.productType());
        assertEquals(CspCode.SALA, resp.cspCode());
        assertEquals(CODE_MA, resp.nationalityCode());
        assertTrue(resp.moroccanResident());
        assertEquals(35, resp.age());
        assertEquals(0, resp.totalMonthlyIncome().amount().compareTo(AMOUNT_20000));
        assertEquals(0, resp.applicableRate().value().compareTo(DECIMAL_4_80));
        assertEquals(0, resp.rateMin().value().compareTo(DECIMAL_3_50));
        assertEquals(0, resp.rateMax().value().compareTo(DECIMAL_5_50));
        assertEquals(snapshot(TEXT_2800), resp.applicable());
        assertEquals(1, resp.amortizationSchedule().size());
        assertEquals(1, resp.mourabahaAmortizationSchedule().size());
        assertEquals(0, resp.taeg().compareTo(DECIMAL_5_10));
        assertTrue(resp.bounds().min().equals(snapshot(TEXT_2800)));
        assertTrue(resp.mourabahaSnapshot().equals(mourabahaSnapshot(TEXT_2800)));
    }

    @Test
    void shouldBeEqualToItself() {
        MortgageCreditResponse resp = fullBuilder().build();
        assertTrue(resp.equals(resp));
    }

    @Test
    void shouldNotBeEqualToDifferentType() {
        MortgageCreditResponse resp = fullBuilder().build();
        assertFalse(resp.equals(USER_OTHER));
    }

    @Test
    void shouldBeEqualWhenAllFieldsMatch() {
        assertEquals(fullBuilder().build(), fullBuilder().build());
    }

    @Test
    void shouldHaveSameHashCodeWhenAllFieldsMatch() {
        assertEquals(fullBuilder().build().hashCode(), fullBuilder().build().hashCode());
    }

    @Test
    void shouldBeEqualWhenAllFieldsAreNull() {
        MortgageCreditResponse r1 = MortgageCreditResponse.getBuilder().build();
        MortgageCreditResponse r2 = MortgageCreditResponse.getBuilder().build();
        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenAmountDiffers() {
        assertFalse(fullBuilder().build().equals(fullBuilder().amount(money(TEXT_600000)).build()));
    }

    @Test
    void shouldNotBeEqualWhenAmountIsNullOnOneSide() {
        assertFalse(fullBuilder().build().equals(fullBuilder().amount(null).build()));
    }

    @Test
    void shouldNotBeEqualWhenAmountIsNullOnThisSide() {
        assertFalse(fullBuilder().amount(null).build().equals(fullBuilder().build()));
    }

    @Test
    void shouldNotBeEqualWhenDurationDiffers() {
        assertFalse(fullBuilder().build().equals(fullBuilder().duration(120).build()));
    }

    @Test
    void shouldNotBeEqualWhenProductTypeDiffers() {
        assertFalse(fullBuilder().build().equals(fullBuilder().productType(ProductType.MOURABAHA).build()));
    }

    @Test
    void shouldNotBeEqualWhenCspCodeDiffers() {
        assertFalse(fullBuilder().build().equals(fullBuilder().cspCode(CspCode.FONC).build()));
    }

    @Test
    void shouldNotBeEqualWhenNationalityCodeDiffers() {
        assertFalse(fullBuilder().build().equals(fullBuilder().nationalityCode(CODE_FR).build()));
    }

    @Test
    void shouldNotBeEqualWhenMoroccanResidentDiffers() {
        assertFalse(fullBuilder().build().equals(fullBuilder().moroccanResident(false).build()));
    }

    @Test
    void shouldNotBeEqualWhenAgeDiffers() {
        assertFalse(fullBuilder().build().equals(fullBuilder().age(40).build()));
    }

    @Test
    void shouldNotBeEqualWhenTotalMonthlyIncomeDiffers() {
        assertFalse(fullBuilder().build().equals(fullBuilder().totalMonthlyIncome(money(TEXT_25000)).build()));
    }

    @Test
    void shouldNotBeEqualWhenTotalMonthlyIncomeIsNullOnOneSide() {
        assertFalse(fullBuilder().build().equals(fullBuilder().totalMonthlyIncome(null).build()));
    }

    @Test
    void shouldNotBeEqualWhenApplicableRateDiffers() {
        assertFalse(fullBuilder().build()
            .equals(fullBuilder().applicableRate(Rate.of(DECIMAL_5_00)).build()));
    }

    @Test
    void shouldNotBeEqualWhenRateMinDiffers() {
        assertFalse(fullBuilder().build().equals(fullBuilder().rateMin(Rate.of(DECIMAL_3_00)).build()));
    }

    @Test
    void shouldNotBeEqualWhenRateMaxDiffers() {
        assertFalse(fullBuilder().build().equals(fullBuilder().rateMax(Rate.of(DECIMAL_6_00)).build()));
    }

    @Test
    void shouldNotBeEqualWhenApplicableSnapshotDiffers() {
        assertFalse(fullBuilder().build().equals(fullBuilder().applicable(snapshot(TEXT_9999)).build()));
    }

    @Test
    void shouldNotBeEqualWhenApplicableSnapshotIsNullOnOneSide() {
        assertFalse(fullBuilder().build().equals(fullBuilder().applicable(null).build()));
    }

    @Test
    void shouldNotBeEqualWhenApplicableSnapshotIsNullOnThisSide() {
        assertFalse(fullBuilder().applicable(null).build().equals(fullBuilder().build()));
    }

    @Test
    void shouldNotBeEqualWhenApplicableRateIsNullOnThisSide() {
        assertFalse(fullBuilder().applicableRate(null).build().equals(fullBuilder().build()));
    }

    @Test
    void shouldNotBeEqualWhenBoundsDiffer() {
        assertFalse(fullBuilder().build()
            .equals(fullBuilder().bounds(new RateBoundsSnapshot(snapshot("1"), snapshot("2"))).build()));
    }

    @Test
    void shouldNotBeEqualWhenBoundsIsNullOnOneSide() {
        assertFalse(fullBuilder().build().equals(fullBuilder().bounds(null).build()));
    }

    @Test
    void shouldNotBeEqualWhenBoundsIsNullOnThisSide() {
        assertFalse(fullBuilder().bounds(null).build().equals(fullBuilder().build()));
    }

    @Test
    void shouldNotBeEqualWhenMourabahaSnapshotDiffers() {
        assertFalse(fullBuilder().build()
            .equals(fullBuilder().mourabahaSnapshot(mourabahaSnapshot(TEXT_9999)).build()));
    }

    @Test
    void shouldNotBeEqualWhenMourabahaSnapshotIsNullOnOneSide() {
        assertFalse(fullBuilder().build().equals(fullBuilder().mourabahaSnapshot(null).build()));
    }

    @Test
    void shouldNotBeEqualWhenMourabahaSnapshotIsNullOnThisSide() {
        assertFalse(fullBuilder().mourabahaSnapshot(null).build().equals(fullBuilder().build()));
    }

    @Test
    void shouldNotBeEqualWhenAmortizationScheduleIsNullOnOneSide() {
        assertFalse(fullBuilder().build().equals(fullBuilder().amortizationSchedule(null).build()));
    }

    @Test
    void shouldNotBeEqualWhenAmortizationScheduleSizeDiffers() {
        MortgageCreditResponse other = fullBuilder()
            .amortizationSchedule(List.of(amortizationRow(TEXT_1000), amortizationRow(TEXT_2000)))
            .build();
        assertFalse(fullBuilder().build().equals(other));
    }

    @Test
    void shouldNotBeEqualWhenAmortizationScheduleValuesDiffer() {
        MortgageCreditResponse other = fullBuilder().amortizationSchedule(List.of(amortizationRow(TEXT_9999))).build();
        assertFalse(fullBuilder().build().equals(other));
    }

    @Test
    void shouldNotBeEqualWhenMourabahaAmortizationScheduleIsNullOnOneSide() {
        assertFalse(fullBuilder().build().equals(fullBuilder().mourabahaAmortizationSchedule(null).build()));
    }

    @Test
    void shouldNotBeEqualWhenMourabahaAmortizationScheduleSizeDiffers() {
        MortgageCreditResponse other = fullBuilder()
            .mourabahaAmortizationSchedule(List.of(mourabahaRow(TEXT_1000), mourabahaRow(TEXT_2000)))
            .build();
        assertFalse(fullBuilder().build().equals(other));
    }

    @Test
    void shouldNotBeEqualWhenMourabahaAmortizationScheduleValuesDiffer() {
        MortgageCreditResponse other = fullBuilder()
            .mourabahaAmortizationSchedule(List.of(mourabahaRow(TEXT_9999)))
            .build();
        assertFalse(fullBuilder().build().equals(other));
    }

    @Test
    void shouldNotBeEqualWhenTaegDiffers() {
        assertFalse(fullBuilder().build().equals(fullBuilder().taeg(DECIMAL_9_99).build()));
    }

    @Test
    void shouldNotBeEqualWhenTaegIsNullOnOneSide() {
        assertFalse(fullBuilder().build().equals(fullBuilder().taeg(null).build()));
    }

    @Test
    void shouldNotBeEqualWhenTaegIsNullOnThisSide() {
        assertFalse(fullBuilder().taeg(null).build().equals(fullBuilder().build()));
    }

    @Test
    void shouldToStringContainAmortizationScheduleSizes() {
        MortgageCreditResponse response = fullBuilder().build();
        assertEquals(1, response.amortizationSchedule().size());
        assertEquals(1, response.mourabahaAmortizationSchedule().size());
    }

    @Test
    void shouldToStringHandleNullSchedules() {
        MortgageCreditResponse response = MortgageCreditResponse.getBuilder().build();
        assertNull(response.amortizationSchedule());
        assertNull(response.mourabahaAmortizationSchedule());
    }
}
