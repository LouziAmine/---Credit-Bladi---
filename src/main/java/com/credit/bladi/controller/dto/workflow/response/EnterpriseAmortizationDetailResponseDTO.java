package com.credit.bladi.controller.dto.workflow.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(
    name = "EnterpriseAmortizationDetailResponseDTO",
    description = "Enterprise-investment-credit-specific figures. Present only when the demand this "
        + "amortization schedule belongs to is an ENTERPRISE demand. All monetary values are in MAD."
)
public record EnterpriseAmortizationDetailResponseDTO(
    @Schema(description = "Monthly payment at the applicable rate, excluding fees (MAD).", example = "22186.45")
    BigDecimal monthlyPayment,
    @Schema(description = "Annual debt service of the financing (monthlyPayment x 12, MAD).",
        example = "266237.40") BigDecimal annualDebtService,
    @Schema(description = "Total interest over the financing duration (MAD).", example = "364261.80")
    BigDecimal totalInterest,
    @Schema(description = "Total repayment amount (principal + interest) over the financing duration (MAD).",
        example = "1864261.80") BigDecimal totalCost
) {}
