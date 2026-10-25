package com.credit.bladi.controller.dto.user.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(name = "UserResponseDTO", description = "User account info.")
public record UserResponseDTO(
    @Schema(description = "User id", example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b") UUID id,
    @Schema(description = "Username", example = "john.doe") String username,
    @Schema(description = "Display name (null if not set).", example = "Amine Louzi") String fullName,
    @Schema(description = "Email address (null if not set).", example = "john.doe@bladi-credit.ma") String email,
    @Schema(description = "User role", example = "CLIENT") String role,
    @Schema(description = "Grade code (null if not profiled).", example = "ANALYSTE") String gradeCode,
    @Schema(description = "Grade display name (null if not profiled).", example = "Analyst") String gradeName,
    @Schema(description = "Team code (null if not profiled).", example = "PAE") String teamCode,
    @Schema(description = "Team display name (null if not profiled).",
        example = "PAE (Risk Analysis and Commitments Division)") String teamName
) {}
