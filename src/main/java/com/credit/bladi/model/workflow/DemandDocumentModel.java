package com.credit.bladi.model.workflow;

import com.credit.bladi.model.enums.DocumentStatus;
import com.credit.bladi.model.enums.ScanStatus;
import java.time.Instant;
import java.util.UUID;

public record DemandDocumentModel(
        Long id,
        UUID publicId,
        Long demandId,
        String stepCode,
        String stepName,
        String originalFilename,
        String storedFilename,
        String contentType,
        Long sizeBytes,
        String checksumSha256,
        String documentType,
        DocumentStatus status,
        ScanStatus scanStatus,
        Long supersededById,
        UUID supersededByPublicId,
        String uploadedBy,
        Instant uploadedAt
) {
}
