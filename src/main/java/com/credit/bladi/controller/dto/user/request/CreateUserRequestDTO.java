package com.credit.bladi.controller.dto.user.request;

import com.credit.bladi.validator.ValidationConstraints;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(
    name = "CreateUserRequestDTO",
    description = "Request payload to create a new user account. Admin use only."
)
public record CreateUserRequestDTO(
    @Schema(
        description = "Username. Must be between 3 and 100 characters.",
        example = "john.doe",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "{validation.username.notBlank}")
    @Size(min = 3, max = 100, message = "{validation.username.size}")
    @Pattern(regexp = ValidationConstraints.USERNAME_REGEX, message = "{validation.username.pattern}")
    String username,

    @Schema(
        description = "Display name shown across the UI instead of the login username. Optional.",
        example = "Amine Louzi"
    )
    String fullName,

    @Schema(
        description = "Email address, used to notify this user (e.g. when a demand is assigned to them). "
            + "Optional.",
        example = "john.doe@bladi-credit.ma"
    )
    @Email(message = "{validation.user.email.pattern}")
    String email,

    @Schema(
        description = "Password. Must be between 12 and 100 characters (OWASP ASVS V2.1.1).",
        example = "secureP@ss1234",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "{validation.password.notBlank}")
    @Size(min = 12, max = 100, message = "{validation.password.size}")
    @Pattern(regexp = ValidationConstraints.PASSWORD_COMPLEXITY_REGEX, message = "{validation.password.pattern}")
    String password,

    @Schema(
        description = "Role assigned to the user. Must be one of: CLIENT, MANAGER.",
        example = "CLIENT",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "{validation.role.notNull}")
    @Pattern(regexp = ValidationConstraints.ROLE_REGEX, message = "{validation.role.pattern}")
    String role,

    @Schema(
        description = "Grade code to profile this user with (see GET /api/v1/grades). Optional.",
        example = "ANALYSTE"
    )
    String gradeCode,

    @Schema(
        description = "Team code to profile this user with (see GET /api/v1/teams). Optional.",
        example = "PAE"
    )
    String teamCode
) {
    @Override
    public String toString() {
        return String.format(
            "CreateUserRequestDTO [username=%s, password=***, role=%s]",
            username, role);
    }
}
