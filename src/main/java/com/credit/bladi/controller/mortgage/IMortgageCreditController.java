package com.credit.bladi.controller.mortgage;

import com.credit.bladi.controller.dto.mortgage.request.MortgageCreditRequestDTO;
import com.credit.bladi.controller.dto.mortgage.response.MortgageCreditResponseDTO;
import com.credit.bladi.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Credit Simulation", description = "Mortgage credit simulation APIs")
public interface IMortgageCreditController {
    @Operation(
        summary = "Simulate a mortgage credit",
        description = """
            Calculates the monthly payment and fees for a mortgage credit based on:
            - Credit amount (integer, 70,000 – 30,000,000)
            - Repayment duration in months (12 – 300)
            - Client CSP profile: RETR | SALA | FONC | PROF | INDE
            - Nationality code ISO 3166-1 alpha-2 (e.g. MA, FR)
            - Moroccan residency (strict boolean)
            - Financing product: CONVENTIONAL (interest-based, default) | MOURABAHA
              (participative: profit margin + VAT on margin, no interest)
            """
    )

    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Simulation calculated successfully",
            content = @Content(
                schema = @Schema(implementation = MortgageCreditResponseDTO.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request data (validation failure on a field)",
            content = @Content(
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "timestamp": "2026-06-17 10:00:00",
                      "errorCode": "CB-400",
                      "description": "Amount must be between 70000 and 30000000"
                    }
                    """))),
        @ApiResponse(
            responseCode = "404",
            description = "CSP profile not supported or inactive",
            content = @Content(
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "timestamp": "2026-06-17 10:00:00",
                      "errorCode": "CB-404",
                      "description": "CSP not supported or inactive."
                    }
                    """))),
        @ApiResponse(
            responseCode = "422",
            description = "BAM business rule not met (duration, debt ratio, deposit, retirement age)",
            content = @Content(
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "timestamp": "2026-06-17 10:00:00",
                      "errorCode": "CB-422",
                      "description": "Duration out of range for this CSP profile (BAM regulation)."
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
    ResponseEntity<MortgageCreditResponseDTO> simulate(@Valid @RequestBody MortgageCreditRequestDTO requestDTO);
}
