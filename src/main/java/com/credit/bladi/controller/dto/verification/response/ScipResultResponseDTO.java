package com.credit.bladi.controller.dto.verification.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(name = "ScipResultResponseDTO", description = "Simulated SCIP (cheque payment incidents) result.")
public record ScipResultResponseDTO(
    @Schema(example = "CLEAR") String status,
    List<ChequeIncidentResponseDTO> incidents
) {
}
