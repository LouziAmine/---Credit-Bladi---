package com.credit.bladi.unittest.datasource.audit.mapper;

import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_LOGIN;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_SUCCESS;
import static com.credit.bladi.unittest.mock.data.MockContacts.IP_10_0_0_1;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_JDOE;
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
            .username(USER_JDOE)
            .action(AuditAction.LOGIN)
            .ipAddress(IP_10_0_0_1)
            .details(CODE_SUCCESS)
            .performedAt(LocalDateTime.now())
            .build();
    }

    @Test
    void shouldMapModelToEntity() {
        AuditLogEntity entity = AuditLogEntityMapper.mapToEntity(model());

        assertEquals(USER_JDOE, entity.getUsername());
        assertEquals(CODE_LOGIN, entity.getAction());
        assertEquals(IP_10_0_0_1, entity.getIpAddress());
        assertEquals(CODE_SUCCESS, entity.getDetails());
    }

    @Test
    void shouldMapEntityToModel() {
        AuditLogEntity entity = new AuditLogEntity();
        entity.setUsername(USER_JDOE);
        entity.setAction(CODE_LOGIN);
        entity.setIpAddress(IP_10_0_0_1);
        entity.setDetails(CODE_SUCCESS);
        LocalDateTime now = LocalDateTime.now();
        entity.setPerformedAt(now);

        AuditLogModel model = AuditLogEntityMapper.mapFromEntity(entity);

        assertEquals(USER_JDOE, model.getUsername());
        assertEquals(AuditAction.LOGIN, model.getAction());
        assertEquals(IP_10_0_0_1, model.getIpAddress());
        assertEquals(CODE_SUCCESS, model.getDetails());
        assertEquals(now, model.getPerformedAt());
    }

    @Test
    void shouldMapEntitiesToPage() {
        AuditLogEntity entity = new AuditLogEntity();
        entity.setUsername(USER_JDOE);
        entity.setAction(CODE_LOGIN);

        CreditPage<AuditLogModel> page = AuditLogEntityMapper.mapToPage(List.of(entity), 1L, 1, 20);

        assertEquals(1, page.getItems().size());
        assertEquals(USER_JDOE, page.getItems().get(0).getUsername());
        assertEquals(1, page.getCurrentPage());
        assertEquals(1, page.getLastPage());
        assertEquals(20, page.getSize());
        assertEquals(1, page.getTotal());
    }

    @Test
    void shouldMapToPageWithZeroSizeYieldsZeroLastPage() {
        AuditLogEntity entity = new AuditLogEntity();
        entity.setUsername(USER_JDOE);
        entity.setAction(CODE_LOGIN);

        CreditPage<AuditLogModel> page = AuditLogEntityMapper.mapToPage(List.of(entity), 1L, 1, 0);

        assertEquals(0, page.getLastPage());
    }
}
