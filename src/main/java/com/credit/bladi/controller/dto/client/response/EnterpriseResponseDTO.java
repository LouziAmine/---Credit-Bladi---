package com.credit.bladi.controller.dto.client.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(name = "EnterpriseResponseDTO", description = "An Enterprise (client company) and its portfolio owner.")
public record EnterpriseResponseDTO(
    @Schema(description = "Enterprise id.", example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b") UUID id,
    @Schema(description = "Enterprise legal name.", example = "Atlas Textile SARL") String name,
    @Schema(description = "Economic sector (null if not set).", example = "Textile") String sector,
    @Schema(description = "RC/ICE-style business registration reference (null if not set). Required for a "
        + "client to self-service claim portal access to this Enterprise.", example = "RC-45210-CASABLANCA")
    String registrationNumber,
    @Schema(description = "When this Enterprise was created.") Instant createdAt
) {}
