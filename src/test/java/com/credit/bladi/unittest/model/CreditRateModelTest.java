package com.credit.bladi.unittest.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.model.simulation.CreditRateModel;
import com.credit.bladi.model.vo.DurationRange;
import com.credit.bladi.model.vo.Rate;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class CreditRateModelTest {

    @Test
    void shouldBuildWithAllFields() {
        CreditRateModel model = CreditRateModel.getBuilder()
            .id(1L)
            .cspCode(CspCode.SALA)
            .rateMin(Rate.of(new BigDecimal("3.50")))
            .rateMax(Rate.of(new BigDecimal("5.50")))
            .durationRange(DurationRange.of(12, 300))
            .active(true)
            .build();

        assertNotNull(model);
        assertEquals(1L, model.getId());
        assertEquals(CspCode.SALA, model.getCspCode());
        assertEquals(0, model.getRateMin().value().compareTo(new BigDecimal("3.50")));
        assertEquals(0, model.getRateMax().value().compareTo(new BigDecimal("5.50")));
        assertEquals(12, model.getDurationRange().minMonths());
        assertEquals(300, model.getDurationRange().maxMonths());
        assertTrue(model.getActive());
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
            .id(1L).rateMax(Rate.of(new BigDecimal("6.00"))).build();

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
                .rateMin(Rate.of(new BigDecimal("6.00")))
                .rateMax(Rate.of(new BigDecimal("5.00")))
                .build());
    }

    @Test
    void shouldDescribeChangesFromBefore() {
        CreditRateModel before = CreditRateModel.getBuilder()
            .id(1L).cspCode(CspCode.SALA)
            .rateMin(Rate.of(new BigDecimal("5.00"))).rateMax(Rate.of(new BigDecimal("5.50")))
            .durationRange(DurationRange.of(12, 240)).active(true).build();
        CreditRateModel after = CreditRateModel.getBuilder()
            .id(1L).cspCode(CspCode.SALA)
            .rateMin(Rate.of(new BigDecimal("5.00"))).rateMax(Rate.of(new BigDecimal("6.00")))
            .durationRange(DurationRange.of(12, 240)).active(true).build();

        String changes = after.describeChangesFrom(before);

        assertTrue(changes.contains("rateMax"));
        assertTrue(!changes.contains("rateMin:"));
    }

    @Test
    void shouldDescribeRateMinBecomingUnset() {
        CreditRateModel before = CreditRateModel.getBuilder()
            .id(1L).rateMin(Rate.of(new BigDecimal("5.00"))).build();
        CreditRateModel after = CreditRateModel.getBuilder().id(1L).build();

        String changes = after.describeChangesFrom(before);

        assertTrue(changes.contains("rateMin"));
    }

    @Test
    void shouldHaveToString() {
        CreditRateModel model = CreditRateModel.getBuilder()
            .id(1L).cspCode(CspCode.SALA).durationRange(DurationRange.of(12, 300)).build();
        String str = model.toString();
        assertTrue(str.contains("SALA"));
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
        assertTrue(!model.equals("other"));
    }
}