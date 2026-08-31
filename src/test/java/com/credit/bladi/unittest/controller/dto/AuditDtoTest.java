package com.credit.bladi.unittest.controller.dto;

import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_LOGIN;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_SUCCESS;
import static com.credit.bladi.unittest.mock.data.MockContacts.IP_10_0_0_1;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_JDOE;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_OTHER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.controller.dto.audit.response.AuditLogResponseDTO;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class AuditDtoTest {
    private static final UUID ID_1 = UUID.randomUUID();

    private static final UUID ID_2 = UUID.randomUUID();

    @Test
    void shouldSetAndGetResponseFields() {
        LocalDateTime now = LocalDateTime.now();
        AuditLogResponseDTO dto = new AuditLogResponseDTO(ID_1, USER_JDOE, CODE_LOGIN, IP_10_0_0_1, CODE_SUCCESS, now,
            null,
            null);

        assertEquals(ID_1, dto.id());
        assertEquals(USER_JDOE, dto.username());
        assertEquals(CODE_LOGIN, dto.action());
        assertEquals(IP_10_0_0_1, dto.ipAddress());
        assertEquals(CODE_SUCCESS, dto.details());
        assertEquals(now, dto.performedAt());
    }

    @Test
    void shouldResponseEqualsAndHashCode() {
        AuditLogResponseDTO d1 = new AuditLogResponseDTO(ID_1, null, null, null, null, null, null, null);
        AuditLogResponseDTO d2 = new AuditLogResponseDTO(ID_1, null, null, null, null, null, null, null);

        assertEquals(d1, d2);
        assertEquals(d1.hashCode(), d2.hashCode());
    }

    @Test
    void shouldResponseNotEqualWhenDifferentFields() {
        AuditLogResponseDTO d1 = new AuditLogResponseDTO(ID_1, null, null, null, null, null, null, null);
        AuditLogResponseDTO d2 = new AuditLogResponseDTO(ID_2, null, null, null, null, null, null, null);

        assertFalse(d1.equals(d2));
    }

    @Test
    void shouldResponseNotEqualToDifferentType() {
        AuditLogResponseDTO dto = new AuditLogResponseDTO(null, null, null, null, null, null, null, null);
        assertFalse(dto.equals(USER_OTHER));
    }

    @Test
    void shouldResponseToString() {
        AuditLogResponseDTO dto = new AuditLogResponseDTO(ID_1, USER_JDOE, CODE_LOGIN, null, null, null, null, null);
        String str = dto.toString();
        assertTrue(str.contains(USER_JDOE));
        assertTrue(str.contains(CODE_LOGIN));
    }
}
