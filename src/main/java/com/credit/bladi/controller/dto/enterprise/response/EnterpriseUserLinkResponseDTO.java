package com.credit.bladi.controller.dto.enterprise.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(name = "EnterpriseUserLinkResponseDTO",
    description = "A link between a CLIENT account and the Enterprise portfolio it can access. Resolve "
        + "enterpriseId/userId against GET /enterprises/{id} / GET /users/{id} for display names.")
public record EnterpriseUserLinkResponseDTO(
    @Schema(description = "Link id.", example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b") UUID id,
    @Schema(description = "Linked Enterprise public id.", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    UUID enterpriseId,
    @Schema(description = "Linked user public id.", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") UUID userId,
    @Schema(description = "RESPONSABLE or DELEGUE.", example = "RESPONSABLE") String role,
    @Schema(description = "PENDING, APPROVED, REJECTED or REVOKED.", example = "APPROVED") String status,
    @Schema(description = "MANAGER_INVITE or CLIENT_CLAIM.", example = "MANAGER_INVITE") String origin,
    @Schema(description = "Username that created this link (the inviting Manager, or the claiming client).",
        example = "manager1") String requestedByUsername,
    @Schema(description = "Manager who approved/rejected this link (null while PENDING).",
        example = "manager1") String decidedByUsername,
    @Schema(description = "When the link was approved/rejected (null while PENDING).") Instant decidedAt,
    @Schema(description = "When this link request was created.") Instant createdAt,
    @Schema(description = "Username of the linked CLIENT account.", example = "jdupont") String username
) {}
