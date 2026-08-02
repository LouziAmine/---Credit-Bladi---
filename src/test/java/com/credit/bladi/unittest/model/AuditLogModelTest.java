package com.credit.bladi.unittest.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.model.audit.AuditLogModel;
import com.credit.bladi.model.enums.AuditAction;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class AuditLogModelTest {

    @Test
    void shouldBuildWithAllFields() {
        LocalDateTime now = LocalDateTime.now();
        AuditLogModel model = AuditLogModel.getBuilder()
            .id(1L)
            .username("jdoe")
            .action(AuditAction.LOGIN)
            .ipAddress("10.0.0.1")
            .details("SUCCESS")
            .performedAt(now)
            .build();

        assertEquals(1L, model.getId());
        assertEquals("jdoe", model.getUsername());
        assertEquals(AuditAction.LOGIN, model.getAction());
        assertEquals("10.0.0.1", model.getIpAddress());
        assertEquals("SUCCESS", model.getDetails());
        assertEquals(now, model.getPerformedAt());
    }

    @Test
    void shouldBeEqualToItself() {
        AuditLogModel model = AuditLogModel.getBuilder().id(1L).build();
        assertTrue(model.equals(model));
    }

    @Test
    void shouldBeEqualWhenSameId() {
        AuditLogModel m1 = AuditLogModel.getBuilder().id(1L).username("jdoe").build();
        AuditLogModel m2 = AuditLogModel.getBuilder().id(1L).username("other").build();
        assertEquals(m1, m2);
    }

    @Test
    void shouldNotBeEqualWhenDifferentId() {
        AuditLogModel m1 = AuditLogModel.getBuilder().id(1L).build();
        AuditLogModel m2 = AuditLogModel.getBuilder().id(2L).build();
        assertFalse(m1.equals(m2));
    }

    @Test
    void shouldNotBeEqualWhenNullId() {
        AuditLogModel m1 = AuditLogModel.getBuilder().build();
        AuditLogModel m2 = AuditLogModel.getBuilder().build();
        assertFalse(m1.equals(m2));
    }

    @Test
    void shouldNotBeEqualToDifferentType() {
        AuditLogModel model = AuditLogModel.getBuilder().id(1L).build();
        assertFalse(model.equals("other"));
    }

    @Test
    void shouldHaveHashCode() {
        AuditLogModel model = AuditLogModel.getBuilder().id(1L).build();
        assertNotNull(model.hashCode());
    }

    @Test
    void shouldHaveToString() {
        AuditLogModel model = AuditLogModel.getBuilder().id(1L).username("jdoe").action(AuditAction.LOGIN).build();
        String str = model.toString();
        assertTrue(str.contains("jdoe"));
        assertTrue(str.contains("LOGIN"));
    }
}