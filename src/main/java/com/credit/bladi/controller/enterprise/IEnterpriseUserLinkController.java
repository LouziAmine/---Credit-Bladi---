package com.credit.bladi.controller.enterprise;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.controller.dto.enterprise.request.InviteEnterpriseLinkRequestDTO;
import com.credit.bladi.controller.dto.enterprise.request.RejectEnterpriseLinkRequestDTO;
import com.credit.bladi.controller.dto.enterprise.response.EnterpriseUserLinkResponseDTO;
import com.credit.bladi.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Enterprise user links", description = "Manager review of CLIENT access requests to an "
    + "Enterprise's portfolio (invite an already-registered client, or approve/reject/revoke a client's "
    + "self-service claim). See docs/enterprise-credit-workflow.md \"Enterprise client portal access\".")
public interface IEnterpriseUserLinkController {
    @Operation(summary = "Invite an already-registered CLIENT to an Enterprise's portfolio",
        description = "The link is created APPROVED immediately: the Manager's own action is the required "
            + "human verification, no separate client-side acceptance step.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Link created (APPROVED)",
            content = @Content(schema = @Schema(implementation = EnterpriseUserLinkResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Enterprise or target user not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "An active link already exists for this pair",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "422", description = "The target user does not have the CLIENT role",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<EnterpriseUserLinkResponseDTO> inviteClient(
        @Parameter(description = "Identifier of the Enterprise.", example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b")
        @PathVariable UUID enterpriseId,
        @Valid @RequestBody InviteEnterpriseLinkRequestDTO requestDTO
    );

    @Operation(summary = "List every client access link for one Enterprise (paginated)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Paginated list of links",
            content = @Content(schema = @Schema(implementation = CreditPage.class)))
    })
    ResponseEntity<CreditPage<EnterpriseUserLinkResponseDTO>> getLinksForEnterprise(
        @Parameter(description = "Identifier of the Enterprise.", example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b")
        @PathVariable UUID enterpriseId,
        @Parameter(description = "Page number (1-based)", name = "page", example = "1")
        @Min(value = 1, message = "{validation.invalidPage}")
        @RequestParam(name = "page", defaultValue = "1", required = false) Integer page,
        @Parameter(description = "Page size", name = "size", example = "20")
        @Min(value = 1, message = "{validation.invalidSize.min}")
        @RequestParam(name = "size", defaultValue = "20", required = false) Integer size
    );

    @Operation(summary = "List PENDING client-claim requests across every Enterprise (review queue)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Paginated list of pending links",
            content = @Content(schema = @Schema(implementation = CreditPage.class)))
    })
    ResponseEntity<CreditPage<EnterpriseUserLinkResponseDTO>> getPendingLinks(
        @Parameter(description = "Page number (1-based)", name = "page", example = "1")
        @Min(value = 1, message = "{validation.invalidPage}")
        @RequestParam(name = "page", defaultValue = "1", required = false) Integer page,
        @Parameter(description = "Page size", name = "size", example = "20")
        @Min(value = 1, message = "{validation.invalidSize.min}")
        @RequestParam(name = "size", defaultValue = "20", required = false) Integer size
    );

    @Operation(summary = "Approve a PENDING client-claim link")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Link approved",
            content = @Content(schema = @Schema(implementation = EnterpriseUserLinkResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Link not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "Link is not PENDING",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<EnterpriseUserLinkResponseDTO> approveLink(
        @Parameter(description = "Identifier of the link.", example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b")
        @PathVariable UUID linkId
    );

    @Operation(summary = "Reject a PENDING client-claim link")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Link rejected",
            content = @Content(schema = @Schema(implementation = EnterpriseUserLinkResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Link not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "Link is not PENDING",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<EnterpriseUserLinkResponseDTO> rejectLink(
        @Parameter(description = "Identifier of the link.", example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b")
        @PathVariable UUID linkId,
        @Valid @RequestBody RejectEnterpriseLinkRequestDTO requestDTO
    );

    @Operation(summary = "Revoke an APPROVED link (e.g. an employee leaves the company)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Link revoked",
            content = @Content(schema = @Schema(implementation = EnterpriseUserLinkResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Link not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "Link is not APPROVED",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<EnterpriseUserLinkResponseDTO> revokeLink(
        @Parameter(description = "Identifier of the link.", example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b")
        @PathVariable UUID linkId
    );
}
