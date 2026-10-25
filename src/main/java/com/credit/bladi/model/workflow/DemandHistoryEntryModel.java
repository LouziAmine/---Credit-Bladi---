package com.credit.bladi.model.workflow;

import java.math.BigDecimal;
import java.time.Instant;

public record DemandHistoryEntryModel(
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
        Instant enteredAt,
        Instant exitedAt,
        String decisionNote,
        String decisionOutcome,
        String returnReasonCode,
        String recommendations,
        String reserves,
        String committeeType,
        Boolean insuranceAdiSubscribed,
        String insuranceCoverageType,
        Boolean insuranceDamageSubscribed,
        String rateOption,
        BigDecimal previousRate,
        BigDecimal appliedRate,
        String rateChangedBy,
        String amountOption,
        BigDecimal previousAmount,
        BigDecimal appliedAmount,
        String durationOption,
        Integer previousDuration,
        Integer appliedDuration,
        String classificationOutcome
) {
}
