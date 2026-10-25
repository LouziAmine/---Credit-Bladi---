package com.credit.bladi.controller.dto.invitation.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(name = "ClientInvitationPreviewResponseDTO",
    description = "Preview of a pending client invitation, shown before the invitee sets a password.")
public record ClientInvitationPreviewResponseDTO(
    @Schema(description = "The invited email address.", example = "drcontable@cgi.com") String email,
    @Schema(description = "Name of the Enterprise this invitation grants access to; null for a mortgage borrower.",
        example = "Atlas Textile SARL") String enterpriseName,
    @Schema(description = "Reference of the mortgage dossier this invitation grants access to; null for an Enterprise.",
        example = "DEM-3F2A9C1B4D5E") String demandReference,
    @Schema(description = "When this invitation link stops being valid.") Instant expiresAt
) {}
