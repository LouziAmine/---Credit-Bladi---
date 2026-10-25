package com.credit.bladi.repository.workflow;

import com.credit.bladi.model.workflow.DocumentRequestModel;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IDocumentRequestRepository {
    DocumentRequestModel create(Long demandId, String documentType, String requestedByUsername);

    Optional<DocumentRequestModel> findById(Long id);

    Optional<Long> findIdByPublicId(UUID publicId);

    List<DocumentRequestModel> findByDemandId(Long demandId);

    DocumentRequestModel fulfill(Long id, Long fulfilledDocumentId, Instant fulfilledAt);
}
