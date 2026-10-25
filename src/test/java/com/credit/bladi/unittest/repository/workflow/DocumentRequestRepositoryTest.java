package com.credit.bladi.unittest.repository.workflow;

import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_PIECE_D_IDENTITE;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_MANAGER1;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.credit.bladi.datasource.workflow.DocumentRequestDatasource;
import com.credit.bladi.datasource.workflow.entity.DocumentRequestEntity;
import com.credit.bladi.model.enums.DocumentRequestStatus;
import com.credit.bladi.model.workflow.DocumentRequestModel;
import com.credit.bladi.repository.workflow.DocumentRequestRepository;
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
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class DocumentRequestRepositoryTest {
    @Mock
    private DocumentRequestDatasource datasource;

    private DocumentRequestRepository repository;

    @BeforeEach
    void setUp() {
        repository = new DocumentRequestRepository(datasource, Clock.systemUTC());
    }

    private static DocumentRequestEntity entity(long id, DocumentRequestStatus status) {
        DocumentRequestEntity entity = new DocumentRequestEntity();
        entity.setId(id);
        entity.setDemandId(1L);
        entity.setDocumentType(LABEL_PIECE_D_IDENTITE);
        entity.setStatus(status);
        entity.setRequestedByUsername(USER_MANAGER1);
        entity.setCreatedAt(Instant.now());
        return entity;
    }

    @Test
    void shouldCreateAPendingRequest() {
        when(datasource.save(any(DocumentRequestEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DocumentRequestModel result = repository.create(1L, LABEL_PIECE_D_IDENTITE, USER_MANAGER1);

        assertEquals(1L, result.demandId());
        assertEquals(LABEL_PIECE_D_IDENTITE, result.documentType());
        assertEquals(DocumentRequestStatus.PENDING, result.status());
        assertEquals(USER_MANAGER1, result.requestedByUsername());
    }

    @Test
    void shouldFindById() {
        when(datasource.findById(1L)).thenReturn(Optional.of(entity(1L, DocumentRequestStatus.PENDING)));

        Optional<DocumentRequestModel> result = repository.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().id());
    }

    @Test
    void shouldFindByDemandId() {
        when(datasource.findByDemandIdOrderByCreatedAtDesc(1L))
            .thenReturn(List.of(entity(1L, DocumentRequestStatus.PENDING),
                entity(2L, DocumentRequestStatus.FULFILLED)));

        List<DocumentRequestModel> result = repository.findByDemandId(1L);

        assertEquals(2, result.size());
    }

    @Test
    void shouldFulfillARequestOnlyWhileItIsStillPending() {
        Instant fulfilledAt = Instant.now();
        DocumentRequestEntity fulfilled = entity(1L, DocumentRequestStatus.FULFILLED);
        ReflectionTestUtils.setField(fulfilled, "fulfilledDocumentId", 7L);
        ReflectionTestUtils.setField(fulfilled, "fulfilledAt", fulfilledAt);
        when(datasource.fulfillIfPending(1L, 7L, fulfilledAt)).thenReturn(1);
        when(datasource.findById(1L)).thenReturn(Optional.of(fulfilled));

        DocumentRequestModel result = repository.fulfill(1L, 7L, fulfilledAt);

        assertEquals(DocumentRequestStatus.FULFILLED, result.status());
        assertEquals(7L, result.fulfilledDocumentId());
        assertEquals(fulfilledAt, result.fulfilledAt());
    }

    @Test
    void shouldRefuseToFulfillARequestThatIsNoLongerPending() {
        Instant fulfilledAt = Instant.now();
        when(datasource.fulfillIfPending(1L, 7L, fulfilledAt)).thenReturn(0);

        assertThrows(OptimisticLockingFailureException.class, () -> repository.fulfill(1L, 7L, fulfilledAt));
    }
}
