package com.credit.bladi.unittest.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.model.bam.BamParametersModel;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Ratio;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class BamParametersModelTest {

    @Test
    void shouldBuildWithAllFields() {
        BamParametersModel model = BamParametersModel.getBuilder()
            .id(1L)
            .processingFeesRatio(Ratio.of(new BigDecimal("0.01")))
            .minProcessingFees(Money.of(new BigDecimal("5000")))
            .maxProcessingFees(Money.of(new BigDecimal("20000")))
            .registrationFeeRatio(Ratio.of(new BigDecimal("0.015")))
            .landConservationRatio(Ratio.of(new BigDecimal("0.015")))
            .notaryFeeRatio(Ratio.of(new BigDecimal("0.01")))
            .active(true)
            .build();

        assertNotNull(model);
        assertEquals(1L, model.getId());
        assertEquals(Ratio.of(new BigDecimal("0.01")), model.getProcessingFeesRatio());
        assertEquals(0, model.getMinProcessingFees().amount().compareTo(new BigDecimal("5000")));
        assertEquals(0, model.getMaxProcessingFees().amount().compareTo(new BigDecimal("20000")));
        assertEquals(Ratio.of(new BigDecimal("0.015")), model.getRegistrationFeeRatio());
        assertEquals(Ratio.of(new BigDecimal("0.015")), model.getLandConservationRatio());
        assertEquals(Ratio.of(new BigDecimal("0.01")), model.getNotaryFeeRatio());
        assertTrue(model.getActive());
    }

    @Test
    void shouldBeEqualWhenSameFields() {
        BamParametersModel m1 = BamParametersModel.getBuilder().id(1L).active(true).build();
        BamParametersModel m2 = BamParametersModel.getBuilder().id(1L).active(true).build();

        assertEquals(m1, m2);
    }

    @Test
    void shouldHaveHashCode() {
        BamParametersModel model = BamParametersModel.getBuilder().id(1L).build();
        assertNotNull(model.hashCode());
    }

    @Test
    void shouldHaveToString() {
        BamParametersModel model = BamParametersModel.getBuilder().id(1L).active(true).build();
        String str = model.toString();
        assertTrue(str.contains("1"));
        assertTrue(str.contains("true"));
    }

    @Test
    void shouldDescribeNoChangesWhenAllOptionalFieldsAreNullOnBothSides() {
        BamParametersModel before = BamParametersModel.getBuilder().id(1L).active(true).build();
        BamParametersModel after = BamParametersModel.getBuilder().id(1L).active(true).build();

        assertEquals("", after.describeChangesFrom(before));
    }

    @Test
    void shouldDescribeMinProcessingFeesChange() {
        BamParametersModel before = BamParametersModel.getBuilder()
            .id(1L)
            .minProcessingFees(Money.of(new BigDecimal("1000")))
            .build();
        BamParametersModel after = BamParametersModel.getBuilder()
            .id(1L)
            .minProcessingFees(Money.of(new BigDecimal("2000")))
            .build();

        String changes = after.describeChangesFrom(before);

        assertTrue(changes.contains("minProcessingFees"));
    }

    @Test
    void shouldDescribeMinProcessingFeesAndRatioBecomingUnset() {
        BamParametersModel before = BamParametersModel.getBuilder()
            .id(1L)
            .minProcessingFees(Money.of(new BigDecimal("1000")))
            .processingFeesRatio(Ratio.of(new BigDecimal("0.01")))
            .build();
        BamParametersModel after = BamParametersModel.getBuilder().id(1L).build();

        String changes = after.describeChangesFrom(before);

        assertTrue(changes.contains("minProcessingFees"));
        assertTrue(changes.contains("processingFeesRatio"));
    }

    @Test
    void shouldDescribeNoChangeWhenMinProcessingFeesEqual() {
        BamParametersModel before = BamParametersModel.getBuilder()
            .id(1L)
            .minProcessingFees(Money.of(new BigDecimal("1000")))
            .build();
        BamParametersModel after = BamParametersModel.getBuilder()
            .id(1L)
            .minProcessingFees(Money.of(new BigDecimal("1000.00")))
            .build();

        assertEquals("", after.describeChangesFrom(before));
    }

    @Test
    void shouldNotBeEqualWhenDifferentId() {
        BamParametersModel m1 = BamParametersModel.getBuilder().id(1L).build();
        BamParametersModel m2 = BamParametersModel.getBuilder().id(2L).build();
        assertFalse(m1.equals(m2));
    }

    @Test
    void shouldNotBeEqualWhenNullId() {
        BamParametersModel m1 = BamParametersModel.getBuilder().build();
        BamParametersModel m2 = BamParametersModel.getBuilder().build();
        assertFalse(m1.equals(m2));
    }

    @Test
    void shouldTreatEqualBigDecimalsAsEqualViaReflection() throws Exception {
        java.lang.reflect.Method method = BamParametersModel.class.getDeclaredMethod(
            "valuesEqual", Object.class, Object.class);
        method.setAccessible(true);

        assertTrue((boolean) method.invoke(null, new BigDecimal("5.00"), new BigDecimal("5.000")));
        assertFalse((boolean) method.invoke(null, new BigDecimal("5.00"), new BigDecimal("6.00")));
        assertFalse((boolean) method.invoke(null, new BigDecimal("5.00"), "not-a-bigdecimal"));
    }

    @Test
    void shouldNotBeEqualToDifferentType() {
        BamParametersModel model = BamParametersModel.getBuilder().id(1L).build();
        assertFalse(model.equals("other"));
    }

    @Test
    void shouldRejectProcessingFeesRatioAboveMax() {
        assertThrows(IllegalArgumentException.class, () -> BamParametersModel.getBuilder()
            .processingFeesRatio(Ratio.of(new BigDecimal("0.05")))
            .build());
    }

    @Test
    void shouldRejectMaxAgeAtMaturityAboveCeiling() {
        assertThrows(IllegalArgumentException.class, () -> BamParametersModel.getBuilder()
            .maxAgeAtMaturity(100)
            .build());
    }

    @Test
    void shouldRejectMaxAgeAtMaturityBelowOne() {
        assertThrows(IllegalArgumentException.class, () -> BamParametersModel.getBuilder()
            .maxAgeAtMaturity(0)
            .build());
    }
}