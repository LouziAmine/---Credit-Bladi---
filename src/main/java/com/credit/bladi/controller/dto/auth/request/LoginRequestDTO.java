package com.credit.bladi.controller.dto.auth.request;

import com.credit.bladi.validator.ValidationConstraints;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(
    name = "LoginRequestDTO",
    description = "Credentials payload for user authentication."
)
public record LoginRequestDTO(
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
        description = "User password. Must be between 6 and 100 characters.",
        example = "secureP@ss1",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "{validation.password.notBlank}")
    @Size(min = 6, max = 100, message = "{validation.password.size.login}")
    String password
) {
    @Override
    public String toString() {
        return String.format(
            "LoginRequestDTO [username=%s, password=***]",
            username);
    }
}
