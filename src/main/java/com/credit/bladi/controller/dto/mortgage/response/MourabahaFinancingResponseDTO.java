package com.credit.bladi.controller.dto.mortgage.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(
    name = "MourabahaFinancingResponseDTO",
    description = "Mourabaha (participative, interest-free) mortgage detail: profit margin, VAT on margin, "
        + "and Takaful insurance figures. Present only when MortgageCreditResponseDTO.productType is "
        + "MOURABAHA. All monetary values are in MAD."
)
public record MourabahaFinancingResponseDTO(
    @Schema(description = "Profit margin excluding VAT over the financing duration (MAD).",
        example = "33534.55") BigDecimal marginHt,
    @Schema(description = "VAT applied to the profit margin (MAD).", example = "3353.45")
    BigDecimal vatOnMargin,
    @Schema(description = "Profit margin including VAT (MAD).", example = "36888.00")
    BigDecimal marginTtc,
    @Schema(description = "Flat monthly installment including VAT (MAD).", example = "4614.80")
    BigDecimal monthlyPaymentTtc,
    @Schema(description = "Takaful (Islamic cooperative insurance) monthly contribution, doubled if there's "
        + "a co-borrower (MAD).", example = "200.00") BigDecimal takafulMonthlyContribution,
    @Schema(description = "All-in monthly payment (monthlyPaymentTtc + Takaful contribution): the figure "
        + "checked against the BAM debt-ratio rule and folded into the TAEG (MAD).", example = "4814.80")
    BigDecimal allInMonthlyPaymentTtc,
    @Schema(description = "Total Takaful cost over the financing duration (MAD).", example = "48000.00")
    BigDecimal totalTakafulCost,
    @Schema(description = "Total repayment amount including VAT (principal + margin TTC, excluding closing "
        + "fees and Takaful insurance) (MAD).", example = "276888.00") BigDecimal totalCostTtc
) {}
