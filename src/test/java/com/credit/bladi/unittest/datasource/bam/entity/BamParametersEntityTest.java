package com.credit.bladi.unittest.datasource.bam.entity;

import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_20000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_5000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_6000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_01;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_015;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_02;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_OTHER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.common.MergeSupport;
import com.credit.bladi.datasource.bam.entity.BamParametersEntity;
import org.junit.jupiter.api.Test;

class BamParametersEntityTest {
    private BamParametersEntity buildEntity(Long id) {
        BamParametersEntity entity = new BamParametersEntity();
        entity.setId(id);
        entity.setProcessingFeesRatio(DECIMAL_0_01);
        entity.setMinProcessingFees(AMOUNT_5000);
        entity.setMaxProcessingFees(AMOUNT_20000);
        entity.setRegistrationFeeRatio(DECIMAL_0_015);
        entity.setLandConservationRatio(DECIMAL_0_015);
        entity.setNotaryFeeRatio(DECIMAL_0_01);
        entity.setActive(true);
        return entity;
    }

    @Test
    void shouldCreateWithDefaultConstructor() {
        BamParametersEntity entity = new BamParametersEntity();
        assertNotNull(entity);
        assertNull(entity.getId());
    }

    @Test
    void shouldSetAndGetAllFields() {
        BamParametersEntity entity = buildEntity(1L);
        assertEquals(1L, entity.getId());
        assertEquals(DECIMAL_0_01, entity.getProcessingFeesRatio());
        assertEquals(AMOUNT_5000, entity.getMinProcessingFees());
        assertEquals(AMOUNT_20000, entity.getMaxProcessingFees());
        assertEquals(DECIMAL_0_015, entity.getRegistrationFeeRatio());
        assertEquals(DECIMAL_0_015, entity.getLandConservationRatio());
        assertEquals(DECIMAL_0_01, entity.getNotaryFeeRatio());
        assertTrue(entity.getActive());
    }

    @Test
    void shouldMergeNonNullDifferentValues() {
        BamParametersEntity existing = buildEntity(1L);
        existing.setProcessingFeesRatio(DECIMAL_0_01);

        BamParametersEntity incoming = new BamParametersEntity();
        incoming.setProcessingFeesRatio(DECIMAL_0_02);
        incoming.setMinProcessingFees(AMOUNT_6000);

        existing.merge(incoming);

        assertEquals(DECIMAL_0_02, existing.getProcessingFeesRatio());
        assertEquals(AMOUNT_6000, existing.getMinProcessingFees());
    }

    @Test
    void shouldTreatNonBigDecimalCurrentAsUnequalViaMergeSupport() {
        boolean result = MergeSupport.valuesEqual(DECIMAL_0_01, "not-a-bigdecimal");

        assertFalse(result);
    }

    @Test
    void shouldMergeIntoUnsetField() {
        BamParametersEntity existing = new BamParametersEntity();

        BamParametersEntity incoming = new BamParametersEntity();
        incoming.setProcessingFeesRatio(DECIMAL_0_02);

        existing.merge(incoming);

        assertEquals(DECIMAL_0_02, existing.getProcessingFeesRatio());
    }

    @Test
    void shouldNotMergeNullValues() {
        BamParametersEntity existing = buildEntity(1L);
        existing.setRegistrationFeeRatio(DECIMAL_0_015);

        BamParametersEntity incoming = new BamParametersEntity();
        incoming.setRegistrationFeeRatio(null);

        existing.merge(incoming);

        assertEquals(DECIMAL_0_015, existing.getRegistrationFeeRatio());
    }

    @Test
    void shouldNotMergeEqualValues() {
        BamParametersEntity existing = buildEntity(1L);
        existing.setNotaryFeeRatio(DECIMAL_0_01);

        BamParametersEntity incoming = new BamParametersEntity();
        incoming.setNotaryFeeRatio(DECIMAL_0_01);

        existing.merge(incoming);

        assertEquals(DECIMAL_0_01, existing.getNotaryFeeRatio());
    }

    @Test
    void shouldBeEqualToItself() {
        BamParametersEntity entity = buildEntity(1L);
        assertTrue(entity.equals(entity));
    }

    @Test
    void shouldBeEqualWhenSameId() {
        BamParametersEntity e1 = buildEntity(1L);
        BamParametersEntity e2 = buildEntity(1L);
        assertTrue(e1.equals(e2));
    }

    @Test
    void shouldNotBeEqualWhenDifferentId() {
        BamParametersEntity e1 = buildEntity(1L);
        BamParametersEntity e2 = buildEntity(2L);
        assertFalse(e1.equals(e2));
    }

    @Test
    void shouldNotBeEqualWhenNullId() {
        BamParametersEntity e1 = new BamParametersEntity();
        BamParametersEntity e2 = new BamParametersEntity();
        assertFalse(e1.equals(e2));
    }

    @Test
    void shouldNotBeEqualToDifferentType() {
        BamParametersEntity entity = buildEntity(1L);
        assertFalse(entity.equals(USER_OTHER));
    }

    @Test
    void shouldHaveHashCode() {
        BamParametersEntity entity = buildEntity(1L);
        assertNotNull(entity.hashCode());
    }

    @Test
    void shouldHaveToString() {
        BamParametersEntity entity = buildEntity(1L);
        String str = entity.toString();
        assertTrue(str.contains("1"));
        assertTrue(str.contains("true"));
    }
}
