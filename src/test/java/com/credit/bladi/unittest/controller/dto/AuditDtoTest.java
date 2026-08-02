package com.credit.bladi.unittest.controller.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.controller.dto.audit.response.AuditLogResponseDTO;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class AuditDtoTest {

    @Test
    void shouldSetAndGetResponseFields() {
        LocalDateTime now = LocalDateTime.now();
        AuditLogResponseDTO dto = new AuditLogResponseDTO();
        dto.setId(1L);
        dto.setUsername("jdoe");
        dto.setAction("LOGIN");
        dto.setIpAddress("10.0.0.1");
        dto.setDetails("SUCCESS");
        dto.setPerformedAt(now);

        assertEquals(1L, dto.getId());
        assertEquals("jdoe", dto.getUsername());
        assertEquals("LOGIN", dto.getAction());
        assertEquals("10.0.0.1", dto.getIpAddress());
        assertEquals("SUCCESS", dto.getDetails());
        assertEquals(now, dto.getPerformedAt());
    }

    @Test
    void shouldResponseEqualsAndHashCode() {
        AuditLogResponseDTO d1 = new AuditLogResponseDTO();
        d1.setId(1L);

        AuditLogResponseDTO d2 = new AuditLogResponseDTO();
        d2.setId(1L);

        assertEquals(d1, d2);
        assertEquals(d1.hashCode(), d2.hashCode());
    }

    @Test
    void shouldResponseNotEqualWhenDifferentFields() {
        AuditLogResponseDTO d1 = new AuditLogResponseDTO();
        d1.setId(1L);

        AuditLogResponseDTO d2 = new AuditLogResponseDTO();
        d2.setId(2L);

        assertFalse(d1.equals(d2));
    }

    @Test
    void shouldResponseNotEqualToDifferentType() {
        AuditLogResponseDTO dto = new AuditLogResponseDTO();
        assertFalse(dto.equals("other"));
    }

    @Test
    void shouldResponseToString() {
        AuditLogResponseDTO dto = new AuditLogResponseDTO();
        dto.setId(1L);
        dto.setUsername("jdoe");
        dto.setAction("LOGIN");
        String str = dto.toString();
        assertTrue(str.contains("jdoe"));
        assertTrue(str.contains("LOGIN"));
    }
}
