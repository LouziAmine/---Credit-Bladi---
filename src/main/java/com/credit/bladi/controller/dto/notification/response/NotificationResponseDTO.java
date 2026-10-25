package com.credit.bladi.controller.dto.notification.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(name = "NotificationResponseDTO", description = "An in-app notification for the currently "
    + "authenticated user.")
public record NotificationResponseDTO(
    @Schema(description = "Notification identifier.", example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b") UUID id,
    @Schema(description = "Notification type, to pick an icon in the UI.", example = "DEMAND_ASSIGNED") String type,
    @Schema(description = "Short title.", example = "File assigned") String title,
    @Schema(description = "Notification body.",
        example = "File DEM-3F2A9C1B has been assigned to you by admin.") String body,
    @Schema(description = "In-app route this notification points to (null if none).",
        example = "/admin/demands/3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b") String link,
    @Schema(description = "Whether the recipient has read this notification.", example = "false") boolean read,
    @Schema(description = "When this notification was created.") Instant createdAt
) {}
