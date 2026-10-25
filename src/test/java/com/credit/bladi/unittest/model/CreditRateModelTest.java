package com.credit.bladi.unittest.model;

import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_3_50;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_5_00;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_5_50;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_6_00;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_SALA;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_OTHER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.model.creditrate.CreditRateModel;
import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.model.enums.RateFormula;
import com.credit.bladi.model.vo.DurationRange;
import com.credit.bladi.model.vo.Rate;
import org.junit.jupiter.api.Test;

class CreditRateModelTest {
    @Test
    void shouldBuildWithAllFields() {
        CreditRateModel model = CreditRateModel.getBuilder()
            .id(1L)
            .cspCode(CspCode.SALA)
            .rateMin(Rate.of(DECIMAL_3_50))
            .rateMax(Rate.of(DECIMAL_5_50))
            .rateFormula(RateFormula.AVERAGE)
            .durationRange(DurationRange.of(12, 300))
            .active(true)
            .build();

        assertNotNull(model);
        assertEquals(1L, model.getId());
        assertEquals(CspCode.SALA, model.getCspCode());
        assertEquals(0, model.getRateMin().value().compareTo(DECIMAL_3_50));
        assertEquals(0, model.getRateMax().value().compareTo(DECIMAL_5_50));
        assertEquals(RateFormula.AVERAGE, model.getRateFormula());
        assertEquals(12, model.getDurationRange().minMonths());
        assertEquals(300, model.getDurationRange().maxMonths());
        assertTrue(model.getActive());
    }

    @Test
    void shouldCalculateRateUsingRateFormula() {
        CreditRateModel minFormulaModel = CreditRateModel.getBuilder()
            .id(1L)
            .rateMin(Rate.of(DECIMAL_3_50))
            .rateMax(Rate.of(DECIMAL_5_50))
            .rateFormula(RateFormula.MIN)
            .build();

        assertEquals(0, minFormulaModel.calculateRate().value().compareTo(DECIMAL_3_50));
    }

    @Test
    void shouldBeEqualWhenSameFields() {
        CreditRateModel m1 = CreditRateModel.getBuilder()
            .id(1L).cspCode(CspCode.SALA).durationRange(DurationRange.of(12, 300)).build();
        CreditRateModel m2 = CreditRateModel.getBuilder()
            .id(1L).cspCode(CspCode.SALA).durationRange(DurationRange.of(12, 300)).build();

        assertEquals(m1, m2);
    }

    @Test
    void shouldHaveHashCode() {
        CreditRateModel model = CreditRateModel.getBuilder()
            .id(1L).cspCode(CspCode.SALA).durationRange(DurationRange.of(12, 300)).build();
        assertNotNull(model.hashCode());
    }

    @Test
    void shouldAllowNullCspCodeForPartialPatch() {
        CreditRateModel patch = CreditRateModel.getBuilder()
            .id(1L).rateMax(Rate.of(DECIMAL_6_00)).build();

        assertEquals(1L, patch.getId());
        assertNull(patch.getCspCode());
    }

    @Test
    void shouldAllowNullDurationRangeForPartialPatch() {
        CreditRateModel patch = CreditRateModel.getBuilder()
            .id(1L).cspCode(CspCode.SALA).active(true).build();

        assertNull(patch.getDurationRange());
    }

    @Test
    void shouldRejectRateMinGreaterThanRateMax() {
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
            () -> CreditRateModel.getBuilder()
                .id(1L)
                .rateMin(Rate.of(DECIMAL_6_00))
                .rateMax(Rate.of(DECIMAL_5_00))
                .build());
    }

    @Test
    void shouldDescribeChangesFromBefore() {
        CreditRateModel before = CreditRateModel.getBuilder()
            .id(1L).cspCode(CspCode.SALA)
            .rateMin(Rate.of(DECIMAL_5_00)).rateMax(Rate.of(DECIMAL_5_50))
            .durationRange(DurationRange.of(12, 240)).active(true).build();
        CreditRateModel after = CreditRateModel.getBuilder()
            .id(1L).cspCode(CspCode.SALA)
            .rateMin(Rate.of(DECIMAL_5_00)).rateMax(Rate.of(DECIMAL_6_00))
            .durationRange(DurationRange.of(12, 240)).active(true).build();

        String changes = after.describeChangesFrom(before);

        assertTrue(changes.contains("rateMax"));
        assertTrue(!changes.contains("rateMin:"));
    }

    @Test
    void shouldDescribeRateMinBecomingUnset() {
        CreditRateModel before = CreditRateModel.getBuilder()
            .id(1L).rateMin(Rate.of(DECIMAL_5_00)).build();
        CreditRateModel after = CreditRateModel.getBuilder().id(1L).build();

        String changes = after.describeChangesFrom(before);

        assertTrue(changes.contains("rateMin"));
    }

    @Test
    void shouldHaveToString() {
        CreditRateModel model = CreditRateModel.getBuilder()
            .id(1L).cspCode(CspCode.SALA).durationRange(DurationRange.of(12, 300)).build();
        String str = model.toString();
        assertTrue(str.contains(CODE_SALA));
    }

    @Test
    void shouldBeEqualToItself() {
        CreditRateModel model = CreditRateModel.getBuilder().id(1L).build();
        assertTrue(model.equals(model));
    }

    @Test
    void shouldNotBeEqualWhenDifferentId() {
        CreditRateModel m1 = CreditRateModel.getBuilder().id(1L).build();
        CreditRateModel m2 = CreditRateModel.getBuilder().id(2L).build();
        assertTrue(!m1.equals(m2));
    }

    @Test
    void shouldNotBeEqualWhenNullId() {
        CreditRateModel m1 = CreditRateModel.getBuilder().build();
        CreditRateModel m2 = CreditRateModel.getBuilder().build();
        assertTrue(!m1.equals(m2));
    }

    @Test
    void shouldNotBeEqualToDifferentType() {
        CreditRateModel model = CreditRateModel.getBuilder().id(1L).build();
        assertTrue(!model.equals(USER_OTHER));
    }
}
