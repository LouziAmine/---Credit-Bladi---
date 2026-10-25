package com.credit.bladi.unittest.model;

import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_1000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.TEXT_1000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.TEXT_2000;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.model.mourabaha.MourabahaAmortizationRow;
import com.credit.bladi.model.mourabaha.MourabahaFinancialSnapshot;
import com.credit.bladi.model.mourabaha.MourabahaParametersModel;
import com.credit.bladi.model.mourabaha.MourabahaParametersPatchResult;
import com.credit.bladi.model.vo.Money;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class MourabahaModelsTest {
    private static MourabahaAmortizationRow row(int n, String amount) {
        Money m = Money.of(new BigDecimal(amount));
        return new MourabahaAmortizationRow(n, m, m, m, m, m, m, m);
    }

    @Test
    void shouldCreateAmortizationRow() {
        MourabahaAmortizationRow row = row(1, TEXT_1000);
        assertNotNull(row);
        assertEquals(1, row.installmentNumber());
        assertEquals(0, row.beginningBalance().amount().compareTo(AMOUNT_1000));
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
        MourabahaAmortizationRow r1 = new MourabahaAmortizationRow(1, null, null, null, null, null, null, null);
        MourabahaAmortizationRow r2 = new MourabahaAmortizationRow(1, null, null, null, null, null, null, null);

        assertTrue(r1.equals(r2));
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    private static Money money(String v) {
        return Money.of(new BigDecimal(v));
    }

    private static MourabahaAmortizationRow rowWith(int differingIndex) {
        Money[] values = new Money[7];
        java.util.Arrays.fill(values, money(TEXT_1000));
        values[differingIndex] = money(TEXT_2000);
        return new MourabahaAmortizationRow(1, values[0], values[1], values[2], values[3], values[4], values[5],
            values[6]);
    }

    @Test
    void shouldAmortizationRowNotBeEqualValueWhenBeginningBalanceIsNullOnThisSideOnly() {
        MourabahaAmortizationRow r1 = new MourabahaAmortizationRow(
            1, null, money(TEXT_1000), money(TEXT_1000), money(TEXT_1000), money(TEXT_1000), money(TEXT_1000),
            money(TEXT_1000));
        MourabahaAmortizationRow r2 = row(1, TEXT_1000);

        assertFalse(r1.equals(r2));
    }

    @Test
    void shouldAmortizationRowNotBeEqualValueWhenOnlyOneFieldDiffers() {
        MourabahaAmortizationRow base = row(1, TEXT_1000);
        for (int i = 1; i < 7; i++) {
            assertFalse(base.equals(rowWith(i)), "field index " + i + " should cause inequality");
        }
    }

    private static MourabahaFinancialSnapshot snapshot(String amount) {
        Money m = Money.of(new BigDecimal(amount));
        return new MourabahaFinancialSnapshot(m, m, m, m, m, m, m, m, m, m, m, m, m);
    }

    @Test
    void shouldCreateFinancialSnapshot() {
        MourabahaFinancialSnapshot snap = snapshot(TEXT_1000);
        assertNotNull(snap);
        assertEquals(0, snap.monthlyPaymentTtc().amount().compareTo(AMOUNT_1000));
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
        MourabahaFinancialSnapshot s1 =
            new MourabahaFinancialSnapshot(null, null, null, null, null, null, null, null, null, null, null, null,
                null);
        MourabahaFinancialSnapshot s2 =
            new MourabahaFinancialSnapshot(null, null, null, null, null, null, null, null, null, null, null, null,
                null);

        assertTrue(s1.equals(s2));
        assertEquals(s1.hashCode(), s2.hashCode());
    }

    private static MourabahaFinancialSnapshot snapshotWith(int differingIndex) {
        Money[] values = new Money[13];
        java.util.Arrays.fill(values, money(TEXT_1000));
        values[differingIndex] = money(TEXT_2000);
        return new MourabahaFinancialSnapshot(values[0], values[1], values[2], values[3], values[4], values[5],
            values[6], values[7], values[8], values[9], values[10], values[11], values[12]);
    }

    @Test
    void shouldFinancialSnapshotNotBeEqualValueWhenMonthlyPaymentTtcIsNullOnThisSideOnly() {
        MourabahaFinancialSnapshot s1 = new MourabahaFinancialSnapshot(
            null, money(TEXT_1000), money(TEXT_1000), money(TEXT_1000),
            money(TEXT_1000), money(TEXT_1000), money(TEXT_1000), money(TEXT_1000), money(TEXT_1000), money(TEXT_1000),
            money(TEXT_1000), money(TEXT_1000), money(TEXT_1000));
        MourabahaFinancialSnapshot s2 = snapshot(TEXT_1000);

        assertFalse(s1.equals(s2));
    }

    @Test
    void shouldFinancialSnapshotNotBeEqualValueWhenOnlyOneFieldDiffers() {
        MourabahaFinancialSnapshot base = snapshot(TEXT_1000);
        for (int i = 1; i < 13; i++) {
            assertFalse(base.equals(snapshotWith(i)), "field index " + i + " should cause inequality");
        }
    }

    @Test
    void shouldCreatePatchResult() {
        MourabahaParametersModel before = MourabahaParametersModel.getBuilder().id(1L).active(false).build();
        MourabahaParametersModel after = MourabahaParametersModel.getBuilder().id(1L).active(true).build();

        MourabahaParametersPatchResult result = new MourabahaParametersPatchResult(before, after);

        assertEquals(before, result.before());
        assertEquals(after, result.after());
    }

    @Test
    void shouldPatchResultEqualsAndHashCode() {
        MourabahaParametersModel model = MourabahaParametersModel.getBuilder().id(1L).build();
        MourabahaParametersPatchResult r1 = new MourabahaParametersPatchResult(model, model);
        MourabahaParametersPatchResult r2 = new MourabahaParametersPatchResult(model, model);

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertTrue(r1.toString().contains("MourabahaParametersPatchResult"));
    }
}
