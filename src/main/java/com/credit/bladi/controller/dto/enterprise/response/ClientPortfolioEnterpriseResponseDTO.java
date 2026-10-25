package com.credit.bladi.controller.dto.enterprise.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(name = "ClientPortfolioEnterpriseResponseDTO",
    description = "An Enterprise the caller has approved portfolio access to.")
public record ClientPortfolioEnterpriseResponseDTO(
    @Schema(description = "Enterprise id.", example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b") UUID enterpriseId,
    @Schema(description = "Enterprise legal name.", example = "Atlas Textile SARL") String name,
    @Schema(description = "Economic sector (null if not set).", example = "Textile") String sector,
    @Schema(description = "The caller's role on this Enterprise: RESPONSABLE or DELEGUE.",
        example = "RESPONSABLE") String linkRole
) {}
