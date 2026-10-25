package com.credit.bladi.controller.dto.mortgage.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(
    name = "ConventionalFinancingResponseDTO",
    description = "Conventional (interest-based) mortgage detail: rate range and payment/cost figures both "
        + "at the applicable rate and at the CSP profile's min/max rate bounds. Present only when "
        + "MortgageCreditResponseDTO.productType is CONVENTIONAL. All monetary values are in MAD."
)
public record ConventionalFinancingResponseDTO(
    @Schema(description = "Minimum applicable annual interest rate.", example = "3.50") BigDecimal rateMin,
    @Schema(description = "Maximum applicable annual interest rate.", example = "5.50") BigDecimal rateMax,
    @Schema(description = "Estimated monthly payment at the rate actually applicable to this borrower "
        + "(applicableRate), excluding ADI insurance (MAD).", example = "3244.79") BigDecimal monthlyPayment,
    @Schema(description = "ADI (death/disability insurance) monthly premium at the rate actually applicable "
        + "to this borrower, mandatory BAM regulation (MAD).", example = "200.00") BigDecimal adiMonthlyPremium,
    @Schema(description = "All-in monthly payment (monthlyPayment + ADI premium) at the rate actually "
        + "applicable to this borrower: the figure checked against the BAM debt-ratio rule (MAD).",
        example = "3444.79") BigDecimal allInMonthlyPayment,
    @Schema(description = "Total interest over the loan duration at the rate actually applicable to this "
        + "borrower (MAD).", example = "278749.60") BigDecimal totalInterest,
    @Schema(description = "Total ADI insurance cost over the loan duration at the rate actually applicable "
        + "to this borrower (MAD).", example = "48000.00") BigDecimal totalAdiCost,
    @Schema(description = "Total repayment amount (principal + interest, excluding closing fees and ADI "
        + "insurance) at the rate actually applicable to this borrower (MAD).", example = "778749.60")
    BigDecimal totalCost,
    @Schema(description = "Minimum estimated monthly payment, excluding ADI insurance (MAD).", example = "2800.00")
    BigDecimal monthlyPaymentMin,
    @Schema(description = "Maximum estimated monthly payment, excluding ADI insurance (MAD).", example = "3200.00")
    BigDecimal monthlyPaymentMax,
    @Schema(description = "Minimum ADI (death/disability insurance) monthly premium, mandatory BAM regulation (MAD).",
        example = "200.00") BigDecimal adiMonthlyPremiumMin,
    @Schema(description = "Maximum ADI (death/disability insurance) monthly premium, mandatory BAM regulation (MAD).",
        example = "232.00") BigDecimal adiMonthlyPremiumMax,
    @Schema(description = "Minimum all-in monthly payment (monthlyPayment + ADI premium): the figure checked "
        + "against the BAM debt-ratio rule (MAD).", example = "3000.00") BigDecimal allInMonthlyPaymentMin,
    @Schema(description = "Maximum all-in monthly payment (monthlyPayment + ADI premium): the figure checked "
        + "against the BAM debt-ratio rule (MAD).", example = "3432.00") BigDecimal allInMonthlyPaymentMax,
    @Schema(description = "Minimum total interest over the loan duration (MAD).", example = "72000.00")
    BigDecimal totalInterestMin,
    @Schema(description = "Maximum total interest over the loan duration (MAD).", example = "168000.00")
    BigDecimal totalInterestMax,
    @Schema(description = "Minimum total ADI insurance cost over the loan duration (MAD).", example = "48000.00")
    BigDecimal totalAdiCostMin,
    @Schema(description = "Maximum total ADI insurance cost over the loan duration (MAD).", example = "55680.00")
    BigDecimal totalAdiCostMax,
    @Schema(description = "Minimum total repayment amount (principal + interest, excluding closing fees and ADI "
        + "insurance). See totalAdiCostMin/totalFees for those.", example = "572000.00") BigDecimal totalCostMin,
    @Schema(description = "Maximum total repayment amount (principal + interest, excluding closing fees and ADI "
        + "insurance). See totalAdiCostMax/totalFees for those.", example = "668000.00") BigDecimal totalCostMax
) {}
