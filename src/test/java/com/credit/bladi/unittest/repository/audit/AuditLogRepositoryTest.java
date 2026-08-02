package com.credit.bladi.unittest.repository.audit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.datasource.audit.AuditLogDatasource;
import com.credit.bladi.datasource.audit.entity.AuditLogEntity;
import com.credit.bladi.model.audit.AuditLogModel;
import com.credit.bladi.model.enums.AuditAction;
import com.credit.bladi.repository.audit.AuditLogRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class AuditLogRepositoryTest {

    @Mock
    private AuditLogDatasource datasource;

    private AuditLogRepository repository;

    @BeforeEach
    void init() {
        repository = new AuditLogRepository(datasource);
    }

    @Test
    void shouldSaveMappedEntity() {
        AuditLogModel model = AuditLogModel.getBuilder()
            .username("jdoe")
            .action(AuditAction.LOGIN)
            .performedAt(LocalDateTime.now())
            .build();

        repository.save(model);

        verify(datasource).save(any(AuditLogEntity.class));
    }

    @Test
    void shouldReturnPageOfAuditLogEntries() {
        AuditLogEntity entity = new AuditLogEntity();
        entity.setUsername("jdoe");
        entity.setAction("LOGIN");
        entity.setPerformedAt(LocalDateTime.now());
        Page<AuditLogEntity> page = new PageImpl<>(List.of(entity));
        when(datasource.findAll(any(PageRequest.class))).thenReturn(page);

        CreditPage<AuditLogModel> result = repository.findAll(1, 20);

        assertEquals(1, result.getItems().size());
        assertEquals("jdoe", result.getItems().get(0).getUsername());
    }
}
