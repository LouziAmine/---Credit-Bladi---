package com.credit.bladi.controller.investment;

import com.credit.bladi.controller.dto.investment.request.InvestmentCreditRequestDTO;
import com.credit.bladi.controller.dto.investment.response.InvestmentCreditResponseDTO;
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

@Tag(name = "Enterprise Credit Simulation", description = "Business investment credit simulation APIs "
    + "for Petite Entreprise (PME) and Grande Entreprise (GE) applicants")
public interface IInvestmentCreditController {
    @Operation(
        summary = "Simulate a business investment credit",
        description = """
            Calculates the monthly payment, fees and TAEG for a PME/GE investment financing based on:
            - Enterprise category: PME | GE
            - Requested amount and duration
            - Company annual revenue, age, net annual result, existing debt service and guarantees offered

            On success, a Demand is created and tracked through the bank's Workflow/Process/Step
            processing pipeline (Initiation, Instruction, Risk Analysis, Decision, Guarantees); the
            response includes its reference.
            """
    )

    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Simulation calculated successfully",
            content = @Content(schema = @Schema(implementation = InvestmentCreditResponseDTO.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request data (validation failure on a field)",
            content = @Content(
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "timestamp": "2026-08-19 10:00:00",
                      "errorCode": "CB-400",
                      "description": "Amount must be between 50000 and 100000000"
                    }
                    """))),
        @ApiResponse(
            responseCode = "422",
            description = "Business eligibility rule not met (company age, financing/revenue ratio, "
                + "guarantee coverage, debt-service ratio)",
            content = @Content(
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "timestamp": "2026-08-19 10:00:00",
                      "errorCode": "CB-422",
                      "description": "Company age is below the minimum required 2 years for this enterprise type."
                    }
                    """))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "timestamp": "2026-08-19 10:00:00",
                      "errorCode": "CB-500",
                      "description": "Internal server error, please try again later."
                    }
                    """)))
    })
    ResponseEntity<InvestmentCreditResponseDTO> simulate(@Valid @RequestBody InvestmentCreditRequestDTO requestDTO);
}
