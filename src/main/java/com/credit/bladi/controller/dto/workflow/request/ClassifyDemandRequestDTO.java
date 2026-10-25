package com.credit.bladi.controller.dto.workflow.request;

import com.credit.bladi.validator.ValidationConstraints;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.UUID;

@Schema(name = "ClassifyDemandRequestDTO", description = "Request payload to classify a demand still at "
    + "CLASSIFICATION_STEP: the account officer's initial validation with the client, before the demand "
    + "either joins the client's Enterprise portfolio and enters the real pipeline (REAL), or is rejected "
    + "(FAKE / SIMULATION_ONLY). Manager use only.")
public record ClassifyDemandRequestDTO(
    @Schema(description = "Classification outcome. REAL requires enterpriseId. FAKE/SIMULATION_ONLY reject "
        + "the demand: it never enters the pipeline or any portfolio.", example = "REAL",
        requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{validation.classifyDemand.classificationOutcome.notBlank}")
    @Pattern(regexp = ValidationConstraints.CLASSIFICATION_OUTCOME_REGEX,
        message = "{validation.classifyDemand.classificationOutcome.pattern}")
    String classificationOutcome,

    @Schema(description = "Id of the Enterprise (client company) this demand belongs to: the same id "
        + "returned by GET /api/v1/enterprises. Required when classificationOutcome=REAL (ignored otherwise).",
        example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b")
    UUID enterpriseId,

    @Schema(description = "Username to assign INITIATION_STEP to once classified REAL. Required unless "
        + "INITIATION_STEP has no required grade/team.", example = "chargee.affaires1")
    @Size(max = 100, message = "{validation.classifyDemand.assigneeUsername.size}")
    String assigneeUsername,

    @Schema(description = "Optional note explaining the classification decision.",
        example = "Client confirmed by phone, file appears genuine")
    @Size(max = 500, message = "{validation.classifyDemand.note.size}")
    String note,

    @Schema(description = "The client's RIB, captured now while validating the file with them "
        + "(classificationOutcome=REAL only: ignored otherwise). Recorded on the Enterprise as the "
        + "default RIB later pre-filled when the client declares a monthly installment payment; they can still "
        + "type a different one at that point.", example = "230 780 0123456789012345 95")
    @Size(min = 10, max = 34, message = "{validation.classifyDemand.clientRib.size}")
    String clientRib
) {}
