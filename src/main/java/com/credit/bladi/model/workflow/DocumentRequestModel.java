package com.credit.bladi.model.workflow;

import com.credit.bladi.model.enums.DocumentRequestStatus;
import java.time.Instant;
import java.util.UUID;

public record DocumentRequestModel(
        Long id,
        UUID publicId,
        Long demandId,
        UUID demandPublicId,
        String documentType,
        DocumentRequestStatus status,
        String requestedByUsername,
        Instant createdAt,
        Long fulfilledDocumentId,
        UUID fulfilledDocumentPublicId,
        Instant fulfilledAt
) {
}
