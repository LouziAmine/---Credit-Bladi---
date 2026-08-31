package com.credit.bladi.controller.dto.verification.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(
    name = "BamVerificationResponseDTO",
    description = "Result of a SIMULATED SCIP + Centrale des Risques check. `source`/`disclaimer` are always "
        + "present and always say the same thing: this is mock data for MVP tests/demos, never a real BAM "
        + "response: never use it for an actual credit decision."
)
public record BamVerificationResponseDTO(
    @Schema(description = "Always MOCK_SIMULATION: never a real BAM/Credit Bureau response.",
        example = "MOCK_SIMULATION") String source,
    @Schema(description = "Human-readable reminder that this is simulated data, not a real regulatory check.")
        String disclaimer,
    @Schema(example = "001234567000089") String verifiedIdentifier,
    @Schema(description = "ICE | CIN | ICE+CIN", example = "ICE") String identifierType,
    ScipResultResponseDTO scipResult,
    CentraleRisquesResultResponseDTO centraleRisquesResult,
    @Schema(description = "FAVORABLE | TO_REVIEW | UNFAVORABLE", example = "FAVORABLE")
        String simulatedRecommendation,
    Instant timestamp
) {
}
