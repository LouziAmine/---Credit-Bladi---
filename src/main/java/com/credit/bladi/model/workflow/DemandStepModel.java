package com.credit.bladi.model.workflow;

import java.time.Instant;

public record DemandStepModel(
        String processCode,
        String processName,
        String stepCode,
        String stepName,
        String typicalRole,
        String requiredGradeCode,
        String requiredGradeName,
        String requiredTeamCode,
        String requiredTeamName,
        String assignedUsername,
        String assignedBy,
        Instant enteredAt
) {
}
