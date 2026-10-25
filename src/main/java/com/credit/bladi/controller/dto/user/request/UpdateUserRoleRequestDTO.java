package com.credit.bladi.controller.dto.user.request;

import com.credit.bladi.validator.ValidationConstraints;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Schema(
    name = "UpdateUserRoleRequestDTO",
    description = "Request payload to update a user's role. Admin use only."
)
public record UpdateUserRoleRequestDTO(
    @Schema(
        description = "New role to assign to the user. Must be one of: CLIENT, MANAGER.",
        example = "MANAGER",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "{validation.role.notNull}")
    @Pattern(regexp = ValidationConstraints.ROLE_REGEX, message = "{validation.role.pattern}")
    String role
) {}
