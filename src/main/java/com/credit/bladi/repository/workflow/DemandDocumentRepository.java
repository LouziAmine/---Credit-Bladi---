package com.credit.bladi.repository.workflow;

import static com.credit.bladi.exception.WorkflowException.WorkflowExceptionType.DOCUMENT_NOT_FOUND;

import com.credit.bladi.datasource.workflow.DemandDocumentDatasource;
import com.credit.bladi.datasource.workflow.entity.DemandDocumentEntity;
import com.credit.bladi.exception.WorkflowException;
import com.credit.bladi.model.enums.DocumentStatus;
import com.credit.bladi.model.enums.ScanStatus;
import com.credit.bladi.model.workflow.DemandDocumentModel;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class DemandDocumentRepository implements IDemandDocumentRepository {
    private final DemandDocumentDatasource datasource;

    public DemandDocumentRepository(DemandDocumentDatasource datasource) {
        this.datasource = datasource;
    }

    @Override
    public Optional<Long> findIdByPublicId(UUID publicId) {
        return datasource.findByPublicId(publicId).map(DemandDocumentEntity::getId);
    }

    @Override
    public DemandDocumentModel save(DemandDocumentModel document) {
        DemandDocumentEntity entity = new DemandDocumentEntity();
        entity.setDemandId(document.demandId());
        entity.setStepCode(document.stepCode());
        entity.setStepName(document.stepName());
        entity.setOriginalFilename(document.originalFilename());
        entity.setStoredFilename(document.storedFilename());
        entity.setContentType(document.contentType());
        entity.setSizeBytes(document.sizeBytes());
        entity.setChecksumSha256(document.checksumSha256());
        entity.setDocumentType(document.documentType());
        entity.setStatus(document.status().name());
        entity.setScanStatus(document.scanStatus().name());
        entity.setUploadedBy(document.uploadedBy());
        entity.setUploadedAt(document.uploadedAt());
        datasource.save(entity);
        return toModel(entity);
    }

    @Override
    public List<DemandDocumentModel> findByDemandId(Long demandId) {
        return datasource.findByDemandIdOrderByUploadedAtAsc(demandId).stream()
            .map(this::toModel)
            .toList();
    }

    @Override
    public Optional<DemandDocumentModel> findById(Long demandId, Long documentId) {
        return datasource.findByIdAndDemandId(documentId, demandId).map(this::toModel);
    }

    @Override
    public Optional<DemandDocumentModel> findActiveByType(Long demandId, String documentType) {
        return datasource
            .findFirstByDemandIdAndDocumentTypeAndStatus(demandId, documentType, DocumentStatus.ACTIVE.name())
            .map(this::toModel);
    }

    @Override
    public void supersede(Long documentId, Long supersededByDocumentId) {
        DemandDocumentEntity entity = datasource.findById(documentId)
            .orElseThrow(() -> WorkflowException.of(DOCUMENT_NOT_FOUND));
        entity.setStatus(DocumentStatus.SUPERSEDED.name());
        entity.setSupersededById(supersededByDocumentId);
        datasource.save(entity);
    }

    private DemandDocumentModel toModel(DemandDocumentEntity e) {
        UUID supersededByPublicId = e.getSupersededById() == null ? null
            : datasource.findPublicIdById(e.getSupersededById()).orElse(null);
        return new DemandDocumentModel(e.getId(), e.getPublicId(), e.getDemandId(), e.getStepCode(), e.getStepName(),
            e.getOriginalFilename(), e.getStoredFilename(), e.getContentType(), e.getSizeBytes(),
            e.getChecksumSha256(), e.getDocumentType(), DocumentStatus.valueOf(e.getStatus()),
            ScanStatus.valueOf(e.getScanStatus()), e.getSupersededById(), supersededByPublicId, e.getUploadedBy(),
            e.getUploadedAt());
    }
}
