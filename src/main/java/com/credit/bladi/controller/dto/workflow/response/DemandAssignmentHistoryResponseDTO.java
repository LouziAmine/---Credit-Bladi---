package com.credit.bladi.controller.dto.workflow.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(name = "DemandAssignmentHistoryResponseDTO", description = "One assignment or reassignment event on "
    + "a demand's step, so it stays fully traceable regardless of who performed it.")
public record DemandAssignmentHistoryResponseDTO(
    @Schema(description = "Assignment history entry identifier.", example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b")
    UUID id,
    @Schema(description = "Code of the step the demand was on when this (re)assignment happened.",
        example = "RISK_ANALYSIS_STEP") String stepCode,
    @Schema(description = "Name of the step the demand was on when this (re)assignment happened.",
        example = "Credit file counter-analysis") String stepName,
    @Schema(description = "Username previously assigned to the step, null if this was the first assignment.",
        example = "analyst1") String previousUsername,
    @Schema(description = "Username newly assigned to the step.", example = "analyst2") String newUsername,
    @Schema(description = "Username of the user who performed the (re)assignment.", example = "supervisor1")
    String assignedBy,
    @Schema(description = "When the (re)assignment happened.") Instant assignedAt
) {}
