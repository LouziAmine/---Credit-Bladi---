package com.credit.bladi.model.workflow;

import java.time.Instant;
import java.util.UUID;

public record DemandCommentModel(
        Long id,
        UUID publicId,
        String stepCode,
        String stepName,
        String username,
        String comment,
        Instant createdAt
) {
}
