package com.credit.bladi.model.workflow.event;

public record CommitteeDecisionEvent(Long demandId, String decidedByUsername) {
}
