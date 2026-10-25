package com.credit.bladi.unittest.repository.enterprise;

import static com.credit.bladi.unittest.mock.data.MockUsers.USER_JDUPONT;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_MANAGER1;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.datasource.enterprise.EnterpriseUserLinkDatasource;
import com.credit.bladi.datasource.enterprise.entity.EnterpriseUserLinkEntity;
import com.credit.bladi.model.enterprise.EnterpriseUserLinkModel;
import com.credit.bladi.model.enums.EnterpriseLinkOrigin;
import com.credit.bladi.model.enums.EnterpriseLinkRole;
import com.credit.bladi.model.enums.EnterpriseLinkStatus;
import com.credit.bladi.repository.enterprise.EnterpriseUserLinkRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

@ExtendWith(MockitoExtension.class)
class EnterpriseUserLinkRepositoryTest {
    @Mock
    private EnterpriseUserLinkDatasource datasource;

    private EnterpriseUserLinkRepository repository;

    @BeforeEach
    void setUp() {
        repository = new EnterpriseUserLinkRepository(datasource, Clock.systemUTC());
    }

    private static EnterpriseUserLinkEntity entity(long id, EnterpriseLinkStatus status) {
        EnterpriseUserLinkEntity entity = new EnterpriseUserLinkEntity();
        entity.setId(id);
        entity.setEnterpriseId(1L);
        entity.setUserId(5L);
        entity.setRole(EnterpriseLinkRole.RESPONSABLE);
        entity.setStatus(status);
        entity.setOrigin(EnterpriseLinkOrigin.CLIENT_CLAIM);
        entity.setRequestedByUsername(USER_JDUPONT);
        entity.setCreatedAt(Instant.now());
        return entity;
    }

    @Test
    void shouldFindActiveLinkAmongPendingOrApprovedStatuses() {
        EnterpriseUserLinkEntity existing = entity(1L, EnterpriseLinkStatus.PENDING);
        when(datasource.findByEnterpriseIdAndUserIdAndStatusIn(eq(1L), eq(5L), any()))
            .thenReturn(Optional.of(existing));

        Optional<EnterpriseUserLinkModel> result = repository.findActiveLink(1L, 5L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().id());
    }

    @Test
    void shouldReturnEmptyWhenNoActiveLink() {
        when(datasource.findByEnterpriseIdAndUserIdAndStatusIn(eq(1L), eq(5L), any())).thenReturn(Optional.empty());

        Optional<EnterpriseUserLinkModel> result = repository.findActiveLink(1L, 5L);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldMapPaginatedResultsByEnterprise() {
        Page<EnterpriseUserLinkEntity> page = new PageImpl<>(List.of(entity(1L, EnterpriseLinkStatus.APPROVED)));
        when(datasource.findByEnterpriseId(eq(1L), any())).thenReturn(page);

        CreditPage<EnterpriseUserLinkModel> result = repository.findByEnterpriseId(1L, 1, 20);

        assertEquals(1, result.getItems().size());
        assertEquals(1, result.getCurrentPage());
        assertEquals(20, result.getSize());
    }

    @Test
    void shouldMapPaginatedResultsByStatus() {
        Page<EnterpriseUserLinkEntity> page = new PageImpl<>(List.of(entity(2L, EnterpriseLinkStatus.PENDING)));
        when(datasource.findByStatus(eq(EnterpriseLinkStatus.PENDING), any())).thenReturn(page);

        CreditPage<EnterpriseUserLinkModel> result = repository.findByStatus(EnterpriseLinkStatus.PENDING, 1, 20);

        assertEquals(1, result.getItems().size());
        assertEquals(EnterpriseLinkStatus.PENDING, result.getItems().get(0).status());
    }

    @Test
    void shouldUpdateStatusAndDecisionFields() {
        Instant decidedAt = Instant.now();
        when(datasource.findById(1L)).thenReturn(Optional.of(entity(1L, EnterpriseLinkStatus.APPROVED)));

        EnterpriseUserLinkModel result = repository.updateStatus(1L, EnterpriseLinkStatus.APPROVED, USER_MANAGER1,
            decidedAt);

        verify(datasource).updateStatus(1L, EnterpriseLinkStatus.APPROVED, USER_MANAGER1, decidedAt);
        assertEquals(EnterpriseLinkStatus.APPROVED, result.status());
    }

    @Test
    void shouldDecideOnlyWhileStillPending() {
        Instant decidedAt = Instant.now();
        when(datasource.decideIfPending(1L, EnterpriseLinkStatus.REJECTED, USER_MANAGER1, decidedAt)).thenReturn(1);
        when(datasource.findById(1L)).thenReturn(Optional.of(entity(1L, EnterpriseLinkStatus.REJECTED)));

        EnterpriseUserLinkModel result = repository.decideIfPending(1L, EnterpriseLinkStatus.REJECTED, USER_MANAGER1,
            decidedAt);

        assertEquals(EnterpriseLinkStatus.REJECTED, result.status());
    }

    @Test
    void shouldRefuseToDecideALinkAnotherManagerAlreadyDecided() {
        Instant decidedAt = Instant.now();
        when(datasource.decideIfPending(1L, EnterpriseLinkStatus.APPROVED, USER_MANAGER1, decidedAt)).thenReturn(0);

        assertThrows(OptimisticLockingFailureException.class,
            () -> repository.decideIfPending(1L, EnterpriseLinkStatus.APPROVED, USER_MANAGER1, decidedAt));
    }

    @Test
    void shouldFindApprovedLinksByUserId() {
        when(datasource.findByUserIdAndStatus(5L, EnterpriseLinkStatus.APPROVED))
            .thenReturn(List.of(entity(1L, EnterpriseLinkStatus.APPROVED)));

        List<EnterpriseUserLinkModel> result = repository.findApprovedByUserId(5L);

        assertEquals(1, result.size());
        assertEquals(EnterpriseLinkStatus.APPROVED, result.get(0).status());
    }

    @Test
    void shouldFindApprovedLinksByEnterpriseId() {
        when(datasource.findByEnterpriseIdAndStatus(1L, EnterpriseLinkStatus.APPROVED))
            .thenReturn(List.of(entity(1L, EnterpriseLinkStatus.APPROVED)));

        List<EnterpriseUserLinkModel> result = repository.findApprovedByEnterpriseId(1L);

        assertEquals(1, result.size());
        assertEquals(EnterpriseLinkStatus.APPROVED, result.get(0).status());
    }
}
