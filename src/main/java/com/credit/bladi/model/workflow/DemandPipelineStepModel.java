package com.credit.bladi.model.workflow;

import java.time.Instant;

public record DemandPipelineStepModel(
        String processCode,
        String processName,
        String stepCode,
        String stepName,
        String typicalRole,
        String requiredGradeCode,
        String requiredGradeName,
        String requiredTeamCode,
        String requiredTeamName,
        Integer globalOrder,
        String status,
        Instant enteredAt,
        Instant exitedAt
) {
}
