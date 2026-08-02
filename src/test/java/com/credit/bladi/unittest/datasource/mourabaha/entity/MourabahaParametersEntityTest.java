package com.credit.bladi.unittest.datasource.mourabaha.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.datasource.mourabaha.entity.MourabahaParametersEntity;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class MourabahaParametersEntityTest {

    @Test
    void shouldCreateWithDefaultConstructor() {
        MourabahaParametersEntity entity = new MourabahaParametersEntity();
        assertNotNull(entity);
        assertNull(entity.getId());
        assertNull(entity.getProfitRate());
    }

    @Test
    void shouldSetAndGetAllFields() {
        MourabahaParametersEntity entity = new MourabahaParametersEntity();
        entity.setId(1L);
        entity.setProfitRate(new BigDecimal("5.25"));
        entity.setVatRate(new BigDecimal("0.10"));
        entity.setActive(true);

        assertEquals(1L, entity.getId());
        assertEquals(new BigDecimal("5.25"), entity.getProfitRate());
        assertEquals(new BigDecimal("0.10"), entity.getVatRate());
        assertTrue(entity.getActive());
    }

    @Test
    void shouldMergeChangedFieldsOnly() {
        MourabahaParametersEntity current = new MourabahaParametersEntity();
        current.setId(1L);
        current.setProfitRate(new BigDecimal("5.00"));
        current.setVatRate(new BigDecimal("0.10"));
        current.setActive(false);

        MourabahaParametersEntity incoming = new MourabahaParametersEntity();
        incoming.setProfitRate(new BigDecimal("5.25"));

        current.merge(incoming);

        assertEquals(0, new BigDecimal("5.25").compareTo(current.getProfitRate()));
        assertEquals(0, new BigDecimal("0.10").compareTo(current.getVatRate()));
        assertFalse(current.getActive());
    }

    @Test
    void shouldTreatNonBigDecimalCurrentAsUnequalViaReflection() throws Exception {
        java.lang.reflect.Method method = MourabahaParametersEntity.class.getDeclaredMethod(
            "valuesEqual", Object.class, Object.class);
        method.setAccessible(true);

        boolean result = (boolean) method.invoke(null, new BigDecimal("5.25"), "not-a-bigdecimal");

        assertFalse(result);
    }

    @Test
    void shouldMergeIntoUnsetField() {
        MourabahaParametersEntity current = new MourabahaParametersEntity();

        MourabahaParametersEntity incoming = new MourabahaParametersEntity();
        incoming.setProfitRate(new BigDecimal("5.25"));

        current.merge(incoming);

        assertEquals(0, new BigDecimal("5.25").compareTo(current.getProfitRate()));
    }

    @Test
    void shouldNotChangeFieldWhenIncomingValueEqualsCurrent() {
        MourabahaParametersEntity current = new MourabahaParametersEntity();
        current.setProfitRate(new BigDecimal("5.250"));

        MourabahaParametersEntity incoming = new MourabahaParametersEntity();
        incoming.setProfitRate(new BigDecimal("5.25"));

        current.merge(incoming);

        assertEquals(new BigDecimal("5.250"), current.getProfitRate());
    }

    @Test
    void shouldMergeActiveTrueFromIncoming() {
        MourabahaParametersEntity current = new MourabahaParametersEntity();
        current.setActive(false);

        MourabahaParametersEntity incoming = new MourabahaParametersEntity();
        incoming.setActive(true);

        current.merge(incoming);

        assertTrue(current.getActive());
    }

    @Test
    void shouldBeEqualToItself() {
        MourabahaParametersEntity entity = new MourabahaParametersEntity();
        entity.setId(1L);
        assertTrue(entity.equals(entity));
    }

    @Test
    void shouldBeEqualWhenSameId() {
        MourabahaParametersEntity e1 = new MourabahaParametersEntity();
        e1.setId(1L);
        MourabahaParametersEntity e2 = new MourabahaParametersEntity();
        e2.setId(1L);
        assertTrue(e1.equals(e2));
    }

    @Test
    void shouldNotBeEqualWhenDifferentId() {
        MourabahaParametersEntity e1 = new MourabahaParametersEntity();
        e1.setId(1L);
        MourabahaParametersEntity e2 = new MourabahaParametersEntity();
        e2.setId(2L);
        assertFalse(e1.equals(e2));
    }

    @Test
    void shouldNotBeEqualWhenNullId() {
        MourabahaParametersEntity e1 = new MourabahaParametersEntity();
        MourabahaParametersEntity e2 = new MourabahaParametersEntity();
        assertFalse(e1.equals(e2));
    }

    @Test
    void shouldNotBeEqualToDifferentType() {
        MourabahaParametersEntity entity = new MourabahaParametersEntity();
        entity.setId(1L);
        assertFalse(entity.equals("other"));
    }

    @Test
    void shouldHaveHashCode() {
        MourabahaParametersEntity entity = new MourabahaParametersEntity();
        entity.setId(1L);
        assertNotNull(entity.hashCode());
    }

    @Test
    void shouldHaveToString() {
        MourabahaParametersEntity entity = new MourabahaParametersEntity();
        entity.setId(1L);
        entity.setProfitRate(new BigDecimal("5.25"));
        entity.setActive(true);
        String str = entity.toString();
        assertTrue(str.contains("1"));
        assertTrue(str.contains("5.25"));
        assertTrue(str.contains("true"));
    }
}