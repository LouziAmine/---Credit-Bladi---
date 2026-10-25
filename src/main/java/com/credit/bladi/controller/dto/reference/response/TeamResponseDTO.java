package com.credit.bladi.controller.dto.reference.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(name = "TeamResponseDTO", description = "A team/pool a user can belong to, used to route workflow "
    + "steps to the right responsible.")
public record TeamResponseDTO(
    @Schema(description = "Team id", example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b") UUID id,
    @Schema(description = "Team code", example = "PAE") String code,
    @Schema(description = "Team display name", example = "PAE (Risk Analysis and Commitments Division)") String name
) {}
