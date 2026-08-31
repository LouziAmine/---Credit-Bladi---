package com.credit.bladi.controller.dto.investment.request;

import com.credit.bladi.validator.ValidationConstraints;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;

@Schema(
    name = "PatchInvestmentCreditParametersDTO",
    description = "Request payload to update a PME/GE credit parameters record. Admin use only. Only "
        + "provided fields are updated (partial update). The record to update is identified by the "
        + "{id} path variable."
)
public record PatchInvestmentCreditParametersDTO(
    @Schema(description = "New minimum interest rate (%).", example = "6.00")
    @DecimalMin(value = "0.0", inclusive = false, message = "{validation.investmentCreditParameters.rateMin.min}")
    @DecimalMax(value = "100.0", message = "{validation.investmentCreditParameters.rateMin.max}")
    BigDecimal rateMin,

    @Schema(description = "New maximum interest rate (%).", example = "7.50")
    @DecimalMin(value = "0.0", inclusive = false, message = "{validation.investmentCreditParameters.rateMax.min}")
    @DecimalMax(value = "100.0", message = "{validation.investmentCreditParameters.rateMax.max}")
    BigDecimal rateMax,

    @Schema(description = "New rate calculation formula applied to rateMin/rateMax for this enterprise type.",
        example = "AVERAGE")
    @Pattern(regexp = ValidationConstraints.RATE_FORMULA_REGEX,
        message = "{validation.investmentCreditParameters.rateFormula.pattern}")
    String rateFormula,

    @Schema(description = "New minimum company age in years.", example = "2")
    @Min(value = 0, message = "{validation.investmentCreditParameters.minCompanyAgeYears.min}")
    Integer minCompanyAgeYears,

    @Schema(description = "New maximum ratio of requested amount to annual revenue.", example = "0.5000")
    @DecimalMin(value = "0.0", inclusive = false,
        message = "{validation.investmentCreditParameters.maxFinancingToRevenueRatio.min}")
    @DecimalMax(value = "1.0", message = "{validation.investmentCreditParameters.maxFinancingToRevenueRatio.max}")
    BigDecimal maxFinancingToRevenueRatio,

    @Schema(description = "New minimum ratio of guarantees value to requested amount.", example = "0.8000")
    @DecimalMin(value = "0.0", inclusive = false,
        message = "{validation.investmentCreditParameters.minGuaranteeCoverageRatio.min}")
    @DecimalMax(value = "1.0", message = "{validation.investmentCreditParameters.minGuaranteeCoverageRatio.max}")
    BigDecimal minGuaranteeCoverageRatio,

    @Schema(description = "New maximum ratio of total annual debt service to net annual result.",
        example = "0.4000")
    @DecimalMin(value = "0.0", inclusive = false,
        message = "{validation.investmentCreditParameters.maxDebtServiceRatio.min}")
    @DecimalMax(value = "1.0", message = "{validation.investmentCreditParameters.maxDebtServiceRatio.max}")
    BigDecimal maxDebtServiceRatio,

    @Schema(description = "New bank processing fees ratio.", example = "0.0100")
    @DecimalMin(value = "0.0", inclusive = false,
        message = "{validation.investmentCreditParameters.processingFeesRatio.min}")
    @DecimalMax(value = "1.0", message = "{validation.investmentCreditParameters.processingFeesRatio.max}")
    BigDecimal processingFeesRatio,

    @Schema(description = "New monthly insurance premium ratio (ADI/dommages).", example = "0.0010")
    @DecimalMin(value = "0.0", inclusive = false,
        message = "{validation.investmentCreditParameters.insurancePremiumRatio.min}")
    @DecimalMax(value = "1.0", message = "{validation.investmentCreditParameters.insurancePremiumRatio.max}")
    BigDecimal insurancePremiumRatio,

    @Schema(description = "Whether to set this record as the active configuration for its enterprise type.",
        example = "true")
    Boolean active
) {}
