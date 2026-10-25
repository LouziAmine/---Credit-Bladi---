package com.credit.bladi.controller.verification;

import com.credit.bladi.controller.dto.verification.request.BamVerificationRequestDTO;
import com.credit.bladi.controller.dto.verification.response.BamVerificationResponseDTO;
import com.credit.bladi.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "BAM Verification (Mock)", description = "SIMULATED SCIP (cheque payment incidents) + Centrale "
    + "des Risques (outstanding credit exposure) checks: every response is mock data (source=MOCK_SIMULATION), "
    + "for MVP tests/demos of the classification step only. Never a real Bank Al-Maghrib / Credit Bureau "
    + "integration.")
public interface IBamVerificationController {
    @Operation(
        summary = "Run a simulated BAM SCIP + Centrale des Risques check",
        description = "Weighted-random outcome by enterpriseType (see docs/enterprise-credit-workflow.md). "
            + "Pass `scenario` to force a specific outcome instead, e.g. for a manager demo."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Simulated verification result",
            content = @Content(schema = @Schema(implementation = BamVerificationResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid enterpriseType/identifier/companyName/scenario",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<BamVerificationResponseDTO> verify(
        @Valid @RequestBody BamVerificationRequestDTO requestDTO,

        @Parameter(description = "Force a specific outcome instead of the weighted random draw: for demos. "
            + "One of: CLEAR, MINOR_INCIDENT, ACTIVE_INCIDENT, PERSONAL_EXPOSURE_DETECTED, MULTIPLE_EXPOSURES, "
            + "CONSOLIDATED_GROUP_EXPOSURE, REQUIRES_MANUAL_REVIEW.", example = "ACTIVE_INCIDENT")
        @RequestParam(required = false) String scenario
    );
}
