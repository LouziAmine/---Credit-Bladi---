package com.credit.bladi.unittest.datasource.mourabaha.entity;

import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_10;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_5_00;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_5_25;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_5_250;
import static com.credit.bladi.unittest.mock.data.MockAmounts.TEXT_5_25;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_OTHER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.common.MergeSupport;
import com.credit.bladi.datasource.mourabaha.entity.MourabahaParametersEntity;
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
        entity.setProfitRate(DECIMAL_5_25);
        entity.setVatRate(DECIMAL_0_10);
        entity.setActive(true);

        assertEquals(1L, entity.getId());
        assertEquals(DECIMAL_5_25, entity.getProfitRate());
        assertEquals(DECIMAL_0_10, entity.getVatRate());
        assertTrue(entity.getActive());
    }

    @Test
    void shouldMergeChangedFieldsOnly() {
        MourabahaParametersEntity current = new MourabahaParametersEntity();
        current.setId(1L);
        current.setProfitRate(DECIMAL_5_00);
        current.setVatRate(DECIMAL_0_10);
        current.setActive(false);

        MourabahaParametersEntity incoming = new MourabahaParametersEntity();
        incoming.setProfitRate(DECIMAL_5_25);

        current.merge(incoming);

        assertEquals(0, DECIMAL_5_25.compareTo(current.getProfitRate()));
        assertEquals(0, DECIMAL_0_10.compareTo(current.getVatRate()));
        assertFalse(current.getActive());
    }

    @Test
    void shouldTreatNonBigDecimalCurrentAsUnequalViaMergeSupport() {
        boolean result = MergeSupport.valuesEqual(DECIMAL_5_25, "not-a-bigdecimal");

        assertFalse(result);
    }

    @Test
    void shouldMergeIntoUnsetField() {
        MourabahaParametersEntity current = new MourabahaParametersEntity();

        MourabahaParametersEntity incoming = new MourabahaParametersEntity();
        incoming.setProfitRate(DECIMAL_5_25);

        current.merge(incoming);

        assertEquals(0, DECIMAL_5_25.compareTo(current.getProfitRate()));
    }

    @Test
    void shouldNotChangeFieldWhenIncomingValueEqualsCurrent() {
        MourabahaParametersEntity current = new MourabahaParametersEntity();
        current.setProfitRate(DECIMAL_5_250);

        MourabahaParametersEntity incoming = new MourabahaParametersEntity();
        incoming.setProfitRate(DECIMAL_5_25);

        current.merge(incoming);

        assertEquals(DECIMAL_5_250, current.getProfitRate());
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
        assertFalse(entity.equals(USER_OTHER));
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
        entity.setProfitRate(DECIMAL_5_25);
        entity.setActive(true);
        String str = entity.toString();
        assertTrue(str.contains("1"));
        assertTrue(str.contains(TEXT_5_25));
        assertTrue(str.contains("true"));
    }
}
