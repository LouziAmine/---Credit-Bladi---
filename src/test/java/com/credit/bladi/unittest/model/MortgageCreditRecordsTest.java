package com.credit.bladi.unittest.model;

import static com.credit.bladi.unittest.mock.data.MockAmounts.TEXT_1000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.TEXT_1500;
import static com.credit.bladi.unittest.mock.data.MockAmounts.TEXT_2000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.TEXT_2500;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.model.creditrate.CreditRateModel;
import com.credit.bladi.model.creditrate.CreditRatePatchResult;
import com.credit.bladi.model.mortgage.FinancialSnapshot;
import com.credit.bladi.model.mortgage.RateBoundsSnapshot;
import com.credit.bladi.model.vo.AmortizationRow;
import com.credit.bladi.model.vo.Money;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class MortgageCreditRecordsTest {
    private static AmortizationRow row(int n, String amount) {
        Money m = Money.of(new BigDecimal(amount));
        return new AmortizationRow(n, m, m, m, m, m);
    }

    @Test
    void shouldAmortizationRowEqualsValueTrue() {
        assertTrue(row(1, TEXT_1000).equals(row(1, TEXT_1000)));
    }

    @Test
    void shouldAmortizationRowEqualsValueFalseWhenOtherNull() {
        assertFalse(row(1, TEXT_1000).equals(null));
    }

    @Test
    void shouldAmortizationRowEqualsValueFalseWhenInstallmentNumberDiffers() {
        assertFalse(row(1, TEXT_1000).equals(row(2, TEXT_1000)));
    }

    @Test
    void shouldAmortizationRowEqualsValueFalseWhenAmountDiffers() {
        assertFalse(row(1, TEXT_1000).equals(row(1, TEXT_2000)));
    }

    @Test
    void shouldAmortizationRowValueHashCodeBeConsistent() {
        assertEquals(row(1, TEXT_1000).hashCode(), row(1, TEXT_1000).hashCode());
    }

    @Test
    void shouldAmortizationRowEqualsValueTrueWhenAllMoneyFieldsNull() {
        AmortizationRow r1 = new AmortizationRow(1, null, null, null, null, null);
        AmortizationRow r2 = new AmortizationRow(1, null, null, null, null, null);

        assertTrue(r1.equals(r2));
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    private static Money money(String v) {
        return Money.of(new BigDecimal(v));
    }

    @Test
    void shouldAmortizationRowNotBeEqualValueWhenBeginningBalanceIsNullOnThisSideOnly() {
        AmortizationRow r1 = new AmortizationRow(
            1, null, money(TEXT_1000), money(TEXT_1000), money(TEXT_1000), money(TEXT_1000));
        AmortizationRow r2 = new AmortizationRow(
            1, money(TEXT_1000), money(TEXT_1000), money(TEXT_1000), money(TEXT_1000), money(TEXT_1000));

        assertFalse(r1.equals(r2));
    }

    @Test
    void shouldAmortizationRowNotBeEqualValueWhenOnlyInterestDiffers() {
        AmortizationRow r1 = new AmortizationRow(
            1, money(TEXT_1000), money(TEXT_1000), money(TEXT_1000), money(TEXT_1000), money(TEXT_1000));
        AmortizationRow r2 = new AmortizationRow(
            1, money(TEXT_1000), money(TEXT_2000), money(TEXT_1000), money(TEXT_1000), money(TEXT_1000));

        assertFalse(r1.equals(r2));
    }

    @Test
    void shouldAmortizationRowNotBeEqualValueWhenOnlyPrincipalDiffers() {
        AmortizationRow r1 = new AmortizationRow(
            1, money(TEXT_1000), money(TEXT_1000), money(TEXT_1000), money(TEXT_1000), money(TEXT_1000));
        AmortizationRow r2 = new AmortizationRow(
            1, money(TEXT_1000), money(TEXT_1000), money(TEXT_2000), money(TEXT_1000), money(TEXT_1000));

        assertFalse(r1.equals(r2));
    }

    @Test
    void shouldAmortizationRowNotBeEqualValueWhenOnlyPaymentDiffers() {
        AmortizationRow r1 = new AmortizationRow(
            1, money(TEXT_1000), money(TEXT_1000), money(TEXT_1000), money(TEXT_1000), money(TEXT_1000));
        AmortizationRow r2 = new AmortizationRow(
            1, money(TEXT_1000), money(TEXT_1000), money(TEXT_1000), money(TEXT_2000), money(TEXT_1000));

        assertFalse(r1.equals(r2));
    }

    @Test
    void shouldAmortizationRowNotBeEqualValueWhenOnlyEndingBalanceDiffers() {
        AmortizationRow r1 = new AmortizationRow(
            1, money(TEXT_1000), money(TEXT_1000), money(TEXT_1000), money(TEXT_1000), money(TEXT_1000));
        AmortizationRow r2 = new AmortizationRow(
            1, money(TEXT_1000), money(TEXT_1000), money(TEXT_1000), money(TEXT_1000), money(TEXT_2000));

        assertFalse(r1.equals(r2));
    }

    private static FinancialSnapshot snapshot(String amount) {
        Money m = Money.of(new BigDecimal(amount));
        return new FinancialSnapshot(m, m, m, m, m, m, m, m, m, m, m);
    }

    @Test
    void shouldFinancialSnapshotEqualsValueTrue() {
        assertTrue(snapshot(TEXT_1000).equals(snapshot(TEXT_1000)));
    }

    @Test
    void shouldFinancialSnapshotEqualsValueFalseWhenOtherNull() {
        assertFalse(snapshot(TEXT_1000).equals(null));
    }

    @Test
    void shouldFinancialSnapshotEqualsValueFalseWhenAmountDiffers() {
        assertFalse(snapshot(TEXT_1000).equals(snapshot(TEXT_2000)));
    }

    @Test
    void shouldFinancialSnapshotValueHashCodeBeConsistent() {
        assertEquals(snapshot(TEXT_1000).hashCode(), snapshot(TEXT_1000).hashCode());
    }

    @Test
    void shouldFinancialSnapshotEqualsValueTrueWhenAllMoneyFieldsNull() {
        FinancialSnapshot s1 = new FinancialSnapshot(null, null, null, null, null, null, null, null, null, null, null);
        FinancialSnapshot s2 = new FinancialSnapshot(null, null, null, null, null, null, null, null, null, null, null);

        assertTrue(s1.equals(s2));
        assertEquals(s1.hashCode(), s2.hashCode());
    }

    private static FinancialSnapshot snapshotWith(int differingIndex) {
        Money[] values = new Money[11];
        java.util.Arrays.fill(values, money(TEXT_1000));
        values[differingIndex] = money(TEXT_2000);
        return new FinancialSnapshot(values[0], values[1], values[2], values[3], values[4], values[5],
            values[6], values[7], values[8], values[9], values[10]);
    }

    @Test
    void shouldFinancialSnapshotNotBeEqualValueWhenMonthlyPaymentIsNullOnThisSideOnly() {
        FinancialSnapshot s1 = new FinancialSnapshot(
            null, money(TEXT_1000), money(TEXT_1000), money(TEXT_1000), money(TEXT_1000), money(TEXT_1000),
            money(TEXT_1000), money(TEXT_1000), money(TEXT_1000), money(TEXT_1000), money(TEXT_1000));
        FinancialSnapshot s2 = snapshot(TEXT_1000);

        assertFalse(s1.equals(s2));
    }

    @Test
    void shouldFinancialSnapshotNotBeEqualValueWhenOnlyOneFieldDiffers() {
        FinancialSnapshot base = snapshot(TEXT_1000);
        for (int i = 1; i < 11; i++) {
            assertFalse(base.equals(snapshotWith(i)), "field index " + i + " should cause inequality");
        }
    }

    @Test
    void shouldRateBoundsSnapshotEqualsValueTrue() {
        RateBoundsSnapshot b1 = new RateBoundsSnapshot(snapshot(TEXT_1000), snapshot(TEXT_2000));
        RateBoundsSnapshot b2 = new RateBoundsSnapshot(snapshot(TEXT_1000), snapshot(TEXT_2000));
        assertTrue(b1.equals(b2));
    }

    @Test
    void shouldRateBoundsSnapshotEqualsValueFalseWhenOtherNull() {
        RateBoundsSnapshot b1 = new RateBoundsSnapshot(snapshot(TEXT_1000), snapshot(TEXT_2000));
        assertFalse(b1.equals(null));
    }

    @Test
    void shouldRateBoundsSnapshotEqualsValueFalseWhenMinDiffers() {
        RateBoundsSnapshot b1 = new RateBoundsSnapshot(snapshot(TEXT_1000), snapshot(TEXT_2000));
        RateBoundsSnapshot b2 = new RateBoundsSnapshot(snapshot(TEXT_1500), snapshot(TEXT_2000));
        assertFalse(b1.equals(b2));
    }

    @Test
    void shouldRateBoundsSnapshotEqualsValueFalseWhenMaxDiffers() {
        RateBoundsSnapshot b1 = new RateBoundsSnapshot(snapshot(TEXT_1000), snapshot(TEXT_2000));
        RateBoundsSnapshot b2 = new RateBoundsSnapshot(snapshot(TEXT_1000), snapshot(TEXT_2500));
        assertFalse(b1.equals(b2));
    }

    @Test
    void shouldRateBoundsSnapshotValueHashCodeBeConsistent() {
        RateBoundsSnapshot b1 = new RateBoundsSnapshot(snapshot(TEXT_1000), snapshot(TEXT_2000));
        RateBoundsSnapshot b2 = new RateBoundsSnapshot(snapshot(TEXT_1000), snapshot(TEXT_2000));
        assertEquals(b1.hashCode(), b2.hashCode());
    }

    @Test
    void shouldRateBoundsSnapshotEqualsValueTrueWhenBothFieldsNull() {
        RateBoundsSnapshot b1 = new RateBoundsSnapshot(null, null);
        RateBoundsSnapshot b2 = new RateBoundsSnapshot(null, null);

        assertTrue(b1.equals(b2));
        assertEquals(b1.hashCode(), b2.hashCode());
    }

    @Test
    void shouldRateBoundsSnapshotNotBeEqualValueWhenMinIsNullOnThisSideOnly() {
        RateBoundsSnapshot b1 = new RateBoundsSnapshot(null, snapshot(TEXT_2000));
        RateBoundsSnapshot b2 = new RateBoundsSnapshot(snapshot(TEXT_1000), snapshot(TEXT_2000));

        assertFalse(b1.equals(b2));
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
