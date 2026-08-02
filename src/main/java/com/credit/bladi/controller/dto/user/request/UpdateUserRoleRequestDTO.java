package com.credit.bladi.controller.dto.user.request;

import com.credit.bladi.validator.ValidationConstraints;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Schema(
    name = "UpdateUserRoleRequestDTO",
    description = "Request payload to update a user's role. Admin use only."
)
public class UpdateUserRoleRequestDTO {

    @Schema(
        description = "New role to assign to the user. Must be one of: CLIENT, MANAGER.",
        example = "MANAGER",
        requiredMode = Schema.RequiredMode.REQUIRED
    )

    @NotNull(message = "role must not be null")
    @Pattern(regexp = ValidationConstraints.ROLE_REGEX, message = "role must be one of: CLIENT, MANAGER")
    private String role;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof UpdateUserRoleRequestDTO && EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return String.format(
            "UpdateUserRoleRequestDTO [role=%s]",
            role);
    }
}