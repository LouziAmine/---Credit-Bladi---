package com.credit.bladi.controller.dto.workflow.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "AssignDemandRequestDTO", description = "Request payload to assign (or reassign) a demand's "
    + "current step to a specific user of the required grade/team.")
public record AssignDemandRequestDTO(
    @Schema(
        description = "Username of the user to assign the demand's current step to.",
        example = "analyst1",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "{validation.username.notBlank}")
    String username
) {}
