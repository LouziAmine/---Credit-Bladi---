package com.credit.bladi.unittest.datasource.audit.entity;

import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_LOGIN;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_SUCCESS;
import static com.credit.bladi.unittest.mock.data.MockContacts.IP_10_0_0_1;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_JDOE;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_OTHER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.datasource.audit.entity.AuditLogEntity;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class AuditLogEntityTest {
    private static void setId(AuditLogEntity entity, Long id) throws Exception {
        Field field = AuditLogEntity.class.getDeclaredField("id");
        field.setAccessible(true);
        field.set(entity, id);
    }

    @Test
    void shouldCreateWithDefaultConstructor() {
        AuditLogEntity entity = new AuditLogEntity();
        assertNotNull(entity);
        assertNull(entity.getId());
        assertNull(entity.getUsername());
    }

    @Test
    void shouldSetAndGetAllFields() {
        AuditLogEntity entity = new AuditLogEntity();
        LocalDateTime now = LocalDateTime.now();
        entity.setUsername(USER_JDOE);
        entity.setAction(CODE_LOGIN);
        entity.setIpAddress(IP_10_0_0_1);
        entity.setDetails(CODE_SUCCESS);
        entity.setPerformedAt(now);

        assertEquals(USER_JDOE, entity.getUsername());
        assertEquals(CODE_LOGIN, entity.getAction());
        assertEquals(IP_10_0_0_1, entity.getIpAddress());
        assertEquals(CODE_SUCCESS, entity.getDetails());
        assertEquals(now, entity.getPerformedAt());
    }

    @Test
    void shouldBeEqualToItself() {
        AuditLogEntity entity = new AuditLogEntity();
        assertTrue(entity.equals(entity));
    }

    @Test
    void shouldBeEqualWhenSameId() throws Exception {
        AuditLogEntity e1 = new AuditLogEntity();
        setId(e1, 1L);
        AuditLogEntity e2 = new AuditLogEntity();
        setId(e2, 1L);
        assertTrue(e1.equals(e2));
    }

    @Test
    void shouldNotBeEqualWhenDifferentId() throws Exception {
        AuditLogEntity e1 = new AuditLogEntity();
        setId(e1, 1L);
        AuditLogEntity e2 = new AuditLogEntity();
        setId(e2, 2L);
        assertFalse(e1.equals(e2));
    }

    @Test
    void shouldNotBeEqualWhenNullId() {
        AuditLogEntity e1 = new AuditLogEntity();
        AuditLogEntity e2 = new AuditLogEntity();
        assertFalse(e1.equals(e2));
    }

    @Test
    void shouldNotBeEqualToDifferentType() {
        AuditLogEntity entity = new AuditLogEntity();
        assertFalse(entity.equals(USER_OTHER));
    }

    @Test
    void shouldHaveHashCode() {
        AuditLogEntity entity = new AuditLogEntity();
        assertNotNull(entity.hashCode());
    }

    @Test
    void shouldHaveToString() {
        AuditLogEntity entity = new AuditLogEntity();
        entity.setUsername(USER_JDOE);
        entity.setAction(CODE_LOGIN);
        String str = entity.toString();
        assertTrue(str.contains(USER_JDOE));
        assertTrue(str.contains(CODE_LOGIN));
    }
}
