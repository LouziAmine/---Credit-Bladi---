package com.credit.bladi.controller.dto.enterprise.request;

import com.credit.bladi.validator.ValidationConstraints;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(
    name = "ClaimEnterpriseLinkRequestDTO",
    description = "A CLIENT self-service claims portal access to an Enterprise by its exact RC/ICE-style "
        + "registration number: never a fuzzy company-name match, which is not sufficient proof of "
        + "authority to see the company's credit history. Always starts PENDING until a Manager reviews it."
)
public record ClaimEnterpriseLinkRequestDTO(
    @Schema(description = "RC/ICE-style business registration reference of the Enterprise being claimed.",
        example = "RC-45210-CASABLANCA", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{validation.claimEnterpriseLink.registrationNumber.notBlank}")
    @Size(max = 50, message = "{validation.claimEnterpriseLink.registrationNumber.size}")
    String registrationNumber,

    @Schema(description = "RESPONSABLE (the company's primary contact) or DELEGUE (a delegate, e.g. an "
        + "accountant).", example = "RESPONSABLE", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{validation.claimEnterpriseLink.role.notBlank}")
    @Pattern(regexp = ValidationConstraints.ENTERPRISE_LINK_ROLE_REGEX,
        message = "{validation.claimEnterpriseLink.role.pattern}")
    String role
) {}
