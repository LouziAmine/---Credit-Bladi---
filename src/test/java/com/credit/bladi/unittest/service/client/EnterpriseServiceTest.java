package com.credit.bladi.unittest.service.client;

import static com.credit.bladi.unittest.mock.data.MockContacts.COMPANY_ATLAS_TEXTILE_SARL;
import static com.credit.bladi.unittest.mock.data.MockContacts.RIB_230780012345678901234503;
import static com.credit.bladi.unittest.mock.data.MockContacts.RIB_230_780_0123456789012345_03;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_TEXTILE;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_ADMIN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.exception.EnterpriseException;
import com.credit.bladi.exception.EnterpriseException.EnterpriseExceptionType;
import com.credit.bladi.model.audit.AuditTarget;
import com.credit.bladi.model.client.EnterpriseModel;
import com.credit.bladi.model.enums.AuditAction;
import com.credit.bladi.model.vo.Rib;
import com.credit.bladi.repository.client.IEnterpriseRepository;
import com.credit.bladi.service.audit.IAuditService;
import com.credit.bladi.service.client.EnterpriseService;
import com.credit.bladi.service.security.ICurrentActorProvider;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EnterpriseServiceTest {
    @Mock
    private IEnterpriseRepository repository;

    @Mock
    private IAuditService auditService;

    @Mock
    private ICurrentActorProvider currentActorProvider;

    private EnterpriseService enterpriseService;

    @BeforeEach
    void setUp() {
        enterpriseService = new EnterpriseService(repository, auditService, currentActorProvider);
    }

    @Test
    void shouldCreateEnterpriseAndAuditLogIt() {
        EnterpriseModel created = new EnterpriseModel(1L, UUID.randomUUID(), COMPANY_ATLAS_TEXTILE_SARL, LABEL_TEXTILE,
            null,
            null, Instant.now());
        when(repository.create(COMPANY_ATLAS_TEXTILE_SARL, LABEL_TEXTILE, null)).thenReturn(created);
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_ADMIN);

        EnterpriseModel result = enterpriseService.createEnterprise(COMPANY_ATLAS_TEXTILE_SARL, LABEL_TEXTILE, null);

        assertEquals(created, result);
        verify(auditService).log(eq(USER_ADMIN), eq(AuditAction.ENTERPRISE_CREATE), any(AuditTarget.class), any());
    }

    @Test
    void shouldReturnPaginatedEnterprises() {
        CreditPage<EnterpriseModel> page = new CreditPage<>();
        when(repository.findAll(1, 20)).thenReturn(page);

        CreditPage<EnterpriseModel> result = enterpriseService.getAllEnterprises(1, 20);

        assertEquals(page, result);
    }

    @Test
    void shouldReturnEnterpriseById() {
        EnterpriseModel enterprise = new EnterpriseModel(1L, UUID.randomUUID(), COMPANY_ATLAS_TEXTILE_SARL,
            LABEL_TEXTILE, null,
            null, Instant.now());
        when(repository.findById(1L)).thenReturn(Optional.of(enterprise));

        EnterpriseModel result = enterpriseService.getEnterpriseById(1L);

        assertEquals(enterprise, result);
    }

    @Test
    void shouldReturnEnterprisesByIds() {
        EnterpriseModel enterprise = new EnterpriseModel(1L, UUID.randomUUID(), COMPANY_ATLAS_TEXTILE_SARL,
            LABEL_TEXTILE, null,
            null, Instant.now());
        when(repository.findAllByIds(List.of(1L))).thenReturn(List.of(enterprise));

        List<EnterpriseModel> result = enterpriseService.getEnterprisesByIds(List.of(1L));

        assertEquals(List.of(enterprise), result);
    }

    @Test
    void shouldReturnRibWhenEnterpriseIdProvided() {
        EnterpriseModel enterprise = new EnterpriseModel(1L, UUID.randomUUID(), COMPANY_ATLAS_TEXTILE_SARL,
            LABEL_TEXTILE, null,
            Rib.of(RIB_230_780_0123456789012345_03), Instant.now());
        when(repository.findById(1L)).thenReturn(Optional.of(enterprise));

        String result = enterpriseService.getRibOrNull(1L);

        assertEquals(RIB_230780012345678901234503, result);
    }

    @Test
    void shouldReturnNullRibWhenEnterpriseIdIsNull() {
        String result = enterpriseService.getRibOrNull(null);

        assertNull(result);
    }

    @Test
    void shouldThrowWhenEnterpriseNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        EnterpriseException ex = assertThrows(EnterpriseException.class,
            () -> enterpriseService.getEnterpriseById(99L));

        assertEquals(EnterpriseExceptionType.ENTERPRISE_NOT_FOUND, ex.getType());
    }

    @Test
    void shouldReturnMatchWhenRepositoryFindsOne() {
        EnterpriseModel enterprise = new EnterpriseModel(1L, UUID.randomUUID(), COMPANY_ATLAS_TEXTILE_SARL,
            LABEL_TEXTILE, null,
            null, Instant.now());
        when(repository.findMatchByName("afd technology")).thenReturn(Optional.of(enterprise));

        Optional<EnterpriseModel> result = enterpriseService.matchEnterpriseByName("afd technology");

        assertEquals(Optional.of(enterprise), result);
    }

    @Test
    void shouldReturnEmptyWhenNoMatch() {
        when(repository.findMatchByName("unknown co")).thenReturn(Optional.empty());

        Optional<EnterpriseModel> result = enterpriseService.matchEnterpriseByName("unknown co");

        assertEquals(Optional.empty(), result);
    }
}
