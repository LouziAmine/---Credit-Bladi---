package com.credit.bladi.controller.bam;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.controller.dto.bam.request.PatchBamParametersDTO;
import com.credit.bladi.controller.dto.bam.response.BamParametersResponseDTO;
import com.credit.bladi.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "BAM Parameters Management", description = "Back-office APIs for updating BAM regulatory parameters")
public interface IBamParametersController {
    @Operation(
        summary = "Returns all BAM parameters records (paginated)",
        description = "Allows the bank administrator to browse all BAM regulatory fee "
            + "parameter records in the back-office."
    )

    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Paginated list of BAM parameters records",
            content = @Content(schema = @Schema(implementation = CreditPage.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid pagination parameters",
            content = @Content(
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "timestamp": "2026-06-17 10:00:00",
                      "errorCode": "CB-400",
                      "description": "INVALID_PAGE: page must be greater or equal to 1."
                    }
                    """))),
        @ApiResponse(responseCode = "401", description = "Not authenticated",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions: MANAGER role required",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "timestamp": "2026-06-17 10:00:00",
                      "errorCode": "CB-500",
                      "description": "Internal server error, please try again later."
                    }
                    """)))
    })
    ResponseEntity<CreditPage<BamParametersResponseDTO>> getAllParameters(

        @Parameter(description = "Page number (1-based)", name = "page", example = "1")
        @Min(value = 1, message = "{validation.invalidPage}")
        @RequestParam(name = "page", defaultValue = "1", required = false) Integer page,

        @Parameter(description = "Page size (max 50)", name = "size", example = "20")
        @Min(value = 1, message = "{validation.invalidSize}")
        @Max(value = 50, message = "{validation.invalidSize}")
        @RequestParam(name = "size", defaultValue = "20", required = false) Integer size
    );

    @Operation(
        summary = "Get a single BAM parameters record by id",
        description = "Requires a valid MANAGER JWT token."
    )

    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "BAM parameters record found",
            content = @Content(schema = @Schema(implementation = BamParametersResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid id",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Not authenticated",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions: MANAGER role required",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "404",
            description = "No BAM parameters record found for the given ID",
            content = @Content(
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "timestamp": "2026-06-17 10:00:00",
                      "errorCode": "CB-404",
                      "description": "BAM parameters record not found for the given ID."
                    }
                    """))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "timestamp": "2026-06-17 10:00:00",
                      "errorCode": "CB-500",
                      "description": "Internal server error, please try again later."
                    }
                    """)))
    })
    ResponseEntity<BamParametersResponseDTO> getById(
        @Parameter(description = "Identifier of the BAM parameters record.",
            example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b")
        @PathVariable UUID id
    );

    @Operation(
        summary = "Partially update BAM parameters",
        description = """
            Allows the bank administrator to partially update BAM regulatory fee parameters.
            Only fields present in the request body are updated; omitted fields remain unchanged.
            """
    )

    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Parameters updated successfully"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid field value (e.g. negative ratio)",
            content = @Content(
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "timestamp": "2026-06-17 10:00:00",
                      "errorCode": "CB-400",
                      "description": "processingFeesRatio must be strictly positive"
                    }
                    """))),
        @ApiResponse(
            responseCode = "404",
            description = "No BAM parameters record found for the given ID",
            content = @Content(
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "timestamp": "2026-06-17 10:00:00",
                      "errorCode": "CB-404",
                      "description": "BAM parameters record not found for the given ID."
                    }
                    """))),
        @ApiResponse(responseCode = "401", description = "Not authenticated",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions: MANAGER role required",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "409",
            description = "The record was modified concurrently by another request (optimistic lock conflict)",
            content = @Content(
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "timestamp": "2026-06-17 10:00:00",
                      "errorCode": "CB-409",
                      "description": "This resource was modified concurrently. Please retry with the latest version."
                    }
                    """))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "timestamp": "2026-06-17 10:00:00",
                      "errorCode": "CB-500",
                      "description": "Internal server error, please try again later."
                    }
                    """)))
    })
    ResponseEntity<Void> patchParameters(
        @Parameter(description = "Identifier of the BAM parameters record to update.",
            example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b")
        @PathVariable UUID id,
        @Valid @RequestBody PatchBamParametersDTO dto
    );
}
