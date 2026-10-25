package com.credit.bladi.controller.dto.sector.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(name = "ActivitySectorResponseDTO", description = "An Activity Sector available for selection on "
    + "Enterprise/simulation forms.")
public record ActivitySectorResponseDTO(
    @Schema(description = "Sector id.", example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b") UUID id,
    @Schema(description = "Sector name.", example = "Textile") String name,
    @Schema(description = "When this sector was added.") Instant createdAt
) {}
