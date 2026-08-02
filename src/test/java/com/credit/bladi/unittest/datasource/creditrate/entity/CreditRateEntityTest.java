package com.credit.bladi.unittest.datasource.creditrate.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.datasource.creditrate.entity.CreditRateEntity;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class CreditRateEntityTest {

    @Test
    void shouldCreateWithDefaultConstructor() {
        CreditRateEntity entity = new CreditRateEntity();
        assertNotNull(entity);
        assertNull(entity.getId());
        assertNull(entity.getCspCode());
    }

    @Test
    void shouldSetAndGetAllFields() {
        CreditRateEntity entity = new CreditRateEntity();
        entity.setId(1L);
        entity.setCspCode("SALA");
        entity.setRateMin(new BigDecimal("3.50"));
        entity.setRateMax(new BigDecimal("5.50"));
        entity.setMinDurationMonths(12);
        entity.setMaxDurationMonths(300);
        entity.setActive(true);
        entity.setVersion(3L);

        assertEquals(1L, entity.getId());
        assertEquals("SALA", entity.getCspCode());
        assertEquals(new BigDecimal("3.50"), entity.getRateMin());
        assertEquals(new BigDecimal("5.50"), entity.getRateMax());
        assertEquals(12, entity.getMinDurationMonths());
        assertEquals(300, entity.getMaxDurationMonths());
        assertTrue(entity.getActive());
        assertEquals(3L, entity.getVersion());
    }

    @Test
    void shouldTreatNonBigDecimalCurrentAsUnequalViaReflection() throws Exception {
        java.lang.reflect.Method method = CreditRateEntity.class.getDeclaredMethod(
            "valuesEqual", Object.class, Object.class);
        method.setAccessible(true);

        boolean result = (boolean) method.invoke(null, new BigDecimal("1.00"), "not-a-bigdecimal");

        assertFalse(result);
    }

    @Test
    void shouldMergeChangedRateFields() {
        CreditRateEntity current = new CreditRateEntity();
        current.setRateMin(new BigDecimal("3.50"));
        current.setRateMax(new BigDecimal("5.50"));

        CreditRateEntity incoming = new CreditRateEntity();
        incoming.setRateMin(new BigDecimal("4.00"));
        incoming.setRateMax(new BigDecimal("5.50"));

        current.merge(incoming);

        assertEquals(0, new BigDecimal("4.00").compareTo(current.getRateMin()));
        assertEquals(0, new BigDecimal("5.50").compareTo(current.getRateMax()));
    }

    @Test
    void shouldMergeRateIntoUnsetField() {
        CreditRateEntity current = new CreditRateEntity();

        CreditRateEntity incoming = new CreditRateEntity();
        incoming.setRateMin(new BigDecimal("3.50"));

        current.merge(incoming);

        assertEquals(0, new BigDecimal("3.50").compareTo(current.getRateMin()));
    }

    @Test
    void shouldMergeChangedDurationAndActiveFields() {
        CreditRateEntity current = new CreditRateEntity();
        current.setId(1L);
        current.setMinDurationMonths(12);
        current.setMaxDurationMonths(240);
        current.setActive(false);

        CreditRateEntity incoming = new CreditRateEntity();
        incoming.setMinDurationMonths(24);
        incoming.setMaxDurationMonths(300);
        incoming.setActive(true);

        current.merge(incoming);

        assertEquals(24, current.getMinDurationMonths());
        assertEquals(300, current.getMaxDurationMonths());
        assertTrue(current.getActive());
    }

    @Test
    void shouldNotChangeDurationWhenIncomingValueEqualsCurrent() {
        CreditRateEntity current = new CreditRateEntity();
        current.setMinDurationMonths(12);
        current.setActive(true);

        CreditRateEntity incoming = new CreditRateEntity();
        incoming.setMinDurationMonths(12);
        incoming.setActive(true);

        current.merge(incoming);

        assertEquals(12, current.getMinDurationMonths());
        assertTrue(current.getActive());
    }

    @Test
    void shouldBeEqualToItself() {
        CreditRateEntity entity = new CreditRateEntity();
        entity.setId(1L);
        assertTrue(entity.equals(entity));
    }

    @Test
    void shouldBeEqualWhenSameId() {
        CreditRateEntity e1 = new CreditRateEntity();
        e1.setId(1L);
        CreditRateEntity e2 = new CreditRateEntity();
        e2.setId(1L);
        assertTrue(e1.equals(e2));
    }

    @Test
    void shouldNotBeEqualWhenDifferentId() {
        CreditRateEntity e1 = new CreditRateEntity();
        e1.setId(1L);
        CreditRateEntity e2 = new CreditRateEntity();
        e2.setId(2L);
        assertFalse(e1.equals(e2));
    }

    @Test
    void shouldNotBeEqualWhenNullId() {
        CreditRateEntity e1 = new CreditRateEntity();
        CreditRateEntity e2 = new CreditRateEntity();
        assertFalse(e1.equals(e2));
    }

    @Test
    void shouldNotBeEqualToDifferentType() {
        CreditRateEntity entity = new CreditRateEntity();
        entity.setId(1L);
        assertFalse(entity.equals("other"));
    }

    @Test
    void shouldHaveHashCode() {
        CreditRateEntity entity = new CreditRateEntity();
        entity.setId(1L);
        assertNotNull(entity.hashCode());
    }

    @Test
    void shouldHaveToString() {
        CreditRateEntity entity = new CreditRateEntity();
        entity.setId(1L);
        entity.setCspCode("SALA");
        entity.setActive(true);
        String str = entity.toString();
        assertTrue(str.contains("1"));
        assertTrue(str.contains("SALA"));
        assertTrue(str.contains("true"));
    }
}