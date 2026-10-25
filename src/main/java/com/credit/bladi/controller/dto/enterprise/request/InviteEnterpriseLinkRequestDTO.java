package com.credit.bladi.controller.dto.enterprise.request;

import com.credit.bladi.validator.ValidationConstraints;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(
    name = "InviteEnterpriseLinkRequestDTO",
    description = "Manager invites an already-registered CLIENT to access an Enterprise's portfolio. The "
        + "Manager's own action is the required human verification, so the link is created APPROVED "
        + "immediately: no separate client-side acceptance step."
)
public record InviteEnterpriseLinkRequestDTO(
    @Schema(description = "Username of the CLIENT account to grant access to.", example = "jdupont",
        requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{validation.inviteEnterpriseLink.username.notBlank}")
    String username,

    @Schema(description = "RESPONSABLE (the company's primary contact) or DELEGUE (a delegate, e.g. an "
        + "accountant).", example = "RESPONSABLE", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{validation.inviteEnterpriseLink.role.notBlank}")
    @Pattern(regexp = ValidationConstraints.ENTERPRISE_LINK_ROLE_REGEX,
        message = "{validation.inviteEnterpriseLink.role.pattern}")
    String role
) {}
