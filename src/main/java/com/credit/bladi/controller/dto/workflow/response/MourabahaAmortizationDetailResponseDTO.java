package com.credit.bladi.controller.dto.workflow.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(
    name = "MourabahaAmortizationDetailResponseDTO",
    description = "Mortgage-Mourabaha (participative, interest-free) amortization figures: profit margin "
        + "and VAT on margin. Present only when the demand this amortization schedule belongs to is a "
        + "MORTGAGE demand with productType MOURABAHA. All monetary values are in MAD."
)
public record MourabahaAmortizationDetailResponseDTO(
    @Schema(description = "Profit margin excluding VAT over the financing duration (MAD).",
        example = "33534.55") BigDecimal marginHt,
    @Schema(description = "VAT applied to the profit margin (MAD).", example = "3353.45")
    BigDecimal vatOnMargin,
    @Schema(description = "Profit margin including VAT (MAD).", example = "36888.00")
    BigDecimal marginTtc,
    @Schema(description = "Flat monthly installment including VAT (MAD).", example = "4614.80")
    BigDecimal monthlyPaymentTtc
) {}
