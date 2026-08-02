package com.credit.bladi.unittest.datasource.audit.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.datasource.audit.entity.AuditLogEntity;
import com.credit.bladi.datasource.audit.mapper.AuditLogEntityMapper;
import com.credit.bladi.model.audit.AuditLogModel;
import com.credit.bladi.model.enums.AuditAction;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class AuditLogEntityMapperTest {

    private static AuditLogModel model() {
        return AuditLogModel.getBuilder()
            .username("jdoe")
            .action(AuditAction.LOGIN)
            .ipAddress("10.0.0.1")
            .details("SUCCESS")
            .performedAt(LocalDateTime.now())
            .build();
    }

    @Test
    void shouldMapModelToEntity() {
        AuditLogEntity entity = AuditLogEntityMapper.mapToEntity(model());

        assertEquals("jdoe", entity.getUsername());
        assertEquals("LOGIN", entity.getAction());
        assertEquals("10.0.0.1", entity.getIpAddress());
        assertEquals("SUCCESS", entity.getDetails());
    }

    @Test
    void shouldMapEntityToModel() {
        AuditLogEntity entity = new AuditLogEntity();
        entity.setUsername("jdoe");
        entity.setAction("LOGIN");
        entity.setIpAddress("10.0.0.1");
        entity.setDetails("SUCCESS");
        LocalDateTime now = LocalDateTime.now();
        entity.setPerformedAt(now);

        AuditLogModel model = AuditLogEntityMapper.mapFromEntity(entity);

        assertEquals("jdoe", model.getUsername());
        assertEquals(AuditAction.LOGIN, model.getAction());
        assertEquals("10.0.0.1", model.getIpAddress());
        assertEquals("SUCCESS", model.getDetails());
        assertEquals(now, model.getPerformedAt());
    }

    @Test
    void shouldMapEntitiesToPage() {
        AuditLogEntity entity = new AuditLogEntity();
        entity.setUsername("jdoe");
        entity.setAction("LOGIN");

        CreditPage<AuditLogModel> page = AuditLogEntityMapper.mapToPage(List.of(entity), 1L, 1, 20);

        assertEquals(1, page.getItems().size());
        assertEquals("jdoe", page.getItems().get(0).getUsername());
        assertEquals(1, page.getCurrentPage());
        assertEquals(1, page.getLastPage());
        assertEquals(20, page.getSize());
        assertEquals(1, page.getTotal());
    }

    @Test
    void shouldMapToPageWithZeroSizeYieldsZeroLastPage() {
        AuditLogEntity entity = new AuditLogEntity();
        entity.setUsername("jdoe");
        entity.setAction("LOGIN");

        CreditPage<AuditLogModel> page = AuditLogEntityMapper.mapToPage(List.of(entity), 1L, 1, 0);

        assertEquals(0, page.getLastPage());
    }
}
