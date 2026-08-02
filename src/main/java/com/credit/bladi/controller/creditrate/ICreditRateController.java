package com.credit.bladi.controller.creditrate;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.controller.dto.creditrate.request.PatchCreditRateDTO;
import com.credit.bladi.controller.dto.creditrate.response.CreditRateResponseDTO;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Credit Rate Management", description = "Back-office APIs for updating credit interest rates")
public interface ICreditRateController {

    @Operation(
        summary = "Returns all credit rate records (paginated)",
        description = "Allows the bank administrator to browse all credit rate records in the back-office."
    )

    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Paginated list of credit rate records",
            content = @Content(schema = @Schema(implementation = CreditPage.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid pagination parameters",
            content = @Content(
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "timestamp": "2026-07-19 10:00:00",
                      "errorCode": "CB-400",
                      "description": "INVALID_PAGE: page must be greater or equal to 1."
                    }
                    """))),
        @ApiResponse(responseCode = "401", description = "Not authenticated",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions — MANAGER role required",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "timestamp": "2026-07-19 10:00:00",
                      "errorCode": "CB-500",
                      "description": "Internal server error, please try again later."
                    }
                    """)))
    })
    ResponseEntity<CreditPage<CreditRateResponseDTO>> getAllCreditRates(

        @Parameter(description = "Page number (1-based)", name = "page", example = "1")
        @Min(value = 1, message = "INVALID_PAGE: page must be greater or equal to 1.")
        @RequestParam(name = "page", defaultValue = "1", required = false) Integer page,

        @Parameter(description = "Page size (max 50)", name = "size", example = "20")
        @Min(value = 1, message = "INVALID_SIZE: size must be between 1 and 50.")
        @Max(value = 50, message = "INVALID_SIZE: size must be between 1 and 50.")
        @RequestParam(name = "size", defaultValue = "20", required = false) Integer size
    );

    @Operation(
        summary = "Get a single credit rate record by id",
        description = "Requires a valid MANAGER JWT token."
    )

    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Credit rate record found",
            content = @Content(schema = @Schema(implementation = CreditRateResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid id",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Not authenticated",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions — MANAGER role required",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "404",
            description = "No credit rate record found for the given ID",
            content = @Content(
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "timestamp": "2026-07-19 10:00:00",
                      "errorCode": "CB-404",
                      "description": "Credit rate record not found for the given ID."
                    }
                    """))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "timestamp": "2026-07-19 10:00:00",
                      "errorCode": "CB-500",
                      "description": "Internal server error, please try again later."
                    }
                    """)))
    })
    ResponseEntity<CreditRateResponseDTO> getById(
        @Parameter(description = "Identifier of the credit rate record.", example = "1")
        @Min(value = 1, message = "INVALID_ID: id must be a positive number.")
        @PathVariable Long id
    );

    @Operation(
        summary = "Partially update a credit rate",
        description = """
            Allows the bank administrator to partially update a credit rate record.
            Only fields present in the request body are updated; omitted fields remain unchanged.
            The change is reflected immediately in every subsequent simulation for the record's
            CSP code — no application restart is required, the cached rate is invalidated as
            part of this call.
            """
    )

    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Credit rate updated successfully"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid field value (e.g. rateMin greater than rateMax, or only one of "
                + "minDurationMonths/maxDurationMonths provided)",
            content = @Content(
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "timestamp": "2026-07-19 10:00:00",
                      "errorCode": "CB-400",
                      "description": "minDurationMonths and maxDurationMonths must be provided together."
                    }
                    """))),
        @ApiResponse(
            responseCode = "404",
            description = "No credit rate record found for the given ID",
            content = @Content(
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "timestamp": "2026-07-19 10:00:00",
                      "errorCode": "CB-404",
                      "description": "Credit rate record not found for the given ID."
                    }
                    """))),
        @ApiResponse(responseCode = "401", description = "Not authenticated",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions — MANAGER role required",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "409",
            description = "The record was modified concurrently by another request (optimistic lock conflict)",
            content = @Content(
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "timestamp": "2026-07-19 10:00:00",
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
                      "timestamp": "2026-07-19 10:00:00",
                      "errorCode": "CB-500",
                      "description": "Internal server error, please try again later."
                    }
                    """)))
    })
    ResponseEntity<Void> patchCreditRate(
        @Parameter(description = "Identifier of the credit rate record to update.", example = "1")
        @Min(value = 1, message = "INVALID_ID: id must be a positive number.")
        @PathVariable Long id,
        @Valid @RequestBody PatchCreditRateDTO dto
    );
}
