package com.credit.bladi.controller.dto.user.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "UserResponseDTO", description = "User account info.")
public record UserResponseDTO(
    @Schema(description = "User id", example = "42") Long id,
    @Schema(description = "Username", example = "john.doe") String username,
    @Schema(description = "User role", example = "CLIENT") String role
) {}
