package com.credit.bladi.controller.client;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.controller.dto.enterprise.request.ClaimEnterpriseLinkRequestDTO;
import com.credit.bladi.controller.dto.enterprise.response.ClientDemandSummaryResponseDTO;
import com.credit.bladi.controller.dto.enterprise.response.ClientPortfolioEnterpriseResponseDTO;
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
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Client portfolio", description = "Self-service portal for a CLIENT to claim access to an "
    + "Enterprise's portfolio and, once a Manager approves it, see that Enterprise's own credit demands. "
    + "Read-only and scoped to the caller's own APPROVED links only: see "
    + "docs/enterprise-credit-workflow.md \"Enterprise client portal access\".")
public interface IClientPortfolioController {
    @Operation(summary = "Claim portal access to an Enterprise by its exact registration number",
        description = "Starts PENDING; a Manager must approve it before any portfolio data is visible.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Claim created (PENDING)",
            content = @Content(schema = @Schema(implementation = EnterpriseUserLinkResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "No Enterprise found for this registration number",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "An active link already exists for this pair",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<EnterpriseUserLinkResponseDTO> claimEnterprise(
        @Valid @RequestBody ClaimEnterpriseLinkRequestDTO requestDTO
    );

    @Operation(summary = "List my own enterprise-link requests, every status")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "My link requests")
    })
    ResponseEntity<List<EnterpriseUserLinkResponseDTO>> getMyEnterpriseLinks();

    @Operation(summary = "List the Enterprises I have APPROVED portfolio access to")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "My portfolio")
    })
    ResponseEntity<List<ClientPortfolioEnterpriseResponseDTO>> getMyPortfolio();

    @Operation(summary = "List one Enterprise's demands (trimmed, client-safe view)",
        description = "403 unless the caller has an APPROVED link to this Enterprise.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Paginated, trimmed demand list",
            content = @Content(schema = @Schema(implementation = CreditPage.class))),
        @ApiResponse(responseCode = "403", description = "No approved link to this Enterprise",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<CreditPage<ClientDemandSummaryResponseDTO>> getPortfolioEnterpriseDemands(
        @Parameter(description = "Identifier of the Enterprise.", example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b")
        @PathVariable UUID enterpriseId,
        @Parameter(description = "Page number (1-based)", name = "page", example = "1")
        @Min(value = 1, message = "{validation.invalidPage}")
        @RequestParam(name = "page", defaultValue = "1", required = false) Integer page,
        @Parameter(description = "Page size", name = "size", example = "20")
        @Min(value = 1, message = "{validation.invalidSize.min}")
        @RequestParam(name = "size", defaultValue = "20", required = false) Integer size
    );

    @Operation(summary = "List the caller's own mortgage dossiers (trimmed, client-safe view)",
        description = "A mortgage borrower has no Enterprise: access is granted per dossier, when the dossier is "
            + "classified REAL (single client account matching the applicant's e-mail) or by accepting the e-mailed "
            + "invitation.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "The caller's mortgage dossiers, most recently linked first")
    })
    ResponseEntity<List<ClientDemandSummaryResponseDTO>> getMyMortgageDemands();
}
