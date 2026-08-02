package com.credit.bladi.unittest.datasource.auth.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.datasource.auth.entity.RefreshTokenEntity;
import org.junit.jupiter.api.Test;

class RefreshTokenEntityTest {

    @Test
    void shouldCreateWithDefaultConstructor() {
        RefreshTokenEntity entity = new RefreshTokenEntity();
        assertNotNull(entity);
        assertNull(entity.getId());
        assertNull(entity.getTokenHash());
        assertNull(entity.getUserId());
        assertFalse(entity.isRevoked());
    }

    @Test
    void shouldCreateWithAllArgsConstructor() {
        RefreshTokenEntity entity = new RefreshTokenEntity("hash123", 1L, 9999999L);
        assertEquals("hash123", entity.getTokenHash());
        assertEquals(1L, entity.getUserId());
        assertEquals(9999999L, entity.getExpiresAt());
        assertFalse(entity.isRevoked());
    }

    @Test
    void shouldSetAndGetId() {
        RefreshTokenEntity entity = new RefreshTokenEntity();
        entity.setId(42L);
        assertEquals(42L, entity.getId());
    }

    @Test
    void shouldSetAndGetToken() {
        RefreshTokenEntity entity = new RefreshTokenEntity();
        entity.setTokenHash("myhash");
        assertEquals("myhash", entity.getTokenHash());
    }

    @Test
    void shouldSetAndGetUserId() {
        RefreshTokenEntity entity = new RefreshTokenEntity();
        entity.setUserId(7L);
        assertEquals(7L, entity.getUserId());
    }

    @Test
    void shouldSetAndGetExpiresAt() {
        RefreshTokenEntity entity = new RefreshTokenEntity();
        entity.setExpiresAt(123456789L);
        assertEquals(123456789L, entity.getExpiresAt());
    }

    @Test
    void shouldSetAndGetRevoked() {
        RefreshTokenEntity entity = new RefreshTokenEntity();
        entity.setRevoked(true);
        assertTrue(entity.isRevoked());
    }

    @Test
    void shouldBeEqualToItself() {
        RefreshTokenEntity entity = new RefreshTokenEntity();
        entity.setId(1L);
        assertTrue(entity.equals(entity));
    }

    @Test
    void shouldBeEqualWhenSameId() {
        RefreshTokenEntity e1 = new RefreshTokenEntity();
        e1.setId(1L);
        RefreshTokenEntity e2 = new RefreshTokenEntity();
        e2.setId(1L);
        assertTrue(e1.equals(e2));
    }

    @Test
    void shouldNotBeEqualWhenDifferentId() {
        RefreshTokenEntity e1 = new RefreshTokenEntity();
        e1.setId(1L);
        RefreshTokenEntity e2 = new RefreshTokenEntity();
        e2.setId(2L);
        assertFalse(e1.equals(e2));
    }

    @Test
    void shouldNotBeEqualWhenNullId() {
        RefreshTokenEntity e1 = new RefreshTokenEntity();
        RefreshTokenEntity e2 = new RefreshTokenEntity();
        assertFalse(e1.equals(e2));
    }

    @Test
    void shouldNotBeEqualToDifferentType() {
        RefreshTokenEntity entity = new RefreshTokenEntity();
        entity.setId(1L);
        assertFalse(entity.equals("not an entity"));
    }

    @Test
    void shouldHaveHashCode() {
        RefreshTokenEntity entity = new RefreshTokenEntity();
        assertNotNull(entity.hashCode());
    }

    @Test
    void shouldHaveToString() {
        RefreshTokenEntity entity = new RefreshTokenEntity();
        entity.setId(1L);
        entity.setUserId(7L);
        entity.setExpiresAt(123456789L);
        entity.setRevoked(true);
        String str = entity.toString();
        assertTrue(str.contains("1"));
        assertTrue(str.contains("7"));
        assertTrue(str.contains("true"));
    }
}