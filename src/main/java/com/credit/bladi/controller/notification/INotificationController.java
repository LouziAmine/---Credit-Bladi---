package com.credit.bladi.controller.notification;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.controller.dto.notification.response.NotificationResponseDTO;
import com.credit.bladi.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Tag(name = "Notifications", description = "Back-office in-app notification inbox for the "
    + "currently authenticated user")
public interface INotificationController {
    @Operation(
        summary = "List my notifications",
        description = "Most recent first. Scoped to the currently authenticated user: there is no way "
            + "to read another user's inbox through this endpoint."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Paginated list of notifications",
            content = @Content(schema = @Schema(implementation = CreditPage.class))),
        @ApiResponse(responseCode = "401", description = "Not authenticated",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<CreditPage<NotificationResponseDTO>> getMyNotifications(
        @Parameter(description = "Page number (1-based)", name = "page", example = "1")
        @Min(value = 1, message = "{validation.invalidPage}")
        @RequestParam(name = "page", defaultValue = "1", required = false) Integer page,

        @Parameter(description = "Page size (max 50)", name = "size", example = "20")
        @Min(value = 1, message = "{validation.invalidSize}")
        @Max(value = 50, message = "{validation.invalidSize}")
        @RequestParam(name = "size", defaultValue = "20", required = false) Integer size
    );

    @Operation(
        summary = "Count my unread notifications",
        description = "Meant to back a badge count in the header bell icon."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Unread count"),
        @ApiResponse(responseCode = "401", description = "Not authenticated",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<Long> getMyUnreadCount();

    @Operation(
        summary = "Mark one of my notifications as read",
        description = "No-op if it was already read. 404s if the notification doesn't exist or "
            + "doesn't belong to the caller."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Marked as read"),
        @ApiResponse(responseCode = "401", description = "Not authenticated",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Notification not found for the caller",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<Void> markAsRead(
        @Parameter(description = "Identifier of the notification.", example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b")
        @PathVariable UUID id
    );

    @Operation(
        summary = "Mark all of my notifications as read",
        description = "Bulk equivalent of markAsRead: used by a \"tout marquer comme lu\" action."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "All marked as read"),
        @ApiResponse(responseCode = "401", description = "Not authenticated",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<Void> markAllAsRead();

    @Operation(
        summary = "Open a live notification stream (Server-Sent Events)",
        description = "Push channel: the connection stays open and the server emits a `notification` "
            + "event every time NotificationService#notify(NotificationMessage) fires for the caller: "
            + "the frontend reacts by refreshing the unread count/list, it does not need to poll. A "
            + "`connected` event is sent immediately, and a comment ping every 15s keeps the connection "
            + "alive through proxies. Reconnects on its own (native EventSource behavior) if dropped."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "text/event-stream opened"),
        @ApiResponse(responseCode = "401", description = "Not authenticated",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    SseEmitter stream(@AuthenticationPrincipal UserDetails currentUser);
}
