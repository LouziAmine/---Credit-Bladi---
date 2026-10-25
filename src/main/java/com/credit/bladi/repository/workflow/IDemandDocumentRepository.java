package com.credit.bladi.repository.workflow;

import com.credit.bladi.model.workflow.DemandDocumentModel;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IDemandDocumentRepository {
    Optional<Long> findIdByPublicId(UUID publicId);

    DemandDocumentModel save(DemandDocumentModel document);

    List<DemandDocumentModel> findByDemandId(Long demandId);

    Optional<DemandDocumentModel> findById(Long demandId, Long documentId);

    Optional<DemandDocumentModel> findActiveByType(Long demandId, String documentType);

    void supersede(Long documentId, Long supersededByDocumentId);
}
