package com.credit.bladi.controller.dto.servicing.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Schema(name = "EarlyRepaymentQuoteResponseDTO", description = "Cost of a remboursement anticipé made today.")
public record EarlyRepaymentQuoteResponseDTO(
    @Schema(description = "True when the whole outstanding capital is repaid (the credit file is then closed).")
    boolean fullRepayment,
    @Schema(description = "Date the quote is computed for.") LocalDate valueDate,
    @Schema(description = "Outstanding capital before the repayment (MAD).", example = "412350.18")
    BigDecimal outstandingBefore,
    @Schema(description = "Capital repaid (MAD).", example = "100000.00") BigDecimal repaidPrincipal,
    @Schema(description = "Interest accrued on the repaid capital since the start of the current period (MAD).",
        example = "215.30") BigDecimal accruedInterest,
    @Schema(description = "Indemnity, capped by loi 31-08 at one month of interest on the repaid capital and "
        + "3% of the outstanding capital (MAD).", example = "437.50") BigDecimal indemnity,
    @Schema(description = "Total the client pays: repaid capital + accrued interest + indemnity (MAD).",
        example = "100652.80") BigDecimal totalToPay,
    @Schema(description = "Outstanding capital after the repayment (MAD).", example = "312350.18")
    BigDecimal outstandingAfter,
    @Schema(description = "New monthly installment after a partial repayment (null for a total one).",
        example = "2455.10") BigDecimal newMonthlyPayment,
    @Schema(description = "Remaining installments with their new amounts (empty for a total repayment).")
    List<RescheduledInstallmentResponseDTO> newSchedule
) {
    @Schema(name = "RescheduledInstallmentResponseDTO", description = "A remaining installment after rescheduling.")
    public record RescheduledInstallmentResponseDTO(
        @Schema(description = "Installment number.", example = "13") Integer sequenceNumber,
        @Schema(description = "Due date.") LocalDate dueDate,
        @Schema(description = "New amount (MAD).", example = "2455.10") BigDecimal amount
    ) {}
}
