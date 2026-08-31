package com.credit.bladi.controller.dto.verification.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(name = "CentraleRisquesResultResponseDTO", description = "Simulated Centrale des Risques (outstanding "
    + "credit exposure) result.")
public record CentraleRisquesResultResponseDTO(
    @Schema(example = "CLEAR") String status,
    List<OutstandingLoanResponseDTO> outstandingLoans
) {
}
