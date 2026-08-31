package com.credit.bladi.model.workflow;

import com.credit.bladi.model.audit.AuditChanges;

public record StepDecisions(RateDecision rate, AmountDecision amount, DurationDecision duration) {
    public AuditChanges termChanges() {
        return AuditChanges.of()
            .field("rate", rate.previousRate(), rate.appliedRate())
            .field("amount", amount.previousAmount(), amount.appliedAmount())
            .field("duration", duration.previousDuration(), duration.appliedDuration());
    }
}
