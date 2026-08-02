package com.credit.bladi.unittest.datasource.auth.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.datasource.auth.entity.RevokedTokenEntity;
import org.junit.jupiter.api.Test;

class RevokedTokenEntityTest {

    @Test
    void shouldCreateWithDefaultConstructor() {
        RevokedTokenEntity entity = new RevokedTokenEntity();
        assertNotNull(entity);
        assertNull(entity.getId());
        assertNull(entity.getTokenHash());
    }

    @Test
    void shouldCreateWithAllArgsConstructor() {
        RevokedTokenEntity entity = new RevokedTokenEntity("hash123", 9999999L);
        assertEquals("hash123", entity.getTokenHash());
        assertEquals(9999999L, entity.getExpiresAt());
    }

    @Test
    void shouldSetAndGetId() {
        RevokedTokenEntity entity = new RevokedTokenEntity();
        entity.setId(42L);
        assertEquals(42L, entity.getId());
    }

    @Test
    void shouldSetAndGetToken() {
        RevokedTokenEntity entity = new RevokedTokenEntity();
        entity.setTokenHash("revokedhash");
        assertEquals("revokedhash", entity.getTokenHash());
    }

    @Test
    void shouldSetAndGetExpiresAt() {
        RevokedTokenEntity entity = new RevokedTokenEntity();
        entity.setExpiresAt(123456789L);
        assertEquals(123456789L, entity.getExpiresAt());
    }

    @Test
    void shouldBeEqualToItself() {
        RevokedTokenEntity entity = new RevokedTokenEntity();
        entity.setId(1L);
        assertTrue(entity.equals(entity));
    }

    @Test
    void shouldBeEqualWhenSameId() {
        RevokedTokenEntity e1 = new RevokedTokenEntity();
        e1.setId(1L);
        RevokedTokenEntity e2 = new RevokedTokenEntity();
        e2.setId(1L);
        assertTrue(e1.equals(e2));
    }

    @Test
    void shouldNotBeEqualWhenDifferentId() {
        RevokedTokenEntity e1 = new RevokedTokenEntity();
        e1.setId(1L);
        RevokedTokenEntity e2 = new RevokedTokenEntity();
        e2.setId(2L);
        assertFalse(e1.equals(e2));
    }

    @Test
    void shouldNotBeEqualWhenNullId() {
        RevokedTokenEntity e1 = new RevokedTokenEntity();
        RevokedTokenEntity e2 = new RevokedTokenEntity();
        assertFalse(e1.equals(e2));
    }

    @Test
    void shouldNotBeEqualToDifferentType() {
        RevokedTokenEntity entity = new RevokedTokenEntity();
        entity.setId(1L);
        assertFalse(entity.equals("not an entity"));
    }

    @Test
    void shouldHaveHashCode() {
        RevokedTokenEntity entity = new RevokedTokenEntity();
        assertNotNull(entity.hashCode());
    }

    @Test
    void shouldHaveToString() {
        RevokedTokenEntity entity = new RevokedTokenEntity();
        entity.setId(1L);
        entity.setExpiresAt(123456789L);
        String str = entity.toString();
        assertTrue(str.contains("1"));
        assertTrue(str.contains("123456789"));
    }
}