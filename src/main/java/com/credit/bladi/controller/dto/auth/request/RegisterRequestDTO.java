package com.credit.bladi.controller.dto.auth.request;

import com.credit.bladi.validator.ValidationConstraints;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Schema(
    name = "RegisterRequestDTO",
    description = "Registration payload for new user self-registration."
)
public class RegisterRequestDTO {

    @Schema(
        description = "Desired username. Must be between 3 and 100 characters.",
        example = "john.doe",
        requiredMode = Schema.RequiredMode.REQUIRED
    )

    @NotBlank(message = "username must not be blank")
    @Size(min = 3, max = 100, message = "username must be between 3 and 100 characters")
    @Pattern(regexp = ValidationConstraints.USERNAME_REGEX,
        message = "username may only contain letters, digits, dots, underscores and hyphens")
    private String username;

    @Schema(
        description = "Password. Must be between 12 and 100 characters (OWASP ASVS V2.1.1).",
        example = "secureP@ss1234",
        requiredMode = Schema.RequiredMode.REQUIRED
    )

    @NotBlank(message = "password must not be blank")
    @Size(min = 12, max = 100, message = "password must be between 12 and 100 characters")
    @Pattern(regexp = ValidationConstraints.PASSWORD_COMPLEXITY_REGEX,
        message = "password must contain at least one uppercase letter, one lowercase letter and one digit")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof RegisterRequestDTO && EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return String.format(
            "RegisterRequestDTO [username=%s, password=***]",
            username);
    }
}