package com.credit.bladi.unittest.model;

import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_1000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_2000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_20000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_5000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_01;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_015;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_05;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_1000_00;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_5_00;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_5_000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_6_00;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_OTHER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.common.MergeSupport;
import com.credit.bladi.model.bam.BamParametersModel;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Ratio;
import org.junit.jupiter.api.Test;

class BamParametersModelTest {
    @Test
    void shouldBuildWithAllFields() {
        BamParametersModel model = BamParametersModel.getBuilder()
            .id(1L)
            .processingFeesRatio(Ratio.of(DECIMAL_0_01))
            .minProcessingFees(Money.of(AMOUNT_5000))
            .maxProcessingFees(Money.of(AMOUNT_20000))
            .registrationFeeRatio(Ratio.of(DECIMAL_0_015))
            .landConservationRatio(Ratio.of(DECIMAL_0_015))
            .notaryFeeRatio(Ratio.of(DECIMAL_0_01))
            .active(true)
            .build();

        assertNotNull(model);
        assertEquals(1L, model.getId());
        assertEquals(Ratio.of(DECIMAL_0_01), model.getProcessingFeesRatio());
        assertEquals(0, model.getMinProcessingFees().amount().compareTo(AMOUNT_5000));
        assertEquals(0, model.getMaxProcessingFees().amount().compareTo(AMOUNT_20000));
        assertEquals(Ratio.of(DECIMAL_0_015), model.getRegistrationFeeRatio());
        assertEquals(Ratio.of(DECIMAL_0_015), model.getLandConservationRatio());
        assertEquals(Ratio.of(DECIMAL_0_01), model.getNotaryFeeRatio());
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
            .minProcessingFees(Money.of(AMOUNT_1000))
            .build();
        BamParametersModel after = BamParametersModel.getBuilder()
            .id(1L)
            .minProcessingFees(Money.of(AMOUNT_2000))
            .build();

        String changes = after.describeChangesFrom(before);

        assertTrue(changes.contains("minProcessingFees"));
    }

    @Test
    void shouldDescribeMinProcessingFeesAndRatioBecomingUnset() {
        BamParametersModel before = BamParametersModel.getBuilder()
            .id(1L)
            .minProcessingFees(Money.of(AMOUNT_1000))
            .processingFeesRatio(Ratio.of(DECIMAL_0_01))
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
            .minProcessingFees(Money.of(AMOUNT_1000))
            .build();
        BamParametersModel after = BamParametersModel.getBuilder()
            .id(1L)
            .minProcessingFees(Money.of(DECIMAL_1000_00))
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
    void shouldTreatEqualBigDecimalsAsEqualViaMergeSupport() {
        assertTrue(MergeSupport.valuesEqual(DECIMAL_5_00, DECIMAL_5_000));
        assertFalse(MergeSupport.valuesEqual(DECIMAL_5_00, DECIMAL_6_00));
        assertFalse(MergeSupport.valuesEqual(DECIMAL_5_00, "not-a-bigdecimal"));
    }

    @Test
    void shouldNotBeEqualToDifferentType() {
        BamParametersModel model = BamParametersModel.getBuilder().id(1L).build();
        assertFalse(model.equals(USER_OTHER));
    }

    @Test
    void shouldRejectProcessingFeesRatioAboveMax() {
        assertThrows(IllegalArgumentException.class, () -> BamParametersModel.getBuilder()
            .processingFeesRatio(Ratio.of(DECIMAL_0_05))
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
