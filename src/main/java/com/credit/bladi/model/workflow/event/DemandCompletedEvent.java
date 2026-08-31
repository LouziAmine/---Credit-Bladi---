package com.credit.bladi.model.workflow.event;

public record DemandCompletedEvent(Long demandId, String triggeredByUsername) {
}
