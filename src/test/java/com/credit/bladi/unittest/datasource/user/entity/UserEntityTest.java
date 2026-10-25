package com.credit.bladi.unittest.datasource.user.entity;

import static com.credit.bladi.unittest.mock.data.MockUsers.USER_ALICE;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_JOHNDOE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.datasource.user.entity.UserEntity;
import com.credit.bladi.model.enums.UserRole;
import org.junit.jupiter.api.Test;

class UserEntityTest {
    @Test
    void shouldCreateWithDefaultConstructor() {
        UserEntity entity = new UserEntity();
        assertNotNull(entity);
        assertNull(entity.getId());
        assertNull(entity.getUsername());
        assertNull(entity.getPassword());
        assertNull(entity.getRole());
    }

    @Test
    void shouldSetAndGetId() {
        UserEntity entity = new UserEntity();
        entity.setId(42L);
        assertEquals(42L, entity.getId());
    }

    @Test
    void shouldSetAndGetUsername() {
        UserEntity entity = new UserEntity();
        entity.setUsername(USER_JOHNDOE);
        assertEquals(USER_JOHNDOE, entity.getUsername());
    }

    @Test
    void shouldSetAndGetPassword() {
        UserEntity entity = new UserEntity();
        entity.setPassword("$2a$encoded");
        assertEquals("$2a$encoded", entity.getPassword());
    }

    @Test
    void shouldSetAndGetRole() {
        UserEntity entity = new UserEntity();
        entity.setRole(UserRole.MANAGER);
        assertEquals(UserRole.MANAGER, entity.getRole());
    }

    @Test
    void shouldBeEqualToItself() {
        UserEntity entity = new UserEntity();
        entity.setId(1L);
        assertTrue(entity.equals(entity));
    }

    @Test
    void shouldBeEqualWhenSameId() {
        UserEntity e1 = new UserEntity();
        e1.setId(1L);
        UserEntity e2 = new UserEntity();
        e2.setId(1L);
        assertTrue(e1.equals(e2));
    }

    @Test
    void shouldNotBeEqualWhenDifferentId() {
        UserEntity e1 = new UserEntity();
        e1.setId(1L);
        UserEntity e2 = new UserEntity();
        e2.setId(2L);
        assertFalse(e1.equals(e2));
    }

    @Test
    void shouldNotBeEqualWhenBothIdsNull() {
        UserEntity e1 = new UserEntity();
        UserEntity e2 = new UserEntity();
        assertFalse(e1.equals(e2));
    }

    @Test
    void shouldNotBeEqualToDifferentType() {
        UserEntity entity = new UserEntity();
        entity.setId(1L);
        assertFalse(entity.equals("not an entity"));
    }

    @Test
    void shouldNotBeEqualToNull() {
        UserEntity entity = new UserEntity();
        entity.setId(1L);
        assertFalse(entity.equals(null));
    }

    @Test
    void shouldHaveHashCode() {
        UserEntity entity = new UserEntity();
        entity.setId(1L);
        assertNotEquals(0, entity.hashCode());
    }

    @Test
    void shouldHaveToStringWithIdAndUsername() {
        UserEntity entity = new UserEntity();
        entity.setId(1L);
        entity.setUsername(USER_ALICE);
        entity.setRole(UserRole.CLIENT);
        String str = entity.toString();
        assertTrue(str.contains("1"));
        assertTrue(str.contains(USER_ALICE));
    }
}
