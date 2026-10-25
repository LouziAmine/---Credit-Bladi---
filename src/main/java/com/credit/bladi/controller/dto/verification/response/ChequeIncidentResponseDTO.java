package com.credit.bladi.controller.dto.verification.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(name = "ChequeIncidentResponseDTO", description = "One simulated SCIP unpaid-cheque line.")
public record ChequeIncidentResponseDTO(
    @Schema(example = "unpaid_cheque") String type,
    LocalDate date,
    @Schema(example = "15000") BigDecimal amount,
    @Schema(description = "Whether the incident has since been resolved.") boolean resolved
) {
}
