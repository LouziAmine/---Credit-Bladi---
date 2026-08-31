package com.credit.bladi.controller.investment;

import com.credit.bladi.controller.dto.investment.request.PatchInvestmentCreditParametersDTO;
import com.credit.bladi.controller.dto.investment.response.InvestmentCreditParametersResponseDTO;
import com.credit.bladi.exception.ErrorResponse;
import com.credit.bladi.validator.ValidationConstraints;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Enterprise Credit Parameters Management",
    description = "Back-office APIs for updating PME/GE credit eligibility and pricing parameters")
public interface IInvestmentCreditParametersController {
    @Operation(
        summary = "Get a single enterprise credit parameters record by id",
        description = "Requires a valid MANAGER JWT token."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Record found",
            content = @Content(schema = @Schema(implementation = InvestmentCreditParametersResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Not authenticated",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions: MANAGER role required",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "503",
            description = "No parameters record found for the given ID",
            content = @Content(
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "timestamp": "2026-08-19 10:00:00",
                      "errorCode": "CB-503",
                      "description": "Enterprise credit parameters are not configured for this enterprise type."
                    }
                    """)))
    })
    ResponseEntity<InvestmentCreditParametersResponseDTO> getById(
        @Parameter(description = "Identifier of the record.", example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b")
        @PathVariable UUID id
    );

    @Operation(
        summary = "Get the active enterprise credit parameters record for a given enterprise type",
        description = "Requires a valid MANAGER JWT token."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Active record found",
            content = @Content(schema = @Schema(implementation = InvestmentCreditParametersResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Not authenticated",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions: MANAGER role required",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "503", description = "No active record found for this enterprise type",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<InvestmentCreditParametersResponseDTO> getActiveByEnterpriseType(
        @Parameter(description = "Enterprise category. Must be one of: TPE, AUTO_ENTREPRENEUR, PME, ETI, GE.",
            example = "PME")
        @Pattern(regexp = ValidationConstraints.ENTERPRISE_TYPE_REGEX,
            message = "{validation.investmentCredit.enterpriseType.pattern}")
        @PathVariable String enterpriseType
    );

    @Operation(
        summary = "Partially update an enterprise credit parameters record",
        description = "Only fields present in the request body are updated; omitted fields remain "
            + "unchanged. The change is reflected immediately in every subsequent enterprise credit "
            + "simulation for the record's enterprise type: the cached parameters are invalidated as "
            + "part of this call."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Record updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid field value (e.g. rateMin greater than rateMax)",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Not authenticated",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions: MANAGER role required",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "503", description = "No record found for the given ID",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "409",
            description = "The record was modified concurrently by another request (optimistic lock conflict)",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<Void> patchParameters(
        @Parameter(description = "Identifier of the record to update.",
            example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b")
        @PathVariable UUID id,
        @Valid @RequestBody PatchInvestmentCreditParametersDTO dto
    );
}
