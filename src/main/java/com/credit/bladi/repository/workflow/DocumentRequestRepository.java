package com.credit.bladi.repository.workflow;

import com.credit.bladi.datasource.workflow.DocumentRequestDatasource;
import com.credit.bladi.datasource.workflow.entity.DocumentRequestEntity;
import com.credit.bladi.datasource.workflow.mapper.DocumentRequestEntityMapper;
import com.credit.bladi.model.workflow.DocumentRequestModel;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Repository;

@Repository
public class DocumentRequestRepository implements IDocumentRequestRepository {
    private final DocumentRequestDatasource datasource;

    private final Clock clock;

    public DocumentRequestRepository(DocumentRequestDatasource datasource, Clock clock) {
        this.clock = clock;
        this.datasource = datasource;
    }

    @Override
    public DocumentRequestModel create(Long demandId, String documentType, String requestedByUsername) {
        DocumentRequestEntity entity = DocumentRequestEntityMapper.mapToNewEntity(demandId, documentType,
            requestedByUsername, clock.instant());
        datasource.save(entity);
        return toModel(entity);
    }

    @Override
    public Optional<DocumentRequestModel> findById(Long id) {
        return datasource.findById(id).map(this::toModel);
    }

    @Override
    public Optional<Long> findIdByPublicId(UUID publicId) {
        return datasource.findByPublicId(publicId).map(DocumentRequestEntity::getId);
    }

    @Override
    public List<DocumentRequestModel> findByDemandId(Long demandId) {
        return datasource.findByDemandIdOrderByCreatedAtDesc(demandId).stream()
            .map(this::toModel)
            .toList();
    }

    @Override
    public DocumentRequestModel fulfill(Long id, Long fulfilledDocumentId, Instant fulfilledAt) {
        int updated = datasource.fulfillIfPending(id, fulfilledDocumentId, fulfilledAt);
        if (updated == 0) {
            throw new OptimisticLockingFailureException("DocumentRequest " + id + " is no longer PENDING");
        }
        return findById(id).orElseThrow();
    }

    private DocumentRequestModel toModel(DocumentRequestEntity entity) {
        UUID demandPublicId = datasource.findDemandPublicId(entity.getDemandId()).orElse(null);
        UUID fulfilledDocumentPublicId = entity.getFulfilledDocumentId() == null ? null
            : datasource.findDocumentPublicId(entity.getFulfilledDocumentId()).orElse(null);
        return DocumentRequestEntityMapper.mapFromEntity(entity, demandPublicId, fulfilledDocumentPublicId);
    }
}
