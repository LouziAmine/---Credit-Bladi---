package com.credit.bladi.controller.dto.auth.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "AuthUserResponseDTO", description = "Authenticated user info returned after login.")
public record AuthUserResponseDTO(
    @Schema(description = "Authenticated username", example = "john.doe") String username,
    @Schema(description = "User role", example = "MANAGER") String role
) {}
