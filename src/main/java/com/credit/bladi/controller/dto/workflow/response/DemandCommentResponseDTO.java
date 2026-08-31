package com.credit.bladi.controller.dto.workflow.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(name = "DemandCommentResponseDTO", description = "A remark left on a demand's current step at the "
    + "time it was posted.")
public record DemandCommentResponseDTO(
    @Schema(description = "Comment identifier.", example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b") UUID id,
    @Schema(description = "Code of the step the demand was on when this comment was posted.",
        example = "RISK_ANALYSIS_STEP") String stepCode,
    @Schema(description = "Name of the step the demand was on when this comment was posted.",
        example = "Credit file counter-analysis") String stepName,
    @Schema(description = "Username of the author.", example = "analyst1") String username,
    @Schema(description = "The remark.", example = "Waiting for the client's supporting document.")
    String comment,
    @Schema(description = "When the comment was posted.") Instant createdAt
) {}
