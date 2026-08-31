package com.credit.bladi.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(
    name = "AmortizationRowDTO",
    description = """
        One row of the month-by-month amortization schedule at the \
        applicable rate/margin, for a constant monthly payment.

        Conventional loans populate `interest`; Mourabaha financings populate `marginHt` /
        `vatOnMargin` / `marginTtc` instead (the unused set is null, depending on productType).
        """
)
public record AmortizationRowDTO(
    @Schema(description = "Installment number (1-based).", example = "1") Integer installmentNumber,
    @Schema(description = "Remaining principal balance at the start of this installment (MAD).",
        example = "500000.00") BigDecimal beginningBalance,
    @Schema(description = "Conventional only: interest portion of this installment (MAD).",
        example = "2291.67") BigDecimal interest,
    @Schema(description = "Mourabaha only: profit margin excluding VAT for this installment (MAD).",
        example = "1050.00") BigDecimal marginHt,
    @Schema(description = "Mourabaha only: VAT on the margin for this installment (MAD).",
        example = "105.00") BigDecimal vatOnMargin,
    @Schema(description = "Mourabaha only: profit margin including VAT for this installment (MAD).",
        example = "1155.00") BigDecimal marginTtc,
    @Schema(description = "Principal portion of this installment (MAD).", example = "1147.77") BigDecimal principal,
    @Schema(description = "Total installment amount due (principal + interest/margin); may differ from the "
        + "nominal constant monthly payment by a few centimes on the final installment due to rounding "
        + "closure (MAD).", example = "3439.44") BigDecimal payment,
    @Schema(description = "Remaining principal balance after this installment (0.00 on the final "
        + "installment) (MAD).", example = "498852.23") BigDecimal endingBalance
) {}
