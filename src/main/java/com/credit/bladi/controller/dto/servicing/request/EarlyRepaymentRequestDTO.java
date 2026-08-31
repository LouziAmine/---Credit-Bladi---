package com.credit.bladi.controller.dto.servicing.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;

@Schema(name = "EarlyRepaymentRequestDTO", description = "Remboursement anticipé actually received by the bank.")
public record EarlyRepaymentRequestDTO(
    @Schema(description = "Capital repaid (MAD). Omit to repay the whole outstanding capital (total repayment).",
        example = "100000.00")
    @DecimalMin(value = "0.01", message = "{validation.earlyRepayment.amount.min}")
    BigDecimal amount
) {}
