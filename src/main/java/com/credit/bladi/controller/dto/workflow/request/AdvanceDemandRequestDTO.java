package com.credit.bladi.controller.dto.workflow.request;

import com.credit.bladi.validator.ValidationConstraints;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

@Schema(name = "AdvanceDemandRequestDTO", description = "Request payload to advance a demand to its "
    + "next processing step. Admin use only.")
public record AdvanceDemandRequestDTO(
    @Schema(description = "Optional note explaining the decision to advance this demand (e.g. review "
        + "outcome, missing documents resolved).", example = "File complete, forwarded for "
        + "risk counter-analysis")
    @Size(max = 500, message = "{validation.advanceDemand.note.size}")
    String note,

    @Schema(description = "Optional decision outcome, typically only meaningful when advancing past the "
        + "DECISION_STEP (either committee mode: Rotating or Standing-Session committee). Must be one "
        + "of: APPROVED, DEFERRED, REJECTED.", example = "APPROVED")
    @Pattern(regexp = "^(APPROVED|DEFERRED|REJECTED)$", message = "{validation.advanceDemand.decisionOutcome.pattern}")
    String decisionOutcome,

    @Schema(description = "Optional structured return reason, used when returning "
        + "the file for completion (e.g. a request for additional information): distinct from the "
        + "free-text note. Not validated against a referential table in this first version.",
        example = "Missing supporting document")
    @Size(max = 100, message = "{validation.advanceDemand.returnReasonCode.size}")
    String returnReasonCode,

    @Schema(description = "Optional committee recommendations, typically only meaningful when advancing "
        + "past the DECISION_STEP.", example = "Strengthen the quarterly monitoring of revenue")
    @Size(max = 500, message = "{validation.advanceDemand.recommendations.size}")
    String recommendations,

    @Schema(description = "Optional committee reserves, typically only meaningful when advancing past the "
        + "DECISION_STEP.", example = "Subject to the mortgage guarantee being put in place")
    @Size(max = 500, message = "{validation.advanceDemand.reserves.size}")
    String reserves,

    @Schema(description = "Optional committee type, typically only meaningful when "
        + "advancing past RISK_COMMITTEE_PROG_STEP (chosen when programming the committee's agenda, "
        + "before the decision itself). Must be one of: ROTATING, STANDING_SESSION.",
        example = "ROTATING")
    @Pattern(regexp = "^(ROTATING|STANDING_SESSION)$", message = "{validation.advanceDemand.committeeType.pattern}")
    String committeeType,

    @Schema(description = "Whether the ADI (death and disability insurance) on the guarantor/director is "
        + "confirmed subscribed. Only meaningful when advancing past INSURANCE_VALIDATION_STEP: required "
        + "(true) to advance past it forward; ignored when returning the file.", example = "true")
    Boolean insuranceAdiSubscribed,

    @Schema(description = "Insurance coverage type: whether the ADI is the bank's group policy or an "
        + "external insurance delegation the client is entitled to under loi 110-14. Only meaningful "
        + "when advancing past INSURANCE_VALIDATION_STEP. Must be one of: BANK_GROUP_POLICY, "
        + "EXTERNAL_DELEGATION.", example = "BANK_GROUP_POLICY")
    @Pattern(regexp = "^(BANK_GROUP_POLICY|EXTERNAL_DELEGATION)$",
        message = "{validation.advanceDemand.insuranceCoverageType.pattern}")
    String insuranceCoverageType,

    @Schema(description = "Whether the property-damage/multi-risk policy on the demand's real guarantees "
        + "(pledge/mortgage/lien) is confirmed subscribed. Only meaningful when advancing past "
        + "INSURANCE_VALIDATION_STEP: required (true) whenever the demand has a non-zero guaranteesValue.",
        example = "true")
    Boolean insuranceDamageSubscribed,

    @Schema(description = "Username to assign the step being opened (the next step, or the previous one "
        + "if returnReasonCode is set) to. Required unless this call completes the demand (advancing past "
        + "its last step, with no returnReasonCode): there is then no step left to assign.",
        example = "analyst2")
    @Size(max = 100, message = "{validation.advanceDemand.assigneeUsername.size}")
    String assigneeUsername,

    @Schema(description = "Mandatory choice for how this step's decision handles the credit's interest "
        + "rate. KEEP reuses the demand's current applicable rate as-is (rejected if the demand has none "
        + "yet: happens on demands created before this field existed, until their first MANUAL). MANUAL "
        + "applies manualRate directly. Required on every advance call, forward or return, and recorded "
        + "on the closed step for GET /{id}/history.",
        example = "KEEP")
    @NotBlank(message = "{validation.advanceDemand.rateOption.notBlank}")
    @Pattern(regexp = "^(KEEP|MANUAL)$", message = "{validation.advanceDemand.rateOption.pattern}")
    String rateOption,

    @Schema(description = "Rate value (%) typed directly by the acting user. Required when "
        + "rateOption=MANUAL (ignored otherwise); must be strictly positive and at most 100.",
        example = "7.25")
    @DecimalMin(value = "0.01", message = "{validation.advanceDemand.manualRate.min}")
    @DecimalMax(value = "100", message = "{validation.advanceDemand.manualRate.max}")
    BigDecimal manualRate,

    @Schema(description = "Mandatory choice for how this step's decision handles the demand's financing "
        + "amount. KEEP reuses the demand's current amount as-is. MANUAL applies manualAmount directly. "
        + "Required on every advance call, forward or return, and recorded on the closed step for "
        + "GET /{id}/history.", example = "KEEP")
    @NotBlank(message = "{validation.advanceDemand.amountOption.notBlank}")
    @Pattern(regexp = "^(KEEP|MANUAL)$", message = "{validation.advanceDemand.amountOption.pattern}")
    String amountOption,

    @Schema(description = "Financing amount (MAD) typed directly by the acting user. Required when "
        + "amountOption=MANUAL (ignored otherwise).", example = "1500000.00")
    @DecimalMin(value = ValidationConstraints.ENTERPRISE_AMOUNT_MIN,
        message = "{validation.advanceDemand.manualAmount.min}")
    @DecimalMax(value = ValidationConstraints.ENTERPRISE_AMOUNT_MAX,
        message = "{validation.advanceDemand.manualAmount.max}")
    BigDecimal manualAmount,

    @Schema(description = "Mandatory choice for how this step's decision handles the demand's financing "
        + "duration. KEEP reuses the demand's current duration as-is. MANUAL applies manualDuration "
        + "directly. Required on every advance call, forward or return, and recorded on the closed step "
        + "for GET /{id}/history.", example = "KEEP")
    @NotBlank(message = "{validation.advanceDemand.durationOption.notBlank}")
    @Pattern(regexp = "^(KEEP|MANUAL)$", message = "{validation.advanceDemand.durationOption.pattern}")
    String durationOption,

    @Schema(description = "Financing duration (months) typed directly by the acting user. Required when "
        + "durationOption=MANUAL (ignored otherwise).", example = "84")
    @Min(value = ValidationConstraints.DURATION_MIN, message = "{validation.advanceDemand.manualDuration.min}")
    @Max(value = ValidationConstraints.DURATION_MAX, message = "{validation.advanceDemand.manualDuration.max}")
    Integer manualDuration
) {}
