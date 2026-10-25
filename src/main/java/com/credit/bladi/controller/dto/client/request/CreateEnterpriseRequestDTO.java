package com.credit.bladi.controller.dto.client.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(
    name = "CreateEnterpriseRequestDTO",
    description = "Request payload to create a new Enterprise (client company). Manager use only."
)
public record CreateEnterpriseRequestDTO(
    @Schema(description = "Enterprise legal name.", example = "Atlas Textile SARL",
        requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{validation.createEnterprise.name.notBlank}")
    @Size(max = 200, message = "{validation.createEnterprise.name.size}")
    String name,

    @Schema(description = "Economic sector (free text). Optional.", example = "Textile")
    @Size(max = 100, message = "{validation.createEnterprise.sector.size}")
    String sector,

    @Schema(description = "RC/ICE-style business registration reference. Optional, but required for the "
        + "company's responsable to later self-service claim portal access to this Enterprise's portfolio "
        + "(see POST /api/v1/me/enterprise-links): a fuzzy company-name match is not sufficient proof of "
        + "authority to see the company's credit history.", example = "RC-45210-CASABLANCA")
    @Size(max = 50, message = "{validation.createEnterprise.registrationNumber.size}")
    String registrationNumber
) {}
