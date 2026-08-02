package com.credit.bladi.unittest.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.model.mourabaha.MourabahaParametersModel;
import com.credit.bladi.model.vo.Rate;
import com.credit.bladi.model.vo.Ratio;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class MourabahaParametersModelTest {

    @Test
    void shouldAllowNullFieldsForPartialPatch() {
        MourabahaParametersModel model = MourabahaParametersModel.getBuilder().id(1L).build();

        assertEquals(1L, model.getId());
        assertNull(model.getProfitRate());
        assertNull(model.getVatRate());
        assertNull(model.getActive());
    }

    @Test
    void shouldBeEqualToItself() {
        MourabahaParametersModel model = MourabahaParametersModel.getBuilder().id(1L).build();
        assertTrue(model.equals(model));
    }

    @Test
    void shouldNotBeEqualWhenDifferentId() {
        MourabahaParametersModel m1 = MourabahaParametersModel.getBuilder().id(1L).build();
        MourabahaParametersModel m2 = MourabahaParametersModel.getBuilder().id(2L).build();
        assertFalse(m1.equals(m2));
    }

    @Test
    void shouldNotBeEqualWhenNullId() {
        MourabahaParametersModel m1 = MourabahaParametersModel.getBuilder().build();
        MourabahaParametersModel m2 = MourabahaParametersModel.getBuilder().build();
        assertFalse(m1.equals(m2));
    }

    @Test
    void shouldNotBeEqualToDifferentType() {
        MourabahaParametersModel model = MourabahaParametersModel.getBuilder().id(1L).build();
        assertFalse(model.equals("other"));
    }

    @Test
    void shouldDescribeNoChangesWhenNothingChanged() {
        MourabahaParametersModel before = MourabahaParametersModel.getBuilder()
            .id(1L)
            .profitRate(Rate.of(new BigDecimal("5.00")))
            .vatRate(Ratio.of(new BigDecimal("0.10")))
            .active(true)
            .build();
        MourabahaParametersModel after = MourabahaParametersModel.getBuilder()
            .id(1L)
            .profitRate(Rate.of(new BigDecimal("5.00")))
            .vatRate(Ratio.of(new BigDecimal("0.10")))
            .active(true)
            .build();

        assertEquals("", after.describeChangesFrom(before));
    }

    @Test
    void shouldDescribeProfitRateAndVatRateBecomingUnset() {
        MourabahaParametersModel before = MourabahaParametersModel.getBuilder()
            .id(1L)
            .profitRate(Rate.of(new BigDecimal("5.00")))
            .vatRate(Ratio.of(new BigDecimal("0.10")))
            .build();
        MourabahaParametersModel after = MourabahaParametersModel.getBuilder().id(1L).build();

        String changes = after.describeChangesFrom(before);

        assertTrue(changes.contains("profitRate"));
        assertTrue(changes.contains("vatRate"));
    }

    @Test
    void shouldDescribeActiveChange() {
        MourabahaParametersModel before = MourabahaParametersModel.getBuilder().id(1L).active(false).build();
        MourabahaParametersModel after = MourabahaParametersModel.getBuilder().id(1L).active(true).build();

        String changes = after.describeChangesFrom(before);

        assertTrue(changes.contains("active"));
        assertTrue(changes.contains("false"));
        assertTrue(changes.contains("true"));
    }

    @Test
    void shouldBuildWithAllFields() {
        MourabahaParametersModel model = MourabahaParametersModel.getBuilder()
            .id(1L)
            .profitRate(Rate.of(new BigDecimal("5.25")))
            .vatRate(Ratio.of(new BigDecimal("0.10")))
            .active(true)
            .build();

        assertNotNull(model);
        assertEquals(1L, model.getId());
        assertEquals(Rate.of(new BigDecimal("5.25")), model.getProfitRate());
        assertEquals(Ratio.of(new BigDecimal("0.10")), model.getVatRate());
        assertTrue(model.getActive());
    }

    @Test
    void shouldBeEqualWhenSameId() {
        MourabahaParametersModel m1 = MourabahaParametersModel.getBuilder().id(1L).active(true).build();
        MourabahaParametersModel m2 = MourabahaParametersModel.getBuilder().id(1L).active(true).build();

        assertEquals(m1, m2);
    }

    @Test
    void shouldHaveHashCode() {
        MourabahaParametersModel model = MourabahaParametersModel.getBuilder().id(1L).build();
        assertNotNull(model.hashCode());
    }

    @Test
    void shouldHaveToString() {
        MourabahaParametersModel model = MourabahaParametersModel.getBuilder().id(1L).active(true).build();
        String str = model.toString();
        assertTrue(str.contains("1"));
        assertTrue(str.contains("true"));
    }

    @Test
    void shouldDescribeChangesFromPrevious() {
        MourabahaParametersModel before = MourabahaParametersModel.getBuilder()
            .id(1L)
            .profitRate(Rate.of(new BigDecimal("5.00")))
            .vatRate(Ratio.of(new BigDecimal("0.10")))
            .active(true)
            .build();
        MourabahaParametersModel after = MourabahaParametersModel.getBuilder()
            .id(1L)
            .profitRate(Rate.of(new BigDecimal("5.25")))
            .vatRate(Ratio.of(new BigDecimal("0.10")))
            .active(true)
            .build();

        String changes = after.describeChangesFrom(before);

        assertTrue(changes.contains("profitRate"));
        assertTrue(changes.contains("5.00"));
        assertTrue(changes.contains("5.25"));
    }
}
