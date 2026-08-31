package com.credit.bladi.controller.dto.investment.response;

import com.credit.bladi.controller.dto.AmortizationRowDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;

@Schema(
    name = "InvestmentCreditResponseDTO",
    description = "Business investment credit simulation result for a PME/GE applicant. All monetary "
        + "values are in MAD."
)
public record InvestmentCreditResponseDTO(
    @Schema(description = "Company legal name.", example = "Atlas Textile SARL") String companyName,
    @Schema(description = "Enterprise category that was simulated.", example = "PME") String enterpriseType,
    @Schema(description = "Economic sector.", example = "Textile") String sector,
    @Schema(description = "Requested financing amount (MAD).", example = "1500000.00") BigDecimal amount,
    @Schema(description = "Financing duration (months).", example = "84") Integer duration,
    @Schema(description = "Applicable annual interest rate for this enterprise type (%).", example = "6.75")
    BigDecimal applicableRate,
    @Schema(description = "Monthly payment at the applicable rate, excluding fees (MAD).", example = "22186.45")
    BigDecimal monthlyPayment,
    @Schema(description = "Annual debt service of the new financing (monthlyPayment x 12), used to check the "
        + "debt-service ratio (MAD).", example = "266237.40") BigDecimal annualDebtService,
    @Schema(description = "Total interest over the financing duration (MAD).", example = "364261.80")
    BigDecimal totalInterest,
    @Schema(description = "Total repayment amount (principal + interest) over the financing duration (MAD).",
        example = "1864261.80") BigDecimal totalCost,
    @Schema(description = "Bank processing fees (MAD).", example = "15000.00") BigDecimal processingFees,
    @Schema(description = "TAEG (Taux Annuel Effectif Global): effective annual cost of the credit, "
        + "including interest and processing fees. Expressed as a percentage.", example = "7.12") BigDecimal taeg,
    @Schema(description = "Reference of the workflow demand created to track this financing request through "
        + "the bank's processing pipeline (Initiation, Instruction, Risk Analysis, Decision, Guarantees).",
        example = "DEM-000123") String demandReference,
    @Schema(description = "Month-by-month amortization schedule at the applicable "
        + "rate.") List<AmortizationRowDTO> amortizationSchedule
) {}
