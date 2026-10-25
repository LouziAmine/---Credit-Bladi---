package com.credit.bladi.controller.dto.workflow.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.Instant;

@Schema(name = "DemandHistoryEntryResponseDTO", description = "One entry of a demand's step-by-step "
    + "processing history.")
public record DemandHistoryEntryResponseDTO(
    @Schema(description = "Process code.", example = "INITIATION") String processCode,
    @Schema(description = "Process name.", example = "File Initiation") String processName,
    @Schema(description = "Step code.", example = "INITIATION_STEP") String stepCode,
    @Schema(description = "Step name.", example = "Processing - File Initiation") String stepName,
    @Schema(description = "Who typically performs this step (informational only, not enforced by "
        + "role-based access control).", example = "Account Officer") String typicalRole,
    @Schema(description = "Grade code required to act on this step (null if not configured).",
        example = "ANALYSTE") String requiredGradeCode,
    @Schema(description = "Grade display name required to act on this step (null if not configured).",
        example = "Analyst") String requiredGradeName,
    @Schema(description = "Team code required to act on this step (null if not configured).",
        example = "PAE") String requiredTeamCode,
    @Schema(description = "Team display name required to act on this step (null if not configured).",
        example = "PAE (Risk Analysis and Commitments Division)") String requiredTeamName,
    @Schema(description = "Username of the manager who handled this step (null if unassigned).",
        example = "admin") String assignedUsername,
    @Schema(description = "Username of the manager/supervisor who performed this assignment "
        + "(null if nobody explicitly assigned this step: closed straight out of the team pool).",
        example = "resp.dept.pae") String assignedBy,
    @Schema(description = "When the demand entered this step.") Instant enteredAt,
    @Schema(description = "When the demand left this step (null if this is the current step).") Instant exitedAt,
    @Schema(description = "Optional note left when advancing past this step.", example = "File complete, "
        + "forwarded for risk counter-analysis") String decisionNote,
    @Schema(description = "Decision outcome recorded when advancing past this step (typically only set on "
        + "the DECISION_STEP, for either committee mode: Rotating or Standing-Session committee). One of: "
        + "APPROVED, DEFERRED, REJECTED. Null on every other step.", example = "APPROVED") String decisionOutcome,
    @Schema(description = "Structured return reason recorded when this step was "
        + "closed, distinct from the free-text decisionNote. Null if not applicable.",
        example = "Missing supporting document") String returnReasonCode,
    @Schema(description = "Committee recommendations recorded when this step was closed, typically only "
        + "set on the DECISION_STEP.", example = "Strengthen the quarterly monitoring of revenue")
    String recommendations,
    @Schema(description = "Committee reserves recorded when this step was closed, typically only set on "
        + "the DECISION_STEP.", example = "Subject to the mortgage guarantee being put in place")
    String reserves,
    @Schema(description = "Committee type recorded when this step was closed, typically "
        + "only set on RISK_COMMITTEE_PROG_STEP. One of: ROTATING, STANDING_SESSION. Null on every other "
        + "step.", example = "ROTATING") String committeeType,
    @Schema(description = "Whether the ADI (death and disability insurance) on the guarantor/director was "
        + "confirmed subscribed when this step was closed: only meaningful on INSURANCE_VALIDATION_STEP, "
        + "and required (advance is rejected otherwise) before advancing past it.", example = "true")
    Boolean insuranceAdiSubscribed,
    @Schema(description = "Insurance coverage type recorded when this step was closed: whether the ADI "
        + "is the bank's group policy or an external insurance delegation (loi 110-14). One of: "
        + "BANK_GROUP_POLICY, EXTERNAL_DELEGATION. Only meaningful on INSURANCE_VALIDATION_STEP.",
        example = "BANK_GROUP_POLICY") String insuranceCoverageType,
    @Schema(description = "Whether the property-damage/multi-risk policy on the demand's real guarantees was "
        + "confirmed subscribed when this step was closed: only meaningful on INSURANCE_VALIDATION_STEP, "
        + "and required whenever the demand has a non-zero guaranteesValue.", example = "true")
    Boolean insuranceDamageSubscribed,
    @Schema(description = "Rate decision made when this step was closed. One of: KEEP, MANUAL. Set on "
        + "every step visit (mandatory on every advance/return).", example = "KEEP") String rateOption,
    @Schema(description = "The demand's applicable rate (%) before this step's rate decision (null only "
        + "if the demand had no rate yet before this step).", example = "7.00") BigDecimal previousRate,
    @Schema(description = "The demand's applicable rate (%) after this step's rate decision.",
        example = "7.25") BigDecimal appliedRate,
    @Schema(description = "Username of the acting user who made this step's rate decision.",
        example = "hamza.idrissi") String rateChangedBy,
    @Schema(description = "Amount decision made when this step was closed. One of: KEEP, MANUAL. Set on "
        + "every step visit (mandatory on every advance/return).", example = "KEEP") String amountOption,
    @Schema(description = "The demand's financing amount (MAD) before this step's amount decision.",
        example = "1500000.00") BigDecimal previousAmount,
    @Schema(description = "The demand's financing amount (MAD) after this step's amount decision.",
        example = "1500000.00") BigDecimal appliedAmount,
    @Schema(description = "Duration decision made when this step was closed. One of: KEEP, MANUAL. Set on "
        + "every step visit (mandatory on every advance/return).", example = "KEEP") String durationOption,
    @Schema(description = "The demand's financing duration (months) before this step's duration decision.",
        example = "84") Integer previousDuration,
    @Schema(description = "The demand's financing duration (months) after this step's duration decision.",
        example = "84") Integer appliedDuration,
    @Schema(description = "Classification outcome recorded when closing CLASSIFICATION_STEP (set only on "
        + "that step). One of: REAL, FAKE, SIMULATION_ONLY. Null on every other step.", example = "REAL")
    String classificationOutcome
) {}
