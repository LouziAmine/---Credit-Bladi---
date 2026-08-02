package com.credit.bladi.unittest.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.model.simulation.AmortizationRow;
import com.credit.bladi.model.simulation.CreditRateModel;
import com.credit.bladi.model.simulation.CreditRatePatchResult;
import com.credit.bladi.model.simulation.FinancialSnapshot;
import com.credit.bladi.model.simulation.RateBoundsSnapshot;
import com.credit.bladi.model.vo.Money;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class SimulationRecordsTest {

    private static AmortizationRow row(int n, String amount) {
        Money m = Money.of(new BigDecimal(amount));
        return new AmortizationRow(n, m, m, m, m, m);
    }

    @Test
    void shouldAmortizationRowEqualsValueTrue() {
        assertTrue(row(1, "1000").equalsValue(row(1, "1000")));
    }

    @Test
    void shouldAmortizationRowEqualsValueFalseWhenOtherNull() {
        assertFalse(row(1, "1000").equalsValue(null));
    }

    @Test
    void shouldAmortizationRowEqualsValueFalseWhenInstallmentNumberDiffers() {
        assertFalse(row(1, "1000").equalsValue(row(2, "1000")));
    }

    @Test
    void shouldAmortizationRowEqualsValueFalseWhenAmountDiffers() {
        assertFalse(row(1, "1000").equalsValue(row(1, "2000")));
    }

    @Test
    void shouldAmortizationRowValueHashCodeBeConsistent() {
        assertEquals(row(1, "1000").valueHashCode(), row(1, "1000").valueHashCode());
    }

    @Test
    void shouldAmortizationRowEqualsValueTrueWhenAllMoneyFieldsNull() {
        AmortizationRow r1 = new AmortizationRow(1, null, null, null, null, null);
        AmortizationRow r2 = new AmortizationRow(1, null, null, null, null, null);

        assertTrue(r1.equalsValue(r2));
        assertEquals(r1.valueHashCode(), r2.valueHashCode());
    }

    private static Money money(String v) {
        return Money.of(new BigDecimal(v));
    }

    @Test
    void shouldAmortizationRowNotBeEqualValueWhenBeginningBalanceIsNullOnThisSideOnly() {
        AmortizationRow r1 = new AmortizationRow(
            1, null, money("1000"), money("1000"), money("1000"), money("1000"));
        AmortizationRow r2 = new AmortizationRow(
            1, money("1000"), money("1000"), money("1000"), money("1000"), money("1000"));

        assertFalse(r1.equalsValue(r2));
    }

    @Test
    void shouldAmortizationRowNotBeEqualValueWhenOnlyInterestDiffers() {
        AmortizationRow r1 = new AmortizationRow(
            1, money("1000"), money("1000"), money("1000"), money("1000"), money("1000"));
        AmortizationRow r2 = new AmortizationRow(
            1, money("1000"), money("2000"), money("1000"), money("1000"), money("1000"));

        assertFalse(r1.equalsValue(r2));
    }

    @Test
    void shouldAmortizationRowNotBeEqualValueWhenOnlyPrincipalDiffers() {
        AmortizationRow r1 = new AmortizationRow(
            1, money("1000"), money("1000"), money("1000"), money("1000"), money("1000"));
        AmortizationRow r2 = new AmortizationRow(
            1, money("1000"), money("1000"), money("2000"), money("1000"), money("1000"));

        assertFalse(r1.equalsValue(r2));
    }

    @Test
    void shouldAmortizationRowNotBeEqualValueWhenOnlyPaymentDiffers() {
        AmortizationRow r1 = new AmortizationRow(
            1, money("1000"), money("1000"), money("1000"), money("1000"), money("1000"));
        AmortizationRow r2 = new AmortizationRow(
            1, money("1000"), money("1000"), money("1000"), money("2000"), money("1000"));

        assertFalse(r1.equalsValue(r2));
    }

    @Test
    void shouldAmortizationRowNotBeEqualValueWhenOnlyEndingBalanceDiffers() {
        AmortizationRow r1 = new AmortizationRow(
            1, money("1000"), money("1000"), money("1000"), money("1000"), money("1000"));
        AmortizationRow r2 = new AmortizationRow(
            1, money("1000"), money("1000"), money("1000"), money("1000"), money("2000"));

        assertFalse(r1.equalsValue(r2));
    }

    private static FinancialSnapshot snapshot(String amount) {
        Money m = Money.of(new BigDecimal(amount));
        return new FinancialSnapshot(m, m, m, m, m, m, m, m, m, m, m);
    }

    @Test
    void shouldFinancialSnapshotEqualsValueTrue() {
        assertTrue(snapshot("1000").equalsValue(snapshot("1000")));
    }

    @Test
    void shouldFinancialSnapshotEqualsValueFalseWhenOtherNull() {
        assertFalse(snapshot("1000").equalsValue(null));
    }

    @Test
    void shouldFinancialSnapshotEqualsValueFalseWhenAmountDiffers() {
        assertFalse(snapshot("1000").equalsValue(snapshot("2000")));
    }

    @Test
    void shouldFinancialSnapshotValueHashCodeBeConsistent() {
        assertEquals(snapshot("1000").valueHashCode(), snapshot("1000").valueHashCode());
    }

    @Test
    void shouldFinancialSnapshotEqualsValueTrueWhenAllMoneyFieldsNull() {
        FinancialSnapshot s1 = new FinancialSnapshot(null, null, null, null, null, null, null, null, null, null, null);
        FinancialSnapshot s2 = new FinancialSnapshot(null, null, null, null, null, null, null, null, null, null, null);

        assertTrue(s1.equalsValue(s2));
        assertEquals(s1.valueHashCode(), s2.valueHashCode());
    }

    private static FinancialSnapshot snapshotWith(int differingIndex) {
        Money[] values = new Money[11];
        java.util.Arrays.fill(values, money("1000"));
        values[differingIndex] = money("2000");
        return new FinancialSnapshot(values[0], values[1], values[2], values[3], values[4], values[5],
            values[6], values[7], values[8], values[9], values[10]);
    }

    @Test
    void shouldFinancialSnapshotNotBeEqualValueWhenMonthlyPaymentIsNullOnThisSideOnly() {
        FinancialSnapshot s1 = new FinancialSnapshot(
            null, money("1000"), money("1000"), money("1000"), money("1000"), money("1000"),
            money("1000"), money("1000"), money("1000"), money("1000"), money("1000"));
        FinancialSnapshot s2 = snapshot("1000");

        assertFalse(s1.equalsValue(s2));
    }

    @Test
    void shouldFinancialSnapshotNotBeEqualValueWhenOnlyOneFieldDiffers() {
        FinancialSnapshot base = snapshot("1000");
        for (int i = 1; i < 11; i++) {
            assertFalse(base.equalsValue(snapshotWith(i)), "field index " + i + " should cause inequality");
        }
    }

    @Test
    void shouldRateBoundsSnapshotEqualsValueTrue() {
        RateBoundsSnapshot b1 = new RateBoundsSnapshot(snapshot("1000"), snapshot("2000"));
        RateBoundsSnapshot b2 = new RateBoundsSnapshot(snapshot("1000"), snapshot("2000"));
        assertTrue(b1.equalsValue(b2));
    }

    @Test
    void shouldRateBoundsSnapshotEqualsValueFalseWhenOtherNull() {
        RateBoundsSnapshot b1 = new RateBoundsSnapshot(snapshot("1000"), snapshot("2000"));
        assertFalse(b1.equalsValue(null));
    }

    @Test
    void shouldRateBoundsSnapshotEqualsValueFalseWhenMinDiffers() {
        RateBoundsSnapshot b1 = new RateBoundsSnapshot(snapshot("1000"), snapshot("2000"));
        RateBoundsSnapshot b2 = new RateBoundsSnapshot(snapshot("1500"), snapshot("2000"));
        assertFalse(b1.equalsValue(b2));
    }

    @Test
    void shouldRateBoundsSnapshotEqualsValueFalseWhenMaxDiffers() {
        RateBoundsSnapshot b1 = new RateBoundsSnapshot(snapshot("1000"), snapshot("2000"));
        RateBoundsSnapshot b2 = new RateBoundsSnapshot(snapshot("1000"), snapshot("2500"));
        assertFalse(b1.equalsValue(b2));
    }

    @Test
    void shouldRateBoundsSnapshotValueHashCodeBeConsistent() {
        RateBoundsSnapshot b1 = new RateBoundsSnapshot(snapshot("1000"), snapshot("2000"));
        RateBoundsSnapshot b2 = new RateBoundsSnapshot(snapshot("1000"), snapshot("2000"));
        assertEquals(b1.valueHashCode(), b2.valueHashCode());
    }

    @Test
    void shouldRateBoundsSnapshotEqualsValueTrueWhenBothFieldsNull() {
        RateBoundsSnapshot b1 = new RateBoundsSnapshot(null, null);
        RateBoundsSnapshot b2 = new RateBoundsSnapshot(null, null);

        assertTrue(b1.equalsValue(b2));
        assertEquals(b1.valueHashCode(), b2.valueHashCode());
    }

    @Test
    void shouldRateBoundsSnapshotNotBeEqualValueWhenMinIsNullOnThisSideOnly() {
        RateBoundsSnapshot b1 = new RateBoundsSnapshot(null, snapshot("2000"));
        RateBoundsSnapshot b2 = new RateBoundsSnapshot(snapshot("1000"), snapshot("2000"));

        assertFalse(b1.equalsValue(b2));
    }

    @Test
    void shouldCreatePatchResult() {
        CreditRateModel before = CreditRateModel.getBuilder().id(1L).active(false).build();
        CreditRateModel after = CreditRateModel.getBuilder().id(1L).active(true).build();

        CreditRatePatchResult result = new CreditRatePatchResult(before, after);

        assertEquals(before, result.before());
        assertEquals(after, result.after());
    }

    @Test
    void shouldPatchResultEqualsAndHashCode() {
        CreditRateModel model = CreditRateModel.getBuilder().id(1L).build();
        CreditRatePatchResult r1 = new CreditRatePatchResult(model, model);
        CreditRatePatchResult r2 = new CreditRatePatchResult(model, model);

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertTrue(r1.toString().contains("CreditRatePatchResult"));
    }
}
