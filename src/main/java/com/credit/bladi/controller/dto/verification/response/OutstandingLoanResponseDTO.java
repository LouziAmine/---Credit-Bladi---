package com.credit.bladi.controller.dto.verification.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(name = "OutstandingLoanResponseDTO",
    description = "One simulated Centrale des Risques outstanding-exposure line.")
public record OutstandingLoanResponseDTO(
    @Schema(example = "Atlas Al Maghrib Bank (fictional)") String institution,
    @Schema(example = "Investment credit") String creditType,
    @Schema(example = "1500000") BigDecimal amount,
    @Schema(description = "CURRENT | OVERDUE | IN_LITIGATION", example = "CURRENT") String repaymentStatus
) {
}
