package com.credit.bladi.controller.dto.investment.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.UUID;

@Schema(
    name = "InvestmentCreditParametersResponseDTO",
    description = "Configurable eligibility/pricing parameters for a PME or GE credit."
)
public record InvestmentCreditParametersResponseDTO(
    @Schema(description = "Record identifier.", example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b") UUID id,
    @Schema(description = "Enterprise category this record applies to.", example = "PME") String enterpriseType,
    @Schema(description = "Minimum interest rate (%).", example = "6.00") BigDecimal rateMin,
    @Schema(description = "Maximum interest rate (%).", example = "7.50") BigDecimal rateMax,
    @Schema(description = "Rate calculation formula applied to rateMin/rateMax for this enterprise type.",
        example = "AVERAGE") String rateFormula,
    @Schema(description = "Minimum company age in years to be eligible.", example = "2") Integer minCompanyAgeYears,
    @Schema(description = "Maximum ratio of requested amount to annual revenue.", example = "0.5000")
    BigDecimal maxFinancingToRevenueRatio,
    @Schema(description = "Minimum ratio of guarantees value to requested amount.", example = "0.8000")
    BigDecimal minGuaranteeCoverageRatio,
    @Schema(description = "Maximum ratio of total annual debt service to net annual result.", example = "0.4000")
    BigDecimal maxDebtServiceRatio,
    @Schema(description = "Bank processing fees ratio applied to the requested amount.", example = "0.0100")
    BigDecimal processingFeesRatio,
    @Schema(description = "Monthly insurance premium ratio (ADI/dommages) applied to the requested amount: "
        + "flat over the life of the credit, folded into allInMonthlyPayment/TAEG.", example = "0.0010")
    BigDecimal insurancePremiumRatio,
    @Schema(description = "Whether this record is the active configuration for its enterprise type.",
        example = "true") Boolean active
) {}
