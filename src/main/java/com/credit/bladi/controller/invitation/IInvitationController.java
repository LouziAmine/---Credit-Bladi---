package com.credit.bladi.controller.invitation;

import com.credit.bladi.controller.dto.invitation.request.AcceptInvitationRequestDTO;
import com.credit.bladi.controller.dto.invitation.response.ClientInvitationPreviewResponseDTO;
import com.credit.bladi.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Client invitations", description = "Public, unauthenticated endpoints backing the "
    + "\"no existing CLIENT account\" half of auto-resolving portfolio access at demand classification: "
    + "see docs/enterprise-credit-workflow.md \"Enterprise client portal access\".")
public interface IInvitationController {
    @Operation(summary = "Preview a pending client invitation before setting a password")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "The invitation's email and target Enterprise",
            content = @Content(schema = @Schema(implementation = ClientInvitationPreviewResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "No pending invitation for this token",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "The invitation has expired",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<ClientInvitationPreviewResponseDTO> preview(
        @Parameter(description = "The raw invitation token from the emailed link.")
        @NotBlank(message = "{validation.token.notBlank}") @PathVariable String token
    );

    @Operation(summary = "Accept a client invitation: create the CLIENT account and its APPROVED "
        + "enterprise link in one step")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Account and link created"),
        @ApiResponse(responseCode = "404", description = "No pending invitation for this token",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "The invitation has expired, or the chosen "
            + "username is already taken",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<Void> accept(
        @Parameter(description = "The raw invitation token from the emailed link.")
        @NotBlank(message = "{validation.token.notBlank}") @PathVariable String token,
        @Valid @RequestBody AcceptInvitationRequestDTO requestDTO
    );
}
