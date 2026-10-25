package com.credit.bladi.controller.dto.servicing.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Schema(name = "InstallmentResponseDTO", description = "One installment of a credit file's "
    + "repayment schedule.")
public record InstallmentResponseDTO(
    @Schema(description = "Record identifier.", example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b") UUID id,
    @Schema(description = "Installment number (1-based).", example = "1") Integer sequenceNumber,
    @Schema(description = "Due date.") LocalDate dueDate,
    @Schema(description = "Amount due (MAD).", example = "22186.45") BigDecimal amount,
    @Schema(description = "PENDING, DECLARED (client says they paid, awaiting manager validation), "
        + "PAID (validated), LATE (overdue, unpaid) or REPAID_EARLY (settled by a total early repayment).",
        example = "PENDING") String status,
    @Schema(description = "When the payment was validated by the manager (null until then).") Instant paidAt,
    @Schema(description = "When the client declared having paid (null while PENDING/LATE).") Instant declaredAt,
    @Schema(description = "Username of the client who declared the payment (null while PENDING/LATE).")
    String declaredBy,
    @Schema(description = "RIB the client says they paid from: check against the bank statement before "
        + "validating (null while PENDING/LATE).", example = "230780012345678901234503")
    String declaredSourceRib,
    @Schema(description = "Late interest (intérêts de retard): accrued to date on an unpaid late installment, "
        + "or charged when a late payment was validated. 0 when paid on time.", example = "38.40")
    BigDecimal latePenalty,
    @Schema(description = "What the client must transfer for this installment: amount + late interest.",
        example = "22224.85") BigDecimal totalDue
) {}
