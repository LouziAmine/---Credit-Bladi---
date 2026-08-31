package com.credit.bladi.controller.dto.user.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;

@Schema(
    name = "UpdateUserProfileRequestDTO",
    description = "Request payload to update a user's grade/team/email profile. Admin use only. "
        + "Any field left null clears that part of the profile."
)
public record UpdateUserProfileRequestDTO(
    @Schema(description = "Grade code to assign (see GET /api/v1/grades). Null clears the grade.",
        example = "ANALYSTE") String gradeCode,
    @Schema(description = "Team code to assign (see GET /api/v1/teams). Null clears the team.",
        example = "PAE") String teamCode,
    @Schema(description = "Email address, used to notify this user. Null clears it.",
        example = "john.doe@bladi-credit.ma")
    @Email(message = "{validation.user.email.pattern}")
    String email
) {}
