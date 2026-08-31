package com.credit.bladi.controller.dto.auth.request;

import com.credit.bladi.validator.ValidationConstraints;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(
    name = "RegisterRequestDTO",
    description = "Registration payload for new user self-registration."
)
public record RegisterRequestDTO(
    @Schema(
        description = "Desired username. Must be between 3 and 100 characters.",
        example = "john.doe",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "{validation.username.notBlank}")
    @Size(min = 3, max = 100, message = "{validation.username.size}")
    @Pattern(regexp = ValidationConstraints.USERNAME_REGEX, message = "{validation.username.pattern}")
    String username,

    @Schema(
        description = "Password. Must be between 12 and 100 characters (OWASP ASVS V2.1.1).",
        example = "secureP@ss1234",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "{validation.password.notBlank}")
    @Size(min = 12, max = 100, message = "{validation.password.size}")
    @Pattern(regexp = ValidationConstraints.PASSWORD_COMPLEXITY_REGEX, message = "{validation.password.pattern}")
    String password
) {
    @Override
    public String toString() {
        return String.format(
            "RegisterRequestDTO [username=%s, password=***]",
            username);
    }
}
