package com.credit.bladi.controller.dto.servicing.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Schema(name = "EarlyRepaymentResponseDTO", description = "A recorded remboursement anticipé.")
public record EarlyRepaymentResponseDTO(
    @Schema(description = "Record identifier.", example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b") UUID id,
    @Schema(description = "True for a total repayment (credit file closed).") boolean fullRepayment,
    @Schema(description = "Outstanding capital before (MAD).") BigDecimal outstandingBefore,
    @Schema(description = "Capital repaid (MAD).") BigDecimal repaidPrincipal,
    @Schema(description = "Accrued interest paid (MAD).") BigDecimal accruedInterest,
    @Schema(description = "Indemnity paid (MAD).") BigDecimal indemnity,
    @Schema(description = "Total paid (MAD).") BigDecimal totalPaid,
    @Schema(description = "Outstanding capital after (MAD).") BigDecimal outstandingAfter,
    @Schema(description = "Manager who recorded it.", example = "sara.bennani") String performedBy,
    @Schema(description = "When it was recorded.") Instant performedAt
) {}
