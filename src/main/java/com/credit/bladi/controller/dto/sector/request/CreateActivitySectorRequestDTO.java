package com.credit.bladi.controller.dto.sector.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(
    name = "CreateActivitySectorRequestDTO",
    description = "Request payload to add a new Activity Sector to the shared list. Manager use only."
)
public record CreateActivitySectorRequestDTO(
    @Schema(description = "Sector name.", example = "Textile", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{validation.createActivitySector.name.notBlank}")
    @Size(max = 100, message = "{validation.createActivitySector.name.size}")
    String name
) {}
