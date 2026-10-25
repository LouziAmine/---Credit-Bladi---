package com.credit.bladi.model.workflow;

import java.time.Instant;
import java.util.UUID;

public record DemandAssignmentHistoryModel(
        Long id,
        UUID publicId,
        String stepCode,
        String stepName,
        String previousUsername,
        String newUsername,
        String assignedBy,
        Instant assignedAt
) {
}
