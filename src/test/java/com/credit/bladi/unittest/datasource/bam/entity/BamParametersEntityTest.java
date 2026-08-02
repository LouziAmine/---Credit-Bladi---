package com.credit.bladi.unittest.datasource.bam.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.datasource.bam.entity.BamParametersEntity;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class BamParametersEntityTest {

    private BamParametersEntity buildEntity(Long id) {
        BamParametersEntity entity = new BamParametersEntity();
        entity.setId(id);
        entity.setProcessingFeesRatio(new BigDecimal("0.01"));
        entity.setMinProcessingFees(new BigDecimal("5000"));
        entity.setMaxProcessingFees(new BigDecimal("20000"));
        entity.setRegistrationFeeRatio(new BigDecimal("0.015"));
        entity.setLandConservationRatio(new BigDecimal("0.015"));
        entity.setNotaryFeeRatio(new BigDecimal("0.01"));
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
        assertEquals(new BigDecimal("0.01"), entity.getProcessingFeesRatio());
        assertEquals(new BigDecimal("5000"), entity.getMinProcessingFees());
        assertEquals(new BigDecimal("20000"), entity.getMaxProcessingFees());
        assertEquals(new BigDecimal("0.015"), entity.getRegistrationFeeRatio());
        assertEquals(new BigDecimal("0.015"), entity.getLandConservationRatio());
        assertEquals(new BigDecimal("0.01"), entity.getNotaryFeeRatio());
        assertTrue(entity.getActive());
    }

    @Test
    void shouldMergeNonNullDifferentValues() {
        BamParametersEntity existing = buildEntity(1L);
        existing.setProcessingFeesRatio(new BigDecimal("0.01"));

        BamParametersEntity incoming = new BamParametersEntity();
        incoming.setProcessingFeesRatio(new BigDecimal("0.02"));
        incoming.setMinProcessingFees(new BigDecimal("6000"));

        existing.merge(incoming);

        assertEquals(new BigDecimal("0.02"), existing.getProcessingFeesRatio());
        assertEquals(new BigDecimal("6000"), existing.getMinProcessingFees());
    }

    @Test
    void shouldTreatNonBigDecimalCurrentAsUnequalViaReflection() throws Exception {
        java.lang.reflect.Method method = BamParametersEntity.class.getDeclaredMethod(
            "valuesEqual", Object.class, Object.class);
        method.setAccessible(true);

        boolean result = (boolean) method.invoke(null, new BigDecimal("0.01"), "not-a-bigdecimal");

        assertFalse(result);
    }

    @Test
    void shouldMergeIntoUnsetField() {
        BamParametersEntity existing = new BamParametersEntity();

        BamParametersEntity incoming = new BamParametersEntity();
        incoming.setProcessingFeesRatio(new BigDecimal("0.02"));

        existing.merge(incoming);

        assertEquals(new BigDecimal("0.02"), existing.getProcessingFeesRatio());
    }

    @Test
    void shouldNotMergeNullValues() {
        BamParametersEntity existing = buildEntity(1L);
        existing.setRegistrationFeeRatio(new BigDecimal("0.015"));

        BamParametersEntity incoming = new BamParametersEntity();
        incoming.setRegistrationFeeRatio(null);

        existing.merge(incoming);

        assertEquals(new BigDecimal("0.015"), existing.getRegistrationFeeRatio());
    }

    @Test
    void shouldNotMergeEqualValues() {
        BamParametersEntity existing = buildEntity(1L);
        existing.setNotaryFeeRatio(new BigDecimal("0.01"));

        BamParametersEntity incoming = new BamParametersEntity();
        incoming.setNotaryFeeRatio(new BigDecimal("0.01"));

        existing.merge(incoming);

        assertEquals(new BigDecimal("0.01"), existing.getNotaryFeeRatio());
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
        assertFalse(entity.equals("other"));
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