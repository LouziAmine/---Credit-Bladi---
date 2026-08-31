package com.credit.bladi.controller.dto.audit.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(
    name = "AuditLogResponseDTO",
    description = "A single audit trail entry: tracks who did what, when, and from where."
)
public record AuditLogResponseDTO(
    @Schema(description = "Entry identifier.", example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b") UUID id,
    @Schema(description = "Username of the actor, or null for unauthenticated actions (e.g. a public "
        + "simulation).", example = "jdoe") String username,
    @Schema(description = "Action performed.", example = "SIMULATION_REJECTED") String action,
    @Schema(description = "IP address the action was performed from.", example = "10.0.0.1") String ipAddress,
    @Schema(description = "Free-text details about the action (e.g. rejection reason, updated resource id).",
        example = "reason=DEBT_RATIO_EXCEEDED, cspCode=FONC, amount=500000.00 MAD, duration=240") String details,
    @Schema(description = "Timestamp the action was performed at.", example = "2026-07-08 10:00:00")
    LocalDateTime performedAt,
    @Schema(description = "Kind of business object the entry is about (DEMAND, ENTERPRISE, USER), or null when "
        + "the action is not tied to one.", example = "DEMAND") String entityType,
    @Schema(description = "Public id of that object.", example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b")
    UUID entityId
) {}
