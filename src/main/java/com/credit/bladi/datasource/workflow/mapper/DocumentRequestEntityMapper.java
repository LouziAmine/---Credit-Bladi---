package com.credit.bladi.datasource.workflow.mapper;

import com.credit.bladi.datasource.workflow.entity.DocumentRequestEntity;
import com.credit.bladi.model.enums.DocumentRequestStatus;
import com.credit.bladi.model.workflow.DocumentRequestModel;
import java.time.Instant;
import java.util.UUID;

public final class DocumentRequestEntityMapper {
    private DocumentRequestEntityMapper() {
    }

    public static DocumentRequestModel mapFromEntity(DocumentRequestEntity entity, UUID demandPublicId,
            UUID fulfilledDocumentPublicId) {
        return new DocumentRequestModel(
            entity.getId(),
            entity.getPublicId(),
            entity.getDemandId(),
            demandPublicId,
            entity.getDocumentType(),
            entity.getStatus(),
            entity.getRequestedByUsername(),
            entity.getCreatedAt(),
            entity.getFulfilledDocumentId(),
            fulfilledDocumentPublicId,
            entity.getFulfilledAt());
    }

    public static DocumentRequestEntity mapToNewEntity(Long demandId, String documentType,
            String requestedByUsername, Instant createdAt) {
        DocumentRequestEntity entity = new DocumentRequestEntity();
        entity.setDemandId(demandId);
        entity.setDocumentType(documentType);
        entity.setStatus(DocumentRequestStatus.PENDING);
        entity.setRequestedByUsername(requestedByUsername);
        entity.setCreatedAt(createdAt);
        return entity;
    }
}
