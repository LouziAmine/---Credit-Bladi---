package com.credit.bladi.unittest.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.model.enums.ProductType;
import com.credit.bladi.model.mourabaha.MourabahaAmortizationRow;
import com.credit.bladi.model.mourabaha.MourabahaFinancialSnapshot;
import com.credit.bladi.model.simulation.AmortizationRow;
import com.credit.bladi.model.simulation.FinancialSnapshot;
import com.credit.bladi.model.simulation.RateBoundsSnapshot;
import com.credit.bladi.model.simulation.response.SimulationResponse;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class SimulationResponseTest {

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
        return new MourabahaFinancialSnapshot(m, m, m, m, m, m, m, m, m, m);
    }

    private static SimulationResponse.Builder fullBuilder() {
        return SimulationResponse.getBuilder()
            .amount(money("500000"))
            .duration(240)
            .productType(ProductType.CONVENTIONAL)
            .cspCode(CspCode.SALA)
            .nationalityCode("MA")
            .moroccanResident(true)
            .age(35)
            .totalMonthlyIncome(money("20000"))
            .applicableRate(Rate.of(new BigDecimal("4.80")))
            .rateMin(Rate.of(new BigDecimal("3.50")))
            .rateMax(Rate.of(new BigDecimal("5.50")))
            .applicable(snapshot("2800"))
            .bounds(new RateBoundsSnapshot(snapshot("2800"), snapshot("3200")))
            .mourabahaSnapshot(mourabahaSnapshot("2800"))
            .amortizationSchedule(List.of(amortizationRow("1000")))
            .mourabahaAmortizationSchedule(List.of(mourabahaRow("1000")))
            .taeg(new BigDecimal("5.10"));
    }

    @Test
    void shouldExposeAllGetters() {
        SimulationResponse resp = fullBuilder().build();

        assertEquals(0, resp.getAmount().amount().compareTo(new BigDecimal("500000")));
        assertEquals(240, resp.getDuration());
        assertEquals(ProductType.CONVENTIONAL, resp.getProductType());
        assertEquals(CspCode.SALA, resp.getCspCode());
        assertEquals("MA", resp.getNationalityCode());
        assertTrue(resp.getMoroccanResident());
        assertEquals(35, resp.getAge());
        assertEquals(0, resp.getTotalMonthlyIncome().amount().compareTo(new BigDecimal("20000")));
        assertEquals(0, resp.getApplicableRate().value().compareTo(new BigDecimal("4.80")));
        assertEquals(0, resp.getRateMin().value().compareTo(new BigDecimal("3.50")));
        assertEquals(0, resp.getRateMax().value().compareTo(new BigDecimal("5.50")));
        assertEquals(snapshot("2800"), resp.getApplicable());
        assertEquals(1, resp.getAmortizationSchedule().size());
        assertEquals(1, resp.getMourabahaAmortizationSchedule().size());
        assertEquals(0, resp.getTaeg().compareTo(new BigDecimal("5.10")));
        assertTrue(resp.getBounds().min().equalsValue(snapshot("2800")));
        assertTrue(resp.getMourabahaSnapshot().equalsValue(mourabahaSnapshot("2800")));
    }

    @Test
    void shouldBeEqualToItself() {
        SimulationResponse resp = fullBuilder().build();
        assertTrue(resp.equals(resp));
    }

    @Test
    void shouldNotBeEqualToDifferentType() {
        SimulationResponse resp = fullBuilder().build();
        assertFalse(resp.equals("other"));
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
        SimulationResponse r1 = SimulationResponse.getBuilder().build();
        SimulationResponse r2 = SimulationResponse.getBuilder().build();
        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenAmountDiffers() {
        assertFalse(fullBuilder().build().equals(fullBuilder().amount(money("600000")).build()));
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
        assertFalse(fullBuilder().build().equals(fullBuilder().nationalityCode("FR").build()));
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
        assertFalse(fullBuilder().build().equals(fullBuilder().totalMonthlyIncome(money("25000")).build()));
    }

    @Test
    void shouldNotBeEqualWhenTotalMonthlyIncomeIsNullOnOneSide() {
        assertFalse(fullBuilder().build().equals(fullBuilder().totalMonthlyIncome(null).build()));
    }

    @Test
    void shouldNotBeEqualWhenApplicableRateDiffers() {
        assertFalse(fullBuilder().build()
            .equals(fullBuilder().applicableRate(Rate.of(new BigDecimal("5.00"))).build()));
    }

    @Test
    void shouldNotBeEqualWhenRateMinDiffers() {
        assertFalse(fullBuilder().build().equals(fullBuilder().rateMin(Rate.of(new BigDecimal("3.00"))).build()));
    }

    @Test
    void shouldNotBeEqualWhenRateMaxDiffers() {
        assertFalse(fullBuilder().build().equals(fullBuilder().rateMax(Rate.of(new BigDecimal("6.00"))).build()));
    }

    @Test
    void shouldNotBeEqualWhenApplicableSnapshotDiffers() {
        assertFalse(fullBuilder().build().equals(fullBuilder().applicable(snapshot("9999")).build()));
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
        assertFalse(fullBuilder().build().equals(fullBuilder().mourabahaSnapshot(mourabahaSnapshot("9999")).build()));
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
        SimulationResponse other = fullBuilder()
            .amortizationSchedule(List.of(amortizationRow("1000"), amortizationRow("2000")))
            .build();
        assertFalse(fullBuilder().build().equals(other));
    }

    @Test
    void shouldNotBeEqualWhenAmortizationScheduleValuesDiffer() {
        SimulationResponse other = fullBuilder().amortizationSchedule(List.of(amortizationRow("9999"))).build();
        assertFalse(fullBuilder().build().equals(other));
    }

    @Test
    void shouldNotBeEqualWhenMourabahaAmortizationScheduleIsNullOnOneSide() {
        assertFalse(fullBuilder().build().equals(fullBuilder().mourabahaAmortizationSchedule(null).build()));
    }

    @Test
    void shouldNotBeEqualWhenMourabahaAmortizationScheduleSizeDiffers() {
        SimulationResponse other = fullBuilder()
            .mourabahaAmortizationSchedule(List.of(mourabahaRow("1000"), mourabahaRow("2000")))
            .build();
        assertFalse(fullBuilder().build().equals(other));
    }

    @Test
    void shouldNotBeEqualWhenMourabahaAmortizationScheduleValuesDiffer() {
        SimulationResponse other = fullBuilder()
            .mourabahaAmortizationSchedule(List.of(mourabahaRow("9999")))
            .build();
        assertFalse(fullBuilder().build().equals(other));
    }

    @Test
    void shouldNotBeEqualWhenTaegDiffers() {
        assertFalse(fullBuilder().build().equals(fullBuilder().taeg(new BigDecimal("9.99")).build()));
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
        String str = fullBuilder().build().toString();
        assertTrue(str.contains("amortizationScheduleSize=1"));
        assertTrue(str.contains("mourabahaAmortizationScheduleSize=1"));
    }

    @Test
    void shouldToStringHandleNullSchedules() {
        String str = SimulationResponse.getBuilder().build().toString();
        assertTrue(str.contains("amortizationScheduleSize=null"));
        assertTrue(str.contains("mourabahaAmortizationScheduleSize=null"));
    }
}
