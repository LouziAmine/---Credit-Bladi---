package com.credit.bladi.unittest.controller.audit.mapper;

import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_LOGIN;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_SUCCESS;
import static com.credit.bladi.unittest.mock.data.MockContacts.IP_10_0_0_1;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_JDOE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.controller.audit.mapper.AuditLogMapper;
import com.credit.bladi.controller.dto.audit.response.AuditLogResponseDTO;
import com.credit.bladi.model.audit.AuditLogModel;
import com.credit.bladi.model.enums.AuditAction;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class AuditLogMapperTest {
    @Test
    void shouldMapCreditPageOfModelsToCreditPageOfDtos() {
        AuditLogModel model = AuditLogModel.getBuilder()
            .id(1L)
            .username(USER_JDOE)
            .action(AuditAction.LOGIN)
            .ipAddress(IP_10_0_0_1)
            .details(CODE_SUCCESS)
            .performedAt(LocalDateTime.now())
            .build();
        CreditPage<AuditLogModel> source = new CreditPage<>();
        source.setItems(List.of(model));
        source.setCurrentPage(1);
        source.setLastPage(1);
        source.setSize(20);
        source.setTotal(1);

        CreditPage<AuditLogResponseDTO> result = AuditLogMapper.toCreditPage(source);

        assertEquals(1, result.getItems().size());
        assertEquals(USER_JDOE, result.getItems().get(0).username());
        assertEquals(CODE_LOGIN, result.getItems().get(0).action());
    }

    @Test
    void shouldMapModelWithNullActionToDtoWithNullAction() {
        AuditLogModel model = AuditLogModel.getBuilder().id(1L).username(USER_JDOE).build();
        CreditPage<AuditLogModel> source = new CreditPage<>();
        source.setItems(List.of(model));

        CreditPage<AuditLogResponseDTO> result = AuditLogMapper.toCreditPage(source);

        assertNull(result.getItems().get(0).action());
    }

    @Test
    void shouldMapNullItemToNullDto() {
        CreditPage<AuditLogModel> source = new CreditPage<>();
        source.setItems(Arrays.asList((AuditLogModel) null));

        CreditPage<AuditLogResponseDTO> result = AuditLogMapper.toCreditPage(source);

        assertNull(result.getItems().get(0));
    }

    @Test
    void shouldReturnEmptyPageWhenSourceIsNull() {
        CreditPage<AuditLogResponseDTO> result = AuditLogMapper.toCreditPage(null);

        assertEquals(0, result.getItems().size());
    }
}
