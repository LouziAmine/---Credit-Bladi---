package com.credit.bladi.controller.dto.workflow.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(name = "DemandStepResponseDTO", description = "Current (or last, if completed) processing step of "
    + "a demand.")
public record DemandStepResponseDTO(
    @Schema(description = "Process code.", example = "RISK_ANALYSIS") String processCode,
    @Schema(description = "Process name.", example = "Risk Analysis") String processName,
    @Schema(description = "Step code.", example = "RISK_ANALYSIS_STEP") String stepCode,
    @Schema(description = "Step name.", example = "Processing - Risk Analysis") String stepName,
    @Schema(description = "Who typically performs this step (informational only, not enforced by "
        + "role-based access control).", example = "PAE Analyst") String typicalRole,
    @Schema(description = "Grade code required to act on this step (null if not configured).",
        example = "ANALYSTE") String requiredGradeCode,
    @Schema(description = "Grade display name required to act on this step (null if not configured).",
        example = "Analyst") String requiredGradeName,
    @Schema(description = "Team code required to act on this step (null if not configured).",
        example = "PAE") String requiredTeamCode,
    @Schema(description = "Team display name required to act on this step (null if not configured).",
        example = "PAE (Risk Analysis and Commitments Division)") String requiredTeamName,
    @Schema(description = "Username of the manager currently assigned to this step (null if unassigned).",
        example = "admin") String assignedUsername,
    @Schema(description = "Username of the manager/supervisor who performed this assignment "
        + "(null if nobody has explicitly assigned this step yet: still in the team pool).",
        example = "resp.dept.pae") String assignedBy,
    @Schema(description = "When the demand entered this step.") Instant enteredAt
) {}
