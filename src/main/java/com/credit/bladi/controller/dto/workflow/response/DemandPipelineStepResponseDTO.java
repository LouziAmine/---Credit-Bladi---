package com.credit.bladi.controller.dto.workflow.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(name = "DemandPipelineStepResponseDTO", description = "One step of a demand's full workflow pipeline, "
    + "with its position and completion status, so a client can render a step-by-step progress view "
    + "(e.g. \"step 6 of 14\").")
public record DemandPipelineStepResponseDTO(
    @Schema(description = "Process code.", example = "RISK_ANALYSIS") String processCode,
    @Schema(description = "Process name.", example = "Risk Analysis") String processName,
    @Schema(description = "Step code.", example = "RISK_ANALYSIS_STEP") String stepCode,
    @Schema(description = "Step name.", example = "Processing - Risk Analysis") String stepName,
    @Schema(description = "Who typically performs this step (informational only, not enforced by "
        + "role-based access control).", example = "PAE Analyst") String typicalRole,
    @Schema(description = "Grade code required to act on this step (null if not configured).",
        example = "ANALYSTE") String requiredGradeCode,
    @Schema(description = "Grade display name required to act on this step (null if not configured).",
        example = "Analyste") String requiredGradeName,
    @Schema(description = "Team code required to act on this step (null if not configured).",
        example = "PAE") String requiredTeamCode,
    @Schema(description = "Team display name required to act on this step (null if not configured).",
        example = "PAE (Risk Analysis and Commitments Division)") String requiredTeamName,
    @Schema(description = "Absolute position of this step in the workflow's full pipeline (1-based).",
        example = "6") Integer globalOrder,
    @Schema(description = "Whether the demand has completed this step, is currently on it, "
        + "or hasn't reached it yet.", example = "CURRENT", allowableValues = {"DONE", "CURRENT", "UPCOMING"})
    String status,
    @Schema(description = "When the demand entered this step (null if UPCOMING).") Instant enteredAt,
    @Schema(description = "When the demand exited this step (null if CURRENT or UPCOMING).") Instant exitedAt
) {}
