package com.credit.bladi.controller.dto.auth.request;

import com.credit.bladi.validator.ValidationConstraints;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Schema(
    name = "LoginRequestDTO",
    description = "Credentials payload for user authentication."
)
public class LoginRequestDTO {

    @Schema(
        description = "Username. Must be between 3 and 100 characters.",
        example = "john.doe",
        requiredMode = Schema.RequiredMode.REQUIRED
    )

    @NotBlank(message = "username must not be blank")
    @Size(min = 3, max = 100, message = "username must be between 3 and 100 characters")
    @Pattern(regexp = ValidationConstraints.USERNAME_REGEX,
        message = "username may only contain letters, digits, dots, underscores and hyphens")
    private String username;

    @Schema(
        description = "User password. Must be between 6 and 100 characters.",
        example = "secureP@ss1",
        requiredMode = Schema.RequiredMode.REQUIRED
    )

    @NotBlank(message = "password must not be blank")
    @Size(min = 6, max = 100, message = "password must be between 6 and 100 characters")
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
        return o instanceof LoginRequestDTO && EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return String.format(
            "LoginRequestDTO [username=%s, password=***]",
            username);
    }
}