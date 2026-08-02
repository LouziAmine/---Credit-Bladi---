package com.credit.bladi.unittest.controller.audit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.controller.audit.AuditLogController;
import com.credit.bladi.controller.dto.audit.response.AuditLogResponseDTO;
import com.credit.bladi.model.audit.AuditLogModel;
import com.credit.bladi.model.enums.AuditAction;
import com.credit.bladi.service.audit.IAuditService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class AuditLogControllerTest {

    @Mock
    private IAuditService auditService;

    private AuditLogController controller;

    @BeforeEach
    void init() {
        controller = new AuditLogController(auditService);
    }

    @Test
    void shouldReturn200WithPageOfAuditLogEntries() {
        AuditLogModel model = AuditLogModel.getBuilder()
            .id(1L)
            .username("jdoe")
            .action(AuditAction.LOGIN)
            .performedAt(LocalDateTime.now())
            .build();
        CreditPage<AuditLogModel> page = new CreditPage<>();
        page.setItems(List.of(model));
        page.setCurrentPage(1);
        page.setLastPage(1);
        page.setSize(20);
        page.setTotal(1);
        when(auditService.getAuditLog(1, 20)).thenReturn(page);

        ResponseEntity<CreditPage<AuditLogResponseDTO>> result = controller.getAuditLog(1, 20);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().getItems().size());
        assertEquals("jdoe", result.getBody().getItems().get(0).getUsername());
    }

    @Test
    void shouldDelegateGetAuditLogToService() {
        when(auditService.getAuditLog(anyInt(), anyInt())).thenReturn(new CreditPage<>());

        controller.getAuditLog(2, 10);

        verify(auditService).getAuditLog(eq(2), eq(10));
    }
}
