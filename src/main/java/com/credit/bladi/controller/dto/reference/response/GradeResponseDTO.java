package com.credit.bladi.controller.dto.reference.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(name = "GradeResponseDTO", description = "A hierarchical profile (grade) a user can be assigned, "
    + "used to route workflow steps to the right responsible.")
public record GradeResponseDTO(
    @Schema(description = "Grade id", example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b") UUID id,
    @Schema(description = "Grade code", example = "ANALYSTE") String code,
    @Schema(description = "Grade display name", example = "Analyste") String name
) {}
